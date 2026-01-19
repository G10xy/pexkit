package io.pexkit.api.async

import io.pexkit.api.CollectionsApi
import io.pexkit.api.model.Collection
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.request.MediaType
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.getOrThrow
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

/**
 * Async wrapper for [CollectionsApi] that provides Java-friendly APIs using [CompletableFuture].
 *
 * All methods return [CompletableFuture] that completes exceptionally with
 * [io.pexkit.api.response.PexKitException] on errors.
 */
public class CollectionsApiAsync internal constructor(
    private val delegate: CollectionsApi,
    private val executor: ExecutorService,
) {
    /**
     * Returns featured collections curated by the Pexels team.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return A [CompletableFuture] containing paginated collections.
     */
    @JvmOverloads
    public fun featured(
        pagination: PaginationParams = PaginationParams(),
    ): CompletableFuture<PaginatedResponse<Collection>> = CompletableFuture.supplyAsync({
        runBlocking { delegate.featured(pagination).getOrThrow() }
    }, executor)

    /**
     * Returns collections belonging to the API key owner.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return A [CompletableFuture] containing paginated collections.
     */
    @JvmOverloads
    public fun my(
        pagination: PaginationParams = PaginationParams(),
    ): CompletableFuture<PaginatedResponse<Collection>> = CompletableFuture.supplyAsync({
        runBlocking { delegate.my(pagination).getOrThrow() }
    }, executor)

    /**
     * Returns media (photos and/or videos) from a specific collection.
     *
     * @param id The collection's unique identifier.
     * @param type Filter by media type. If null, returns both photos and videos.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [CompletableFuture] containing paginated media items.
     */
    @JvmOverloads
    public fun media(
        id: String,
        type: MediaType? = null,
        pagination: PaginationParams = PaginationParams(),
    ): CompletableFuture<PaginatedResponse<CollectionMedia>> = CompletableFuture.supplyAsync({
        runBlocking { delegate.media(id, type, pagination).getOrThrow() }
    }, executor)
}
