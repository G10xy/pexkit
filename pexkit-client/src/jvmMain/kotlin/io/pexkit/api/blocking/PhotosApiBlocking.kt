package io.pexkit.api.blocking

import io.pexkit.api.PhotosApi
import io.pexkit.api.model.Photo
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.PhotoFilters
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.PexKitException
import io.pexkit.api.response.getOrThrow
import kotlinx.coroutines.runBlocking
import kotlin.jvm.Throws

/**
 * Blocking wrapper for [PhotosApi] that provides Java-friendly APIs.
 *
 * All methods throw [io.pexkit.api.response.PexKitException] on errors.
 *
 * For async APIs with [java.util.concurrent.CompletableFuture], see [io.pexkit.api.async.PhotosApiAsync].
 */
public class PhotosApiBlocking internal constructor(
    private val delegate: PhotosApi,
) {
    /**
     * Searches for photos matching the given query.
     *
     * @param query The search term (e.g., "nature", "office workspace").
     * @param filters Optional filters for orientation, size, and color.
     * @param pagination Pagination parameters (page, perPage).
     * @return Paginated photos.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @JvmOverloads
    @Throws(PexKitException::class)
    public fun search(
        query: String,
        filters: PhotoFilters = PhotoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): PaginatedResponse<Photo> = runBlocking {
        delegate.search(query, filters, pagination).getOrThrow()
    }

    /**
     * Returns curated photos hand-picked by the Pexels team.
     *
     * @param pagination Pagination parameters (page, perPage).
     * @return Paginated curated photos.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @JvmOverloads
    @Throws(PexKitException::class)
    public fun curated(
        pagination: PaginationParams = PaginationParams(),
    ): PaginatedResponse<Photo> = runBlocking {
        delegate.curated(pagination).getOrThrow()
    }

    /**
     * Retrieves a specific photo by its ID.
     *
     * @param id The photo's unique identifier.
     * @return The photo.
     * @throws io.pexkit.api.response.PexKitException on API errors.
     */
    @Throws(PexKitException::class)
    public fun get(id: Long): Photo = runBlocking {
        delegate.get(id).getOrThrow()
    }
}
