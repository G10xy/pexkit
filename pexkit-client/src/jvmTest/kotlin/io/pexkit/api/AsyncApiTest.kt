package io.pexkit.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.pexkit.api.async.PexKitAsync
import io.pexkit.api.response.PexKitException
import java.util.concurrent.ExecutionException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AsyncApiTest {

    // Helper to create an async client with mock response
    private fun createAsyncClientWithResponse(
        body: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
    ): PexKitAsync {
        val mockEngine = MockEngine {
            respond(
                content = body,
                status = statusCode,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    "X-Ratelimit-Limit" to listOf("200"),
                    "X-Ratelimit-Remaining" to listOf("199"),
                    "X-Ratelimit-Reset" to listOf("1234567890"),
                ),
            )
        }
        return PexKitAsync.create {
            apiKey = "test-api-key"
            httpClientEngine = mockEngine
        }
    }

    // ===== Photos API Tests =====

    @Test
    fun `async search returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.PHOTOS_SEARCH)

        val future = client.photos.search("nature")
        val result = future.get() // Blocking wait for result

        assertEquals(1, result.data.size)
        assertEquals(MockData.photo.id, result.data.first().id)

        client.close()
    }

    @Test
    fun `async curated returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.PHOTOS_SEARCH)

        val future = client.photos.curated()
        val result = future.get()

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `async get photo returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.PHOTO)

        val future = client.photos.get(MockData.photo.id)
        val photo = future.get()

        assertEquals(MockData.photo.id, photo.id)

        client.close()
    }

    // ===== Videos API Tests =====

    @Test
    fun `async videos search returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.VIDEOS_SEARCH)

        val future = client.videos.search("ocean")
        val result = future.get()

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `async videos popular returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.VIDEOS_SEARCH)

        val future = client.videos.popular()
        val result = future.get()

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `async get video returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.VIDEO)

        val future = client.videos.get(MockData.video.id)
        val video = future.get()

        assertEquals(MockData.video.id, video.id)

        client.close()
    }

    // ===== Collections API Tests =====

    @Test
    fun `async collections featured returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.COLLECTIONS_LIST)

        val future = client.collections.featured()
        val result = future.get()

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `async collections my returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.COLLECTIONS_LIST)

        val future = client.collections.my()
        val result = future.get()

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `async collections media returns CompletableFuture that resolves`() {
        val client = createAsyncClientWithResponse(MockResponses.COLLECTION_MEDIA)

        val future = client.collections.media("abc123")
        val result = future.get()

        assertEquals(2, result.data.size)

        client.close()
    }

    // ===== Error Handling Tests =====

    @Test
    fun `async API completes exceptionally on 401 Unauthorized`() {
        val client = createAsyncClientWithResponse(
            body = MockResponses.ERROR_UNAUTHORIZED,
            statusCode = HttpStatusCode.Unauthorized,
        )

        val future = client.photos.search("nature")

        val exception = assertFailsWith<ExecutionException> {
            future.get()
        }

        assertTrue(exception.cause is PexKitException)

        client.close()
    }

    @Test
    fun `async API completes exceptionally on 404 Not Found`() {
        val client = createAsyncClientWithResponse(
            body = MockResponses.ERROR_NOT_FOUND,
            statusCode = HttpStatusCode.NotFound,
        )

        val future = client.photos.get(99999999L)

        val exception = assertFailsWith<ExecutionException> {
            future.get()
        }

        assertTrue(exception.cause is PexKitException)

        client.close()
    }

    @Test
    fun `async API completes exceptionally on 429 Rate Limited`() {
        val mockEngine = MockEngine {
            respond(
                content = """{"error": "Rate limit exceeded"}""",
                status = HttpStatusCode.TooManyRequests,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    "Retry-After" to listOf("60"),
                ),
            )
        }
        val client = PexKitAsync.create {
            apiKey = "test-api-key"
            httpClientEngine = mockEngine
        }

        val future = client.photos.search("nature")

        val exception = assertFailsWith<ExecutionException> {
            future.get()
        }

        assertTrue(exception.cause is PexKitException)

        client.close()
    }

    // ===== AutoCloseable Tests =====

    @Test
    fun `AutoCloseable works with use block`() {
        val mockEngine = MockEngine {
            respond(
                content = MockResponses.PHOTOS_SEARCH,
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    "X-Ratelimit-Limit" to listOf("200"),
                    "X-Ratelimit-Remaining" to listOf("199"),
                    "X-Ratelimit-Reset" to listOf("1234567890"),
                ),
            )
        }

        val result = PexKitAsync.create {
            apiKey = "test-api-key"
            httpClientEngine = mockEngine
        }.use { client ->
            client.photos.search("nature").get()
        }

        assertEquals(1, result.data.size)
        // Client is automatically closed after use block
    }

    @Test
    fun `client can be created with simple API key`() {
        val mockEngine = MockEngine {
            respond(
                content = MockResponses.PHOTOS_SEARCH,
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    "X-Ratelimit-Limit" to listOf("200"),
                    "X-Ratelimit-Remaining" to listOf("199"),
                    "X-Ratelimit-Reset" to listOf("1234567890"),
                ),
            )
        }

        val client = PexKitAsync.create {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        assertNotNull(client.photos)
        assertNotNull(client.videos)
        assertNotNull(client.collections)

        client.close()
    }
}
