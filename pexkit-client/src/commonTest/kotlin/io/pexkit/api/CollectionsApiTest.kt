package io.pexkit.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.pexkit.api.internal.CollectionMediaApiResponse
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.model.asPhoto
import io.pexkit.api.model.asUnknown
import io.pexkit.api.model.asVideo
import io.pexkit.api.request.MediaType
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.response.PexKitResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class CollectionsApiTest {

    @Test
    fun featuredCollectionsReturnsSuccess() = runTest {
        val client = createTestClientWithResponse(MockResponses.COLLECTIONS_LIST)

        when (val result = client.collections.featured()) {
            is PexKitResult.Success -> {
                val data = result.data
                assertEquals(1, data.data.size)
                assertEquals(MockData.collection.id, data.data.first().id)
                assertEquals(MockData.collection.title, data.data.first().title)
                assertEquals(100, data.totalResults)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun myCollectionsReturnsSuccess() = runTest {
        val client = createTestClientWithResponse(MockResponses.COLLECTIONS_LIST)

        when (val result = client.collections.my()) {
            is PexKitResult.Success -> {
                assertEquals(1, result.data.data.size)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun collectionMediaReturnsSuccess() = runTest {
        val client = createTestClientWithResponse(MockResponses.COLLECTION_MEDIA)

        when (val result = client.collections.media(MockData.collection.id)) {
            is PexKitResult.Success -> {
                assertEquals(2, result.data.data.size)
                assertEquals(250, result.data.totalResults)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun collectionMediaContainsPhotosAndVideos() = runTest {
        val client = createTestClientWithResponse(MockResponses.COLLECTION_MEDIA)

        when (val result = client.collections.media(MockData.collection.id)) {
            is PexKitResult.Success -> {
                val media = result.data.data

                val photo = media[0]
                assertIs<CollectionMedia.PhotoMedia>(photo)
                assertEquals(MockData.photo.id, photo.id)
                assertEquals(CollectionMedia.Type.PHOTO, photo.type)
                assertEquals(MockData.photo.photographer, photo.photographer)

                val video = media[1]
                assertIs<CollectionMedia.VideoMedia>(video)
                assertEquals(MockData.video.id, video.id)
                assertEquals(CollectionMedia.Type.VIDEO, video.type)
                assertEquals(MockData.user.name, video.user.name)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun collectionMediaWithTypeFilter() = runTest {
        var capturedUrl: String? = null

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = MockResponses.COLLECTION_MEDIA,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val client = PexKit {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        client.collections.media(MockData.collection.id, type = MediaType.PHOTOS)

        assertNotNull(capturedUrl)
        assertTrue(capturedUrl!!.contains("type=photos"))

        client.close()
    }

    @Test
    fun collectionMediaWithPagination() = runTest {
        var capturedUrl: String? = null

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = MockResponses.COLLECTION_MEDIA,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val client = PexKit {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        client.collections.media(
            id = MockData.collection.id,
            pagination = PaginationParams(page = 3, perPage = 50),
        )

        assertNotNull(capturedUrl)
        assertTrue(capturedUrl!!.contains("page=3"))
        assertTrue(capturedUrl!!.contains("per_page=50"))

        client.close()
    }

    @Test
    fun collectionDataIsCorrect() = runTest {
        val client = createTestClientWithResponse(MockResponses.COLLECTIONS_LIST)

        when (val result = client.collections.featured()) {
            is PexKitResult.Success -> {
                val collection = result.data.data.first()
                assertEquals(MockData.collection.id, collection.id)
                assertEquals(MockData.collection.title, collection.title)
                assertEquals(MockData.collection.description, collection.description)
                assertEquals(MockData.collection.private, collection.private)
                assertEquals(MockData.collection.mediaCount, collection.mediaCount)
                assertEquals(MockData.collection.photosCount, collection.photosCount)
                assertEquals(MockData.collection.videosCount, collection.videosCount)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }


    @Test
    fun unknownMediaTypeIsPreserved() {
        val json = Json { ignoreUnknownKeys = true }
        val response = json.decodeFromString<CollectionMediaApiResponse>(
            MockResponses.COLLECTION_MEDIA_WITH_UNKNOWN
        )

        val paginatedResponse = response.toPaginatedResponse()
        assertEquals(3, paginatedResponse.data.size)

        // First item is Photo
        val photo = paginatedResponse.data[0]
        assertIs<CollectionMedia.PhotoMedia>(photo)
        assertEquals(CollectionMedia.Type.PHOTO, photo.type)

        // Second item is Video
        val video = paginatedResponse.data[1]
        assertIs<CollectionMedia.VideoMedia>(video)
        assertEquals(CollectionMedia.Type.VIDEO, video.type)

        // Third item is Unknown
        val unknown = paginatedResponse.data[2]
        assertIs<CollectionMedia.Unknown>(unknown)
        assertEquals(CollectionMedia.Type.UNKNOWN, unknown.type)
        assertEquals("Audio", unknown.originalType)
        assertEquals(999L, unknown.id)
    }

    @Test
    fun unknownMediaTypeHasCorrectProperties() {
        val unknown = CollectionMedia.Unknown(
            id = 123L,
            width = 800,
            height = 600,
            url = "https://example.com/media/123",
            originalType = "Podcast",
        )

        assertEquals(123L, unknown.id)
        assertEquals(800, unknown.width)
        assertEquals(600, unknown.height)
        assertEquals("https://example.com/media/123", unknown.url)
        assertEquals("Podcast", unknown.originalType)
        assertEquals(CollectionMedia.Type.UNKNOWN, unknown.type)
    }

    @Test
    fun asUnknownExtensionReturnsCorrectType() {
        val unknown: CollectionMedia = CollectionMedia.Unknown(
            id = 1L,
            width = 100,
            height = 100,
            url = "https://example.com",
            originalType = "NewType",
        )

        assertNotNull(unknown.asUnknown())
        assertNull(unknown.asPhoto())
        assertNull(unknown.asVideo())
    }

}
