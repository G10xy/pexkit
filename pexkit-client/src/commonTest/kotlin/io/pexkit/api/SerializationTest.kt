package io.pexkit.api

import io.pexkit.api.internal.CollectionMediaApiResponse
import io.pexkit.api.internal.CollectionsApiResponse
import io.pexkit.api.internal.PhotosApiResponse
import io.pexkit.api.internal.VideosApiResponse
import io.pexkit.api.model.Collection
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.model.Photo
import io.pexkit.api.model.Video
import io.pexkit.api.model.aspectRatio
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `deserializes Photo with all properties`() {
        val photo = json.decodeFromString<Photo>(MockResponses.PHOTO)

        assertEquals(MockData.photo.id, photo.id)
        assertEquals(MockData.photo.width, photo.width)
        assertEquals(MockData.photo.height, photo.height)
        assertEquals(MockData.photo.url, photo.url)
        assertEquals(MockData.photo.photographer, photo.photographer)
        assertEquals(MockData.photo.photographerUrl, photo.photographerUrl)
        assertEquals(MockData.photo.photographerId, photo.photographerId)
        assertEquals(MockData.photo.avgColor, photo.avgColor)
        assertEquals(MockData.photo.alt, photo.alt)
        assertEquals(MockData.photo.liked, photo.liked)

        assertNotNull(photo.src)
        assertEquals(MockData.photoSource.original, photo.src.original)
    }

    @Test
    fun `deserializes PhotosApiResponse with pagination`() {
        val response = json.decodeFromString<PhotosApiResponse>(MockResponses.PHOTOS_SEARCH)

        assertEquals(1, response.page)
        assertEquals(15, response.perPage)
        assertEquals(10000, response.totalResults)
        assertNotNull(response.nextPage)
        assertNull(response.prevPage)
        assertEquals(1, response.photos.size)

        val photo = response.photos.first()
        assertEquals(MockData.photo.id, photo.id)
    }

    @Test
    fun `deserializes Video with all properties`() {
        val video = json.decodeFromString<Video>(MockResponses.VIDEO)

        assertEquals(MockData.video.id, video.id)
        assertEquals(MockData.video.width, video.width)
        assertEquals(MockData.video.height, video.height)
        assertEquals(MockData.video.url, video.url)
        assertNotNull(video.image)
        assertNull(video.fullRes)
        assertTrue(video.tags.isEmpty())
        assertEquals(MockData.video.duration, video.duration)

        assertEquals(MockData.user.id, video.user.id)
        assertEquals(MockData.user.name, video.user.name)

        assertEquals(2, video.videoFiles.size)
        val hdFile = video.videoFiles.first()
        assertEquals(MockData.videoFileHd.quality, hdFile.quality)
        assertEquals(MockData.videoFileHd.fileType, hdFile.fileType)
        assertEquals(MockData.videoFileHd.width, hdFile.width)
        assertEquals(MockData.videoFileHd.fps, hdFile.fps)

        assertEquals(1, video.videoPictures.size)
        assertEquals(MockData.videoPicture.nr, video.videoPictures.first().nr)
    }

    @Test
    fun `deserializes VideosApiResponse with pagination`() {
        val response = json.decodeFromString<VideosApiResponse>(MockResponses.VIDEOS_SEARCH)

        assertEquals(1, response.page)
        assertEquals(15, response.perPage)
        assertEquals(5000, response.totalResults)
        assertNotNull(response.nextPage)
        assertEquals(1, response.videos.size)
    }

    @Test
    fun `deserializes Collection with all properties`() {
        val collection = json.decodeFromString<Collection>(MockResponses.COLLECTION)

        assertEquals(MockData.collection.id, collection.id)
        assertEquals(MockData.collection.title, collection.title)
        assertEquals(MockData.collection.description, collection.description)
        assertEquals(MockData.collection.private, collection.private)
        assertEquals(MockData.collection.mediaCount, collection.mediaCount)
        assertEquals(MockData.collection.photosCount, collection.photosCount)
        assertEquals(MockData.collection.videosCount, collection.videosCount)
    }

    @Test
    fun `deserializes CollectionsApiResponse with pagination`() {
        val response = json.decodeFromString<CollectionsApiResponse>(MockResponses.COLLECTIONS_LIST)

        assertEquals(1, response.page)
        assertEquals(15, response.perPage)
        assertEquals(100, response.totalResults)
        assertEquals(1, response.collections.size)

        val collection = response.collections.first()
        assertEquals(MockData.collection.id, collection.id)
    }

    @Test
    fun `deserializes CollectionMediaApiResponse with photos and videos`() {
        val response = json.decodeFromString<CollectionMediaApiResponse>(MockResponses.COLLECTION_MEDIA)

        assertEquals("abc123", response.id)
        assertEquals(1, response.page)
        assertEquals(250, response.totalResults)
        assertEquals(2, response.media.size)

        val paginatedResponse = response.toPaginatedResponse()
        assertEquals(2, paginatedResponse.data.size)

        val photoMedia = paginatedResponse.data[0]
        assertTrue(photoMedia is CollectionMedia.PhotoMedia)
        assertEquals(MockData.photo.id, photoMedia.id)
        assertEquals(CollectionMedia.Type.PHOTO, photoMedia.type)

        val videoMedia = paginatedResponse.data[1]
        assertTrue(videoMedia is CollectionMedia.VideoMedia)
        assertEquals(MockData.video.id, videoMedia.id)
        assertEquals(CollectionMedia.Type.VIDEO, videoMedia.type)
    }

    @Test
    fun `calculates Photo aspectRatio correctly`() {
        val photo = json.decodeFromString<Photo>(MockResponses.PHOTO)
        val aspectRatio = photo.aspectRatio()

        val expected = MockData.photo.width.toFloat() / MockData.photo.height
        assertEquals(expected, aspectRatio, 0.01f)
    }

    @Test
    fun `calculates Video aspectRatio correctly`() {
        val video = json.decodeFromString<Video>(MockResponses.VIDEO)
        val aspectRatio = video.aspectRatio()

        val expected = MockData.video.width.toFloat() / MockData.video.height
        assertEquals(expected, aspectRatio, 0.01f)
    }

    @Test
    fun `PaginatedResponse helpers work correctly`() {
        val response = json.decodeFromString<PhotosApiResponse>(MockResponses.PHOTOS_SEARCH)
        val paginated = response.toPaginatedResponse()

        assertTrue(paginated.hasNextPage)
        assertFalse(paginated.hasPrevPage)

        val responsePage2 = json.decodeFromString<PhotosApiResponse>(MockResponses.PHOTOS_SEARCH_PAGE_2)
        val paginatedPage2 = responsePage2.toPaginatedResponse()

        assertTrue(paginatedPage2.hasNextPage)
        assertTrue(paginatedPage2.hasPrevPage)
    }
}
