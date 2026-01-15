package io.pexkit.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.pexkit.api.request.Orientation
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.PhotoFilters
import io.pexkit.api.request.Size
import io.pexkit.api.response.PexKitResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class PhotosApiTest {

    @Test
    fun searchPhotosReturnsSuccess() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> {
                val data = result.data
                assertEquals(1, data.data.size)
                assertEquals(MockData.photo.id, data.data.first().id)
                assertEquals(10000, data.totalResults)
                assertTrue(data.hasNextPage)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun searchPhotosIncludesRateLimitInfo() = runTest {
        val client = createTestClientWithResponse(
            body = MockResponses.PHOTOS_SEARCH,
            rateLimitHeaders = RateLimitHeaders(limit = 200, remaining = 150, reset = 1234567890L),
        )

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> {
                assertEquals(200, result.rateLimit.limit)
                assertEquals(150, result.rateLimit.remaining)
                assertEquals(1234567890L, result.rateLimit.reset)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun searchPhotosWithFilters() = runTest {
        var capturedUrl: String? = null

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = MockResponses.PHOTOS_SEARCH,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val client = PexKit {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        client.photos.search(
            query = "nature",
            filters = PhotoFilters(
                orientation = Orientation.LANDSCAPE,
                size = Size.LARGE,
                color = "FF5733",
            ),
            pagination = PaginationParams(page = 2, perPage = 30),
        )

        assertNotNull(capturedUrl)
        assertTrue(capturedUrl!!.contains("query=nature"))
        assertTrue(capturedUrl!!.contains("orientation=landscape"))
        assertTrue(capturedUrl!!.contains("size=large"))
        assertTrue(capturedUrl!!.contains("color=FF5733"))
        assertTrue(capturedUrl!!.contains("page=2"))
        assertTrue(capturedUrl!!.contains("per_page=30"))

        client.close()
    }

    @Test
    fun curatedPhotosReturnsSuccess() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        when (val result = client.photos.curated()) {
            is PexKitResult.Success -> {
                assertEquals(1, result.data.data.size)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun getPhotoByIdReturnsSuccess() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTO)

        when (val result = client.photos.get(MockData.photo.id)) {
            is PexKitResult.Success -> {
                assertEquals(MockData.photo.id, result.data.id)
                assertEquals(MockData.photo.photographer, result.data.photographer)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun searchPhotosIncludesAuthorizationHeader() = runTest {
        var authHeader: String? = null

        val mockEngine = MockEngine { request ->
            authHeader = request.headers["Authorization"]
            respond(
                content = MockResponses.PHOTOS_SEARCH,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val client = PexKit {
            apiKey = "my-secret-api-key"
            httpClientEngine = mockEngine
        }

        client.photos.search("nature")

        assertEquals("my-secret-api-key", authHeader)

        client.close()
    }
}
