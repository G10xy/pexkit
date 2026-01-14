package io.pexkit.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a collection of media on Pexels.
 *
 * @property id Unique identifier for the collection.
 * @property title Title of the collection.
 * @property description Description of the collection, if available.
 * @property private Whether the collection is private.
 * @property mediaCount Total number of media items in the collection.
 * @property photosCount Number of photos in the collection.
 * @property videosCount Number of videos in the collection.
 */
@Serializable
public data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    val private: Boolean,
    @SerialName("media_count") val mediaCount: Int,
    @SerialName("photos_count") val photosCount: Int,
    @SerialName("videos_count") val videosCount: Int,
)
