package io.pexkit.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a video from the Pexels API.
 *
 * @property id Unique identifier for the video.
 * @property width Video width in pixels.
 * @property height Video height in pixels.
 * @property url Pexels page URL for the video.
 * @property image Thumbnail/preview image URL.
 * @property fullRes Full resolution video URL, if available.
 * @property tags List of tags associated with the video.
 * @property duration Duration of the video in seconds.
 * @property user The videographer who uploaded this video.
 * @property videoFiles Available video file variants.
 * @property videoPictures Preview pictures for the video.
 */
@Serializable
public data class Video(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    @SerialName("full_res") val fullRes: String?,
    val tags: List<String> = emptyList(),
    val duration: Int,
    val user: User,
    @SerialName("video_files") val videoFiles: List<VideoFile>,
    @SerialName("video_pictures") val videoPictures: List<VideoPicture>,
)

/**
 * Calculates the aspect ratio of the video.
 *
 * @return Width divided by height as a float.
 */
public fun Video.aspectRatio(): Float = width.toFloat() / height
