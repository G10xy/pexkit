package io.pexkit.api.internal

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.pexkit.api.response.PexKitError
import io.pexkit.api.response.PexKitResult
import io.pexkit.api.response.RateLimitInfo

/**
 * Executes API requests and handles response/error parsing.
 */
internal class ApiExecutor(private val client: HttpClient) {

    /**
     * Executes a GET request and returns a typed result.
     */
    suspend inline fun <reified T> get(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): PexKitResult<T> {
        return executeRequest {
            client.get(url) { block() }
        }
    }

    /**
     * Executes a request and handles response parsing and error conversion.
     */
    suspend inline fun <reified T> executeRequest(
        request: () -> HttpResponse,
    ): PexKitResult<T> {
        return try {
            val response = request()
            val rateLimit = extractRateLimitInfo(response)

            if (response.status.isSuccess()) {
                val data: T = response.body()
                PexKitResult.Success(data, rateLimit)
            } else {
                PexKitResult.Failure(parseError(response))
            }
        } catch (e: Exception) {
            PexKitResult.Failure(PexKitError.NetworkError(e))
        }
    }

    /**
     * Extracts rate limit information from response headers.
     */
    fun extractRateLimitInfo(response: HttpResponse): RateLimitInfo {
        val headers = response.headers
        return RateLimitInfo(
            limit = headers["X-Ratelimit-Limit"]?.toIntOrNull() ?: 0,
            remaining = headers["X-Ratelimit-Remaining"]?.toIntOrNull() ?: 0,
            reset = headers["X-Ratelimit-Reset"]?.toLongOrNull() ?: 0L,
        )
    }

    /**
     * Parses an error response into a [PexKitError].
     */
    suspend fun parseError(response: HttpResponse): PexKitError {
        val statusCode = response.status.value
        val body = try {
            response.bodyAsText()
        } catch (_: Exception) {
            null
        }

        return when (response.status) {
            HttpStatusCode.Unauthorized -> PexKitError.Unauthorized()
            HttpStatusCode.Forbidden -> PexKitError.Forbidden()
            HttpStatusCode.NotFound -> PexKitError.NotFound(response.call.request.url.toString())
            HttpStatusCode.TooManyRequests -> {
                val retryAfter = response.headers["Retry-After"]?.toIntOrNull()
                PexKitError.RateLimited(retryAfter)
            }
            else -> {
                if (statusCode in 500..599) {
                    PexKitError.ServerError(statusCode)
                } else {
                    PexKitError.Unknown(statusCode, body)
                }
            }
        }
    }
}
