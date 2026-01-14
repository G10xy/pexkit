package io.pexkit.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a video file variant with specific quality and resolution.
 *
 * @property id Unique identifier for this file variant.
 * @property quality Quality level (e.g., "hd", "sd", "hls").
 * @property fileType MIME type (e.g., "video/mp4").
 * @property width Video width in pixels.
 * @property height Video height in pixels.
 * @property fps Frames per second.
 * @property link Direct URL to download/stream the video.
 */
@Serializable
public data class VideoFile(
    val id: Long,
    val quality: String,
    @SerialName("file_type") val fileType: String,
    val width: Int?,
    val height: Int?,
    val fps: Double?,
    val link: String,
)
