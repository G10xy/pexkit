package io.pexkit.api.blocking

import io.pexkit.api.CollectionsApi
import io.pexkit.api.model.Collection
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.request.MediaType
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.PexKitException
import io.pexkit.api.response.getOrThrow
import kotlinx.coroutines.runBlocking
import kotlin.jvm.Throws

/**
 * Blocking wrapper for [CollectionsApi] that provides Java-friendly APIs.
 *
 * All methods throw [io.pexkit.api.response.PexKitException] on errors.
 *
 * For async APIs with [java.util.concurrent.CompletableFuture], see [io.pexkit.api.async.CollectionsApiAsync].
 */
public class CollectionsApiBlocking internal constructor(
    private val delegate: CollectionsApi,
) {
    /**
     * Returns featured collections curated by the Pexels team.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return Paginated collections.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @JvmOverloads
    @Throws(PexKitException::class)
    public fun featured(
        pagination: PaginationParams = PaginationParams(),
    ): PaginatedResponse<Collection> = runBlocking {
        delegate.featured(pagination).getOrThrow()
    }

    /**
     * Returns collections belonging to the API key owner.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return Paginated collections.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @JvmOverloads
    @Throws(PexKitException::class)
    public fun my(
        pagination: PaginationParams = PaginationParams(),
    ): PaginatedResponse<Collection> = runBlocking {
        delegate.my(pagination).getOrThrow()
    }

    /**
     * Returns media (photos and/or videos) from a specific collection.
     *
     * @param id The collection's unique identifier.
     * @param type Filter by media type. If null, returns both photos and videos.
     * @param pagination Pagination parameters (page, perPage).
     * @return Paginated media items.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @JvmOverloads
    @Throws(PexKitException::class)
    public fun media(
        id: String,
        type: MediaType? = null,
        pagination: PaginationParams = PaginationParams(),
    ): PaginatedResponse<CollectionMedia> = runBlocking {
        delegate.media(id, type, pagination).getOrThrow()
    }
}
