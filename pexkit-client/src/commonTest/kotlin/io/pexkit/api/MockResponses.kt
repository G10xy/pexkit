package io.pexkit.api

import io.pexkit.api.internal.CollectionMediaApiResponse
import io.pexkit.api.internal.CollectionMediaItem
import io.pexkit.api.internal.CollectionsApiResponse
import io.pexkit.api.internal.PhotoSourceInternal
import io.pexkit.api.internal.PhotosApiResponse
import io.pexkit.api.internal.UserInternal
import io.pexkit.api.internal.VideoFileInternal
import io.pexkit.api.internal.VideoPictureInternal
import io.pexkit.api.internal.VideosApiResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Mock JSON responses for HTTP mock testing.
 * Generated from MockData objects to ensure type safety.
 */
internal object MockResponses {

    private val json = Json { prettyPrint = false }

    // Single photo JSON
    val PHOTO: String = json.encodeToString(MockData.photo)

    // Photos search response
    val PHOTOS_SEARCH: String = json.encodeToString(
        PhotosApiResponse(
            photos = listOf(MockData.photo),
            page = 1,
            perPage = 15,
            totalResults = 10000,
            nextPage = "https://api.pexels.com/v1/search?page=2&query=nature",
            prevPage = null,
        )
    )

    // Photos search page 2 response
    val PHOTOS_SEARCH_PAGE_2: String = json.encodeToString(
        PhotosApiResponse(
            photos = listOf(MockData.photoPage2),
            page = 2,
            perPage = 15,
            totalResults = 10000,
            nextPage = "https://api.pexels.com/v1/search?page=3&query=nature",
            prevPage = "https://api.pexels.com/v1/search?page=1&query=nature",
        )
    )

    // Single video JSON
    val VIDEO: String = json.encodeToString(MockData.video)

    // Videos search response
    val VIDEOS_SEARCH: String = json.encodeToString(
        VideosApiResponse(
            videos = listOf(MockData.video),
            page = 1,
            perPage = 15,
            totalResults = 5000,
            nextPage = "https://api.pexels.com/videos/search?page=2&query=ocean",
            prevPage = null,
        )
    )

    // Single collection JSON
    val COLLECTION: String = json.encodeToString(MockData.collection)

    // Collections list response
    val COLLECTIONS_LIST: String = json.encodeToString(
        CollectionsApiResponse(
            collections = listOf(MockData.collection),
            page = 1,
            perPage = 15,
            totalResults = 100,
            nextPage = "https://api.pexels.com/v1/collections/featured?page=2",
            prevPage = null,
        )
    )

    // Collection media response (mixed photos and videos)
    val COLLECTION_MEDIA: String = json.encodeToString(
        CollectionMediaApiResponse(
            id = "abc123",
            media = listOf(
                CollectionMediaItem(
                    type = "Photo",
                    id = MockData.photo.id,
                    width = MockData.photo.width,
                    height = MockData.photo.height,
                    url = MockData.photo.url,
                    photographer = MockData.photo.photographer,
                    photographerUrl = MockData.photo.photographerUrl,
                    photographerId = MockData.photo.photographerId,
                    avgColor = MockData.photo.avgColor,
                    src = PhotoSourceInternal(
                        original = MockData.photoSource.original,
                        large2x = MockData.photoSource.large2x,
                        large = MockData.photoSource.large,
                        medium = MockData.photoSource.medium,
                        small = MockData.photoSource.small,
                        portrait = MockData.photoSource.portrait,
                        landscape = MockData.photoSource.landscape,
                        tiny = MockData.photoSource.tiny,
                    ),
                    alt = MockData.photo.alt,
                    liked = MockData.photo.liked,
                ),
                CollectionMediaItem(
                    type = "Video",
                    id = MockData.video.id,
                    width = MockData.video.width,
                    height = MockData.video.height,
                    url = MockData.video.url,
                    image = MockData.video.image,
                    fullRes = MockData.video.fullRes,
                    tags = MockData.video.tags,
                    duration = MockData.video.duration,
                    user = UserInternal(
                        id = MockData.user.id,
                        name = MockData.user.name,
                        url = MockData.user.url,
                    ),
                    videoFiles = listOf(
                        VideoFileInternal(
                            id = MockData.videoFileHd.id,
                            quality = MockData.videoFileHd.quality,
                            fileType = MockData.videoFileHd.fileType,
                            width = MockData.videoFileHd.width,
                            height = MockData.videoFileHd.height,
                            fps = MockData.videoFileHd.fps,
                            link = MockData.videoFileHd.link,
                        ),
                    ),
                    videoPictures = listOf(
                        VideoPictureInternal(
                            id = MockData.videoPicture.id,
                            picture = MockData.videoPicture.picture,
                            nr = MockData.videoPicture.nr,
                        ),
                    ),
                ),
            ),
            page = 1,
            perPage = 15,
            totalResults = 250,
            nextPage = "https://api.pexels.com/v1/collections/abc123?page=2",
            prevPage = null,
        )
    )

    // Error responses
    const val ERROR_UNAUTHORIZED = """{"error": "Unauthorized"}"""
    const val ERROR_NOT_FOUND = """{"error": "Not Found"}"""
}
