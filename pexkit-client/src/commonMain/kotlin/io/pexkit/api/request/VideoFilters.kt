package io.pexkit.api.request

/**
 * Filters for video search and popular videos.
 *
 * @property orientation Filter by video orientation.
 * @property size Filter by minimum video size.
 * @property locale Locale for search query interpretation.
 * @property minWidth Minimum video width in pixels.
 * @property minHeight Minimum video height in pixels.
 * @property minDuration Minimum video duration in seconds.
 * @property maxDuration Maximum video duration in seconds.
 */
public data class VideoFilters(
    val orientation: Orientation? = null,
    val size: Size? = null,
    val locale: Locale? = null,
    val minWidth: Int? = null,
    val minHeight: Int? = null,
    val minDuration: Int? = null,
    val maxDuration: Int? = null,
) {
    init {
        minWidth?.let { require(it > 0) { "minWidth must be positive" } }
        minHeight?.let { require(it > 0) { "minHeight must be positive" } }
        minDuration?.let { require(it > 0) { "minDuration must be positive" } }
        maxDuration?.let { require(it > 0) { "maxDuration must be positive" } }
        if (minDuration != null && maxDuration != null) {
            require(minDuration <= maxDuration) { "minDuration must be <= maxDuration" }
        }
    }
}
