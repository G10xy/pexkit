package io.pexkit.api.model

import kotlinx.serialization.Serializable

/**
 * Represents a preview picture/thumbnail for a video.
 *
 * @property id Unique identifier for the picture.
 * @property picture URL of the preview image.
 * @property nr Index/number of this picture in the sequence.
 */
@Serializable
public data class VideoPicture(
    val id: Long,
    val picture: String,
    val nr: Int,
)
