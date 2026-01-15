package io.pexkit.api

import io.ktor.client.request.parameter
import io.pexkit.api.internal.ApiExecutor
import io.pexkit.api.internal.CollectionMediaApiResponse
import io.pexkit.api.internal.CollectionsApiResponse
import io.pexkit.api.internal.Endpoints
import io.pexkit.api.model.Collection
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.request.MediaType
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.PexKitResult
import io.pexkit.api.response.map

/**
 * API for browsing collections on Pexels.
 *
 * Access via [PexKit.collections]:
 * ```kotlin
 * val result = pexkit.collections.featured()
 * ```
 */
public class CollectionsApi internal constructor(
    private val executor: ApiExecutor,
    private val defaultPerPage: Int,
) {
    /**
     * Returns featured collections curated by the Pexels team.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return A [PexKitResult] containing paginated collections or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#collections-featured)
     */
    public suspend fun featured(
        pagination: PaginationParams = PaginationParams(),
    ): PexKitResult<PaginatedResponse<Collection>> {
        return executor.get<CollectionsApiResponse>(Endpoints.COLLECTIONS_FEATURED) {
            parameter("page", pagination.page)
            parameter("per_page", pagination.perPage ?: defaultPerPage)
        }.map { it.toPaginatedResponse() }
    }

    /**
     * Returns collections belonging to the API key owner.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return A [PexKitResult] containing paginated collections or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#collections-my)
     */
    public suspend fun my(
        pagination: PaginationParams = PaginationParams(),
    ): PexKitResult<PaginatedResponse<Collection>> {
        return executor.get<CollectionsApiResponse>(Endpoints.COLLECTIONS_MY) {
            parameter("page", pagination.page)
            parameter("per_page", pagination.perPage ?: defaultPerPage)
        }.map { it.toPaginatedResponse() }
    }

    /**
     * Returns media (photos and/or videos) from a specific collection.
     *
     * @param id The collection's unique identifier.
     * @param type Filter by media type. If null, returns both photos and videos.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [PexKitResult] containing paginated media items or an error.
     *
     * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#collections-media)
     */
    public suspend fun media(
        id: String,
        type: MediaType? = null,
        pagination: PaginationParams = PaginationParams(),
    ): PexKitResult<PaginatedResponse<CollectionMedia>> {
        return executor.get<CollectionMediaApiResponse>(Endpoints.collectionMedia(id)) {
            parameter("page", pagination.page)
            parameter("per_page", pagination.perPage ?: defaultPerPage)
            type?.let { parameter("type", it.value) }
        }.map { it.toPaginatedResponse() }
    }
}
