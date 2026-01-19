package io.pexkit.api.async

import io.pexkit.api.PhotosApi
import io.pexkit.api.model.Photo
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.PhotoFilters
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.getOrThrow
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

/**
 * Async wrapper for [PhotosApi] that provides Java-friendly APIs using [CompletableFuture].
 *
 * All methods return [CompletableFuture] that completes exceptionally with
 * [io.pexkit.api.response.PexKitException] on errors.
 */
public class PhotosApiAsync internal constructor(
    private val delegate: PhotosApi,
    private val executor: ExecutorService,
) {
    /**
     * Searches for photos matching the given query.
     *
     * @param query The search term (e.g., "nature", "office workspace").
     * @param filters Optional filters for orientation, size, and color.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [CompletableFuture] containing paginated photos.
     */
    @JvmOverloads
    public fun search(
        query: String,
        filters: PhotoFilters = PhotoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): CompletableFuture<PaginatedResponse<Photo>> = CompletableFuture.supplyAsync({
        runBlocking { delegate.search(query, filters, pagination).getOrThrow() }
    }, executor)

    /**
     * Returns curated photos hand-picked by the Pexels team.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return A [CompletableFuture] containing paginated curated photos.
     */
    @JvmOverloads
    public fun curated(
        pagination: PaginationParams = PaginationParams(),
    ): CompletableFuture<PaginatedResponse<Photo>> = CompletableFuture.supplyAsync({
        runBlocking { delegate.curated(pagination).getOrThrow() }
    }, executor)

    /**
     * Retrieves a specific photo by its ID.
     *
     * @param id The photo's unique identifier.
     * @return A [CompletableFuture] containing the photo.
     */
    public fun get(id: Long): CompletableFuture<Photo> = CompletableFuture.supplyAsync({
        runBlocking { delegate.get(id).getOrThrow() }
    }, executor)
}
