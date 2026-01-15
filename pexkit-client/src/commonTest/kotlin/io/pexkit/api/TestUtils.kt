package io.pexkit.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

/**
 * Creates a mock engine that responds with the given JSON body.
 */
internal fun mockEngineWithResponse(
    body: String,
    statusCode: HttpStatusCode = HttpStatusCode.OK,
    rateLimitHeaders: RateLimitHeaders = RateLimitHeaders(),
): MockEngine = MockEngine {
    respond(
        content = body,
        status = statusCode,
        headers = headersOf(
            HttpHeaders.ContentType to listOf("application/json"),
            "X-Ratelimit-Limit" to listOf(rateLimitHeaders.limit.toString()),
            "X-Ratelimit-Remaining" to listOf(rateLimitHeaders.remaining.toString()),
            "X-Ratelimit-Reset" to listOf(rateLimitHeaders.reset.toString()),
        ),
    )
}

/**
 * Rate limit headers for mock responses.
 */
internal data class RateLimitHeaders(
    val limit: Int = 200,
    val remaining: Int = 199,
    val reset: Long = 1234567890L,
)

/**
 * Creates a PexKit client with a predefined response.
 */
internal fun createTestClientWithResponse(
    body: String,
    statusCode: HttpStatusCode = HttpStatusCode.OK,
    rateLimitHeaders: RateLimitHeaders = RateLimitHeaders(),
): PexKit {
    val mockEngine = mockEngineWithResponse(body, statusCode, rateLimitHeaders)
    return PexKit {
        apiKey = "test-api-key"
        httpClientEngine = mockEngine
    }
}
