package io.pexkit.api.internal

/**
 * Pexels API endpoints.
 */
internal object Endpoints {
    private const val PHOTOS_BASE = "https://api.pexels.com/v1"
    private const val VIDEOS_BASE = "https://api.pexels.com/videos"

    // Photos
    const val PHOTOS_SEARCH = "$PHOTOS_BASE/search"
    const val PHOTOS_CURATED = "$PHOTOS_BASE/curated"
    fun photo(id: Long): String = "$PHOTOS_BASE/photos/$id"

    // Videos
    const val VIDEOS_SEARCH = "$VIDEOS_BASE/search"
    const val VIDEOS_POPULAR = "$VIDEOS_BASE/popular"
    fun video(id: Long): String = "$VIDEOS_BASE/videos/$id"

    // Collections
    const val COLLECTIONS_FEATURED = "$PHOTOS_BASE/collections/featured"
    const val COLLECTIONS_MY = "$PHOTOS_BASE/collections"
    fun collectionMedia(id: String): String = "$PHOTOS_BASE/collections/$id"
}
