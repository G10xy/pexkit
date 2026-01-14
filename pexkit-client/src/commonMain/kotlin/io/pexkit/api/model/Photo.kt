package io.pexkit.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a photo from the Pexels API.
 *
 * @property id Unique identifier for the photo.
 * @property width Original width in pixels.
 * @property height Original height in pixels.
 * @property url Pexels page URL for the photo.
 * @property photographer Name of the photographer.
 * @property photographerUrl Pexels profile URL of the photographer.
 * @property photographerId Unique identifier for the photographer.
 * @property avgColor Average color of the photo as a hex string (e.g., "#978E82").
 * @property src Available image sizes.
 * @property alt Alt text description of the photo.
 * @property liked Whether the photo is liked by the API key owner.
 */
@Serializable
public data class Photo(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerialName("photographer_url") val photographerUrl: String,
    @SerialName("photographer_id") val photographerId: Long,
    @SerialName("avg_color") val avgColor: String,
    val src: PhotoSource,
    val alt: String,
    val liked: Boolean = false,
)

/**
 * Calculates the aspect ratio of the photo.
 *
 * @return Width divided by height as a float.
 */
public fun Photo.aspectRatio(): Float = width.toFloat() / height
