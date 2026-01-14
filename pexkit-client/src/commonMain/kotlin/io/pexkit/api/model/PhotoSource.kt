package io.pexkit.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Available image sizes for a photo.
 *
 * Each photo from Pexels comes with multiple pre-generated sizes
 * optimized for different use cases.
 */
@Serializable
public data class PhotoSource(
    /** Original full-resolution image. */
    val original: String,
    /** Extra large size (2x DPI, ~940x650). */
    @SerialName("large2x") val large2x: String,
    /** Large size (~940x650). */
    val large: String,
    /** Medium size (~350px height). */
    val medium: String,
    /** Small size (~130px height). */
    val small: String,
    /** Portrait crop (~800x1200). */
    val portrait: String,
    /** Landscape crop (~1200x627). */
    val landscape: String,
    /** Tiny thumbnail (~280x200). */
    val tiny: String,
)
