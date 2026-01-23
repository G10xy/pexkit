package io.pexkit.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.pexkit.api.blocking.CollectionsApiBlocking
import io.pexkit.api.blocking.PexKitBlocking
import io.pexkit.api.blocking.PhotosApiBlocking
import io.pexkit.api.blocking.VideosApiBlocking
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.PhotoFilters
import io.pexkit.api.request.VideoFilters
import io.pexkit.api.response.PexKitException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BlockingApiTest {

    private fun createBlockingClientWithResponse(
        body: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
    ): PexKitBlocking {
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
        return PexKitBlocking.create {
            apiKey = "test-api-key"
            httpClientEngine = mockEngine
        }
    }


    @Test
    fun `blocking search returns photos`() {
        val client = createBlockingClientWithResponse(MockResponses.PHOTOS_SEARCH)

        val result = client.photos.search("nature")

        assertEquals(1, result.data.size)
        assertEquals(MockData.photo.id, result.data.first().id)
        assertEquals(10000, result.totalResults)
        assertTrue(result.hasNextPage)

        client.close()
    }

    @Test
    fun `blocking search with filters works`() {
        val client = createBlockingClientWithResponse(MockResponses.PHOTOS_SEARCH)

        val result = client.photos.search(
            query = "nature",
            filters = PhotoFilters(),
            pagination = PaginationParams(page = 1, perPage = 15),
        )

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `blocking curated returns photos`() {
        val client = createBlockingClientWithResponse(MockResponses.PHOTOS_SEARCH)

        val result = client.photos.curated()

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `blocking get photo by id returns photo`() {
        val client = createBlockingClientWithResponse(MockResponses.PHOTO)

        val photo = client.photos.get(MockData.photo.id)

        assertEquals(MockData.photo.id, photo.id)
        assertEquals(MockData.photo.photographer, photo.photographer)

        client.close()
    }

    @Test
    fun `blocking videos search returns videos`() {
        val client = createBlockingClientWithResponse(MockResponses.VIDEOS_SEARCH)

        val result = client.videos.search("ocean")

        assertEquals(1, result.data.size)
        assertEquals(MockData.video.id, result.data.first().id)

        client.close()
    }

    @Test
    fun `blocking videos popular returns videos`() {
        val client = createBlockingClientWithResponse(MockResponses.VIDEOS_SEARCH)

        val result = client.videos.popular(
            filters = VideoFilters(),
            pagination = PaginationParams(),
        )

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `blocking get video by id returns video`() {
        val client = createBlockingClientWithResponse(MockResponses.VIDEO)

        val video = client.videos.get(MockData.video.id)

        assertEquals(MockData.video.id, video.id)
        assertEquals(MockData.video.duration, video.duration)

        client.close()
    }

    @Test
    fun `blocking collections featured returns collections`() {
        val client = createBlockingClientWithResponse(MockResponses.COLLECTIONS_LIST)

        val result = client.collections.featured()

        assertEquals(1, result.data.size)
        assertEquals(MockData.collection.id, result.data.first().id)

        client.close()
    }

    @Test
    fun `blocking collections my returns collections`() {
        val client = createBlockingClientWithResponse(MockResponses.COLLECTIONS_LIST)

        val result = client.collections.my()

        assertEquals(1, result.data.size)

        client.close()
    }

    @Test
    fun `blocking collections media returns media`() {
        val client = createBlockingClientWithResponse(MockResponses.COLLECTION_MEDIA)

        val result = client.collections.media("abc123")

        assertEquals(2, result.data.size)

        client.close()
    }

    @Test
    fun `blocking API throws PexKitException on 404 Not Found`() {
        val client = createBlockingClientWithResponse(
            body = MockResponses.ERROR_NOT_FOUND,
            statusCode = HttpStatusCode.NotFound,
        )

        val exception = assertFailsWith<PexKitException> {
            client.photos.get(99999999L)
        }

        assertNotNull(exception.error)

        client.close()
    }

    @Test
    fun `blocking API throws PexKitException on 429 Rate Limited`() {
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
        val client = PexKitBlocking.create {
            apiKey = "test-api-key"
            httpClientEngine = mockEngine
        }

        val exception = assertFailsWith<PexKitException> {
            client.photos.search("nature")
        }

        assertNotNull(exception.error)

        client.close()
    }


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

        val result = PexKitBlocking.create {
            apiKey = "test-api-key"
            httpClientEngine = mockEngine
        }.use { client ->
            client.photos.search("nature")
        }

        assertEquals(1, result.data.size)
    }

    @Test
    fun `client can be created with simple API key`() {
        // This test just verifies the factory method works
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

        val client = PexKitBlocking.create {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        assertNotNull(client.photos)
        assertNotNull(client.videos)
        assertNotNull(client.collections)

        client.close()
    }

}
