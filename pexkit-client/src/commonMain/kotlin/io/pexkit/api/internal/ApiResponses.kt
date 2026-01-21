package io.pexkit.api.internal

import io.pexkit.api.model.Collection
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.model.Photo
import io.pexkit.api.model.PhotoSource
import io.pexkit.api.model.User
import io.pexkit.api.model.Video
import io.pexkit.api.model.VideoFile
import io.pexkit.api.model.VideoPicture
import io.pexkit.api.response.PaginatedResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Raw API response for photo list endpoints.
 */
@Serializable
internal data class PhotosApiResponse(
    val photos: List<Photo>,
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("next_page") val nextPage: String? = null,
    @SerialName("prev_page") val prevPage: String? = null,
) {
    fun toPaginatedResponse(): PaginatedResponse<Photo> = PaginatedResponse(
        data = photos,
        page = page,
        perPage = perPage,
        totalResults = totalResults,
        nextPage = nextPage,
        prevPage = prevPage,
    )
}

/**
 * Raw API response for video list endpoints.
 */
@Serializable
internal data class VideosApiResponse(
    val videos: List<Video>,
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("next_page") val nextPage: String? = null,
    @SerialName("prev_page") val prevPage: String? = null,
) {
    fun toPaginatedResponse(): PaginatedResponse<Video> = PaginatedResponse(
        data = videos,
        page = page,
        perPage = perPage,
        totalResults = totalResults,
        nextPage = nextPage,
        prevPage = prevPage,
    )
}

/**
 * Raw API response for collection list endpoints.
 */
@Serializable
internal data class CollectionsApiResponse(
    val collections: List<Collection>,
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("next_page") val nextPage: String? = null,
    @SerialName("prev_page") val prevPage: String? = null,
) {
    fun toPaginatedResponse(): PaginatedResponse<Collection> = PaginatedResponse(
        data = collections,
        page = page,
        perPage = perPage,
        totalResults = totalResults,
        nextPage = nextPage,
        prevPage = prevPage,
    )
}

/**
 * Raw API response for collection media endpoint.
 * Contains mixed media (photos and videos).
 */
@Serializable
internal data class CollectionMediaApiResponse(
    val media: List<CollectionMediaItem>,
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("next_page") val nextPage: String? = null,
    @SerialName("prev_page") val prevPage: String? = null,
    val id: String,
) {
    fun toPaginatedResponse(): PaginatedResponse<CollectionMedia> = PaginatedResponse(
        data = media.mapNotNull { it.toCollectionMedia() },
        page = page,
        perPage = perPage,
        totalResults = totalResults,
        nextPage = nextPage,
        prevPage = prevPage,
    )
}

/**
 * A media item in a collection (can be photo or video).
 */
@Serializable
internal data class CollectionMediaItem(
    val type: String,
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    // Photo-specific fields
    val photographer: String? = null,
    @SerialName("photographer_url") val photographerUrl: String? = null,
    @SerialName("photographer_id") val photographerId: Long? = null,
    @SerialName("avg_color") val avgColor: String? = null,
    val src: PhotoSourceInternal? = null,
    val alt: String? = null,
    val liked: Boolean? = null,
    // Video-specific fields
    val image: String? = null,
    @SerialName("full_res") val fullRes: String? = null,
    val tags: List<String>? = null,
    val duration: Int? = null,
    val user: UserInternal? = null,
    @SerialName("video_files") val videoFiles: List<VideoFileInternal>? = null,
    @SerialName("video_pictures") val videoPictures: List<VideoPictureInternal>? = null,
)

@Serializable
internal data class PhotoSourceInternal(
    val original: String,
    @SerialName("large2x") val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String,
)

@Serializable
internal data class UserInternal(
    val id: Long,
    val name: String,
    val url: String,
)

@Serializable
internal data class VideoFileInternal(
    val id: Long,
    val quality: String,
    @SerialName("file_type") val fileType: String,
    val width: Int?,
    val height: Int?,
    val fps: Double?,
    val link: String,
)

@Serializable
internal data class VideoPictureInternal(
    val id: Long,
    val picture: String,
    val nr: Int,
)

/**
 * Converts a raw API media item to the public CollectionMedia type.
 * Returns [CollectionMedia.Unknown] for unrecognized media types instead of null.
 */
internal fun CollectionMediaItem.toCollectionMedia(): CollectionMedia? {
    return when (type) {
        "Photo" -> CollectionMedia.PhotoMedia(
            id = id,
            width = width,
            height = height,
            url = url,
            photographer = photographer ?: "",
            photographerUrl = photographerUrl ?: "",
            photographerId = photographerId ?: 0,
            avgColor = avgColor ?: "",
            src = src?.toPhotoSource() ?: return null,
            alt = alt ?: "",
            liked = liked ?: false,
        )
        "Video" -> CollectionMedia.VideoMedia(
            id = id,
            width = width,
            height = height,
            url = url,
            image = image ?: "",
            fullRes = fullRes,
            tags = tags ?: emptyList(),
            duration = duration ?: 0,
            user = user?.toUser() ?: return null,
            videoFiles = videoFiles?.map { it.toVideoFile() } ?: emptyList(),
            videoPictures = videoPictures?.map { it.toVideoPicture() } ?: emptyList(),
        )
        else -> CollectionMedia.Unknown(
            id = id,
            width = width,
            height = height,
            url = url,
            originalType = type,
        )
    }
}

internal fun PhotoSourceInternal.toPhotoSource(): PhotoSource = PhotoSource(
    original = original,
    large2x = large2x,
    large = large,
    medium = medium,
    small = small,
    portrait = portrait,
    landscape = landscape,
    tiny = tiny,
)

internal fun UserInternal.toUser(): User = User(
    id = id,
    name = name,
    url = url,
)

internal fun VideoFileInternal.toVideoFile(): VideoFile = VideoFile(
    id = id,
    quality = quality,
    fileType = fileType,
    width = width,
    height = height,
    fps = fps,
    link = link,
)

internal fun VideoPictureInternal.toVideoPicture(): VideoPicture = VideoPicture(
    id = id,
    picture = picture,
    nr = nr,
)
