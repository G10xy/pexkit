package io.pexkit.api

import io.ktor.client.request.parameter
import io.pexkit.api.internal.ApiExecutor
import io.pexkit.api.internal.Endpoints
import io.pexkit.api.internal.VideosApiResponse
import io.pexkit.api.model.Video
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.VideoFilters
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.PexKitResult
import io.pexkit.api.response.map

/**
 * API for searching and retrieving videos from Pexels.
 *
 * Access via [PexKit.videos]:
 * ```kotlin
 * val result = pexkit.videos.search("ocean")
 * ```
 */
public class VideosApi internal constructor(
    private val executor: ApiExecutor,
    private val defaultPerPage: Int,
) {
    /**
     * Searches for videos matching the given query.
     *
     * @param query The search term (e.g., "ocean", "city traffic").
     * @param filters Optional filters for orientation, size, duration, etc.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [PexKitResult] containing paginated videos or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#videos-search)
     */
    public suspend fun search(
        query: String,
        filters: VideoFilters = VideoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): PexKitResult<PaginatedResponse<Video>> {
        require(query.isNotBlank()) { "Search query cannot be blank" }
        require(query.length <= 200) { "Search query cannot exceed 200 characters" }

        return executor.get<VideosApiResponse>(Endpoints.VIDEOS_SEARCH) {
            parameter("query", query)
            parameter("page", pagination.page)
            parameter("per_page", pagination.perPage ?: defaultPerPage)
            filters.orientation?.let { parameter("orientation", it.value) }
            filters.size?.let { parameter("size", it.value) }
            filters.locale?.let { parameter("locale", it.value) }
            filters.minWidth?.let { parameter("min_width", it) }
            filters.minHeight?.let { parameter("min_height", it) }
            filters.minDuration?.let { parameter("min_duration", it) }
            filters.maxDuration?.let { parameter("max_duration", it) }
        }.map { it.toPaginatedResponse() }
    }

    /**
     * Returns popular videos on Pexels.
     *
     * @param filters Optional filters for dimensions and duration.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [PexKitResult] containing paginated popular videos or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#videos-popular)
     */
    public suspend fun popular(
        filters: VideoFilters = VideoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): PexKitResult<PaginatedResponse<Video>> {
        return executor.get<VideosApiResponse>(Endpoints.VIDEOS_POPULAR) {
            parameter("page", pagination.page)
            parameter("per_page", pagination.perPage ?: defaultPerPage)
            filters.minWidth?.let { parameter("min_width", it) }
            filters.minHeight?.let { parameter("min_height", it) }
            filters.minDuration?.let { parameter("min_duration", it) }
            filters.maxDuration?.let { parameter("max_duration", it) }
        }.map { it.toPaginatedResponse() }
    }

    /**
     * Retrieves a specific video by its ID.
     *
     * @param id The video's unique identifier.
     * @return A [PexKitResult] containing the video or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#videos-show)
     */
    public suspend fun get(id: Long): PexKitResult<Video> {
        return executor.get(Endpoints.video(id))
    }
}
