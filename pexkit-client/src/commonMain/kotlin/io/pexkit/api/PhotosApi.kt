package io.pexkit.api

import io.ktor.client.request.parameter
import io.pexkit.api.internal.ApiExecutor
import io.pexkit.api.internal.Endpoints
import io.pexkit.api.internal.PhotosApiResponse
import io.pexkit.api.model.Photo
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.PhotoFilters
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.PexKitResult
import io.pexkit.api.response.map

/**
 * API for searching and retrieving photos from Pexels.
 *
 * Access via [PexKit.photos]:
 * ```kotlin
 * val result = pexkit.photos.search("nature")
 * ```
 */
public class PhotosApi internal constructor(
    private val executor: ApiExecutor,
    private val defaultPerPage: Int,
) {
    /**
     * Searches for photos matching the given query.
     *
     * @param query The search term (e.g., "nature", "office workspace").
     * @param filters Optional filters for orientation, size, and color.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [PexKitResult] containing paginated photos or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#photos-search)
     */
    public suspend fun search(
        query: String,
        filters: PhotoFilters = PhotoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): PexKitResult<PaginatedResponse<Photo>> {
        return executor.get<PhotosApiResponse>(Endpoints.PHOTOS_SEARCH) {
            parameter("query", query)
            parameter("page", pagination.page)
            parameter("per_page", pagination.perPage ?: defaultPerPage)
            filters.orientation?.let { parameter("orientation", it.value) }
            filters.size?.let { parameter("size", it.value) }
            filters.color?.let { parameter("color", it) }
            filters.locale?.let { parameter("locale", it.value) }
        }.map { it.toPaginatedResponse() }
    }

    /**
     * Returns curated photos hand-picked by the Pexels team.
     *
     * The curated photos are updated hourly.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return A [PexKitResult] containing paginated curated photos or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#photos-curated)
     */
    public suspend fun curated(
        pagination: PaginationParams = PaginationParams(),
    ): PexKitResult<PaginatedResponse<Photo>> {
        return executor.get<PhotosApiResponse>(Endpoints.PHOTOS_CURATED) {
            parameter("page", pagination.page)
            parameter("per_page", pagination.perPage ?: defaultPerPage)
        }.map { it.toPaginatedResponse() }
    }

    /**
     * Retrieves a specific photo by its ID.
     *
     * @param id The photo's unique identifier.
     * @return A [PexKitResult] containing the photo or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#photos-show)
     */
    public suspend fun get(id: Long): PexKitResult<Photo> {
        return executor.get(Endpoints.photo(id))
    }
}
