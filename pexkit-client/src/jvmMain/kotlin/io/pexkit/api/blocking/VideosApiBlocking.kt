package io.pexkit.api.blocking

import io.pexkit.api.VideosApi
import io.pexkit.api.model.Video
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.VideoFilters
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.getOrThrow
import kotlinx.coroutines.runBlocking

/**
 * Blocking wrapper for [VideosApi] that provides Java-friendly APIs.
 *
 * All methods throw [io.pexkit.api.response.PexKitException] on errors.
 *
 * For async APIs with [java.util.concurrent.CompletableFuture], see [io.pexkit.api.async.VideosApiAsync].
 */
public class VideosApiBlocking internal constructor(
    private val delegate: VideosApi,
) {
    /**
     * Searches for videos matching the given query.
     *
     * @param query The search term (e.g., "ocean", "city traffic").
     * @param filters Optional filters for orientation, size, duration, etc.
     * @param pagination Pagination parameters (page, perPage).
     * @return Paginated videos.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @JvmOverloads
    public fun search(
        query: String,
        filters: VideoFilters = VideoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): PaginatedResponse<Video> = runBlocking {
        delegate.search(query, filters, pagination).getOrThrow()
    }

    /**
     * Returns popular videos on Pexels.
     *
     * @param filters Optional filters for dimensions and duration.
     * @param pagination Pagination parameters (page, perPage).
     * @return Paginated popular videos.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @JvmOverloads
    public fun popular(
        filters: VideoFilters = VideoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): PaginatedResponse<Video> = runBlocking {
        delegate.popular(filters, pagination).getOrThrow()
    }

    /**
     * Retrieves a specific video by its ID.
     *
     * @param id The video's unique identifier.
     * @return The video.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    public fun get(id: Long): Video = runBlocking {
        delegate.get(id).getOrThrow()
    }
}
