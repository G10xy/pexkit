package io.pexkit.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.pexkit.api.response.PexKitError
import io.pexkit.api.response.PexKitException
import io.pexkit.api.response.PexKitResult
import io.pexkit.api.response.getOrNull
import io.pexkit.api.response.getOrThrow
import io.pexkit.api.response.onFailure
import io.pexkit.api.response.onSuccess
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class ErrorHandlingTest {

    @Test
    fun unauthorizedError() = runTest {
        val client = createTestClientWithResponse(
            body = MockResponses.ERROR_UNAUTHORIZED,
            statusCode = HttpStatusCode.Unauthorized,
        )

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                assertIs<PexKitError.Unauthorized>(result.error)
                assertTrue(result.error.message.contains("Invalid or missing API key"))
            }
        }

        client.close()
    }

    @Test
    fun forbiddenError() = runTest {
        val client = createTestClientWithResponse(
            body = "{}",
            statusCode = HttpStatusCode.Forbidden,
        )

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                assertIs<PexKitError.Forbidden>(result.error)
            }
        }

        client.close()
    }

    @Test
    fun notFoundError() = runTest {
        val client = createTestClientWithResponse(
            body = MockResponses.ERROR_NOT_FOUND,
            statusCode = HttpStatusCode.NotFound,
        )

        when (val result = client.photos.get(999999999L)) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                assertIs<PexKitError.NotFound>(result.error)
                assertTrue(result.error.message.contains("not found"))
            }
        }

        client.close()
    }

    @Test
    fun rateLimitedError() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "{}",
                status = HttpStatusCode.TooManyRequests,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    "Retry-After" to listOf("60"),
                ),
            )
        }

        val client = PexKit {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                val error = result.error
                assertIs<PexKitError.RateLimited>(error)
                assertEquals(60, error.retryAfter)
                assertTrue(error.message.contains("Retry after 60 seconds"))
            }
        }

        client.close()
    }

    @Test
    fun rateLimitedErrorWithoutRetryAfter() = runTest {
        val client = createTestClientWithResponse(
            body = "{}",
            statusCode = HttpStatusCode.TooManyRequests,
        )

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                val error = result.error
                assertIs<PexKitError.RateLimited>(error)
                assertNull(error.retryAfter)
            }
        }

        client.close()
    }

    @Test
    fun serverError500() = runTest {
        val client = createTestClientWithResponse(
            body = "Internal Server Error",
            statusCode = HttpStatusCode.InternalServerError,
        )

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                val error = result.error
                assertIs<PexKitError.ServerError>(error)
                assertEquals(500, error.statusCode)
            }
        }

        client.close()
    }

    @Test
    fun serverError503() = runTest {
        val client = createTestClientWithResponse(
            body = "Service Unavailable",
            statusCode = HttpStatusCode.ServiceUnavailable,
        )

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                val error = result.error
                assertIs<PexKitError.ServerError>(error)
                assertEquals(503, error.statusCode)
            }
        }

        client.close()
    }

    @Test
    fun unknownError() = runTest {
        val client = createTestClientWithResponse(
            body = "Bad Request: Invalid parameter",
            statusCode = HttpStatusCode.BadRequest,
        )

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> fail("Expected failure")
            is PexKitResult.Failure -> {
                val error = result.error
                assertIs<PexKitError.Unknown>(error)
                assertEquals(400, error.statusCode)
            }
        }

        client.close()
    }

    @Test
    fun getOrNullReturnsNullOnFailure() = runTest {
        val client = createTestClientWithResponse(
            body = "{}",
            statusCode = HttpStatusCode.Unauthorized,
        )

        val result = client.photos.search("nature")
        val data = result.getOrNull()

        assertNull(data)

        client.close()
    }

    @Test
    fun getOrNullReturnsDataOnSuccess() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        val result = client.photos.search("nature")
        val data = result.getOrNull()

        assertEquals(1, data?.data?.size)

        client.close()
    }

    @Test
    fun getOrThrowThrowsOnFailure() = runTest {
        val client = createTestClientWithResponse(
            body = "{}",
            statusCode = HttpStatusCode.Unauthorized,
        )

        val result = client.photos.search("nature")

        assertFailsWith<PexKitException> {
            result.getOrThrow()
        }

        client.close()
    }

    @Test
    fun onSuccessCallbackExecuted() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        var successCalled = false
        val result = client.photos.search("nature")
        result.onSuccess { successCalled = true }

        assertTrue(successCalled)

        client.close()
    }

    @Test
    fun onFailureCallbackExecuted() = runTest {
        val client = createTestClientWithResponse(
            body = "{}",
            statusCode = HttpStatusCode.Unauthorized,
        )

        var failureCalled = false
        var capturedError: PexKitError? = null

        val result = client.photos.search("nature")
        result.onFailure {
            failureCalled = true
            capturedError = it
        }

        assertTrue(failureCalled)
        assertIs<PexKitError.Unauthorized>(capturedError)

        client.close()
    }
}
