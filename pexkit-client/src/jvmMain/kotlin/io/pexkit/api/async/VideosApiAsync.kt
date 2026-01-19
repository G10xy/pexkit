package io.pexkit.api.async

import io.pexkit.api.VideosApi
import io.pexkit.api.model.Video
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.VideoFilters
import io.pexkit.api.response.PaginatedResponse
import io.pexkit.api.response.getOrThrow
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

/**
 * Async wrapper for [VideosApi] that provides Java-friendly APIs using [CompletableFuture].
 *
 * All methods return [CompletableFuture] that completes exceptionally with
 * [io.pexkit.api.response.PexKitException] on errors.
 */
public class VideosApiAsync internal constructor(
    private val delegate: VideosApi,
    private val executor: ExecutorService,
) {
    /**
     * Searches for videos matching the given query.
     *
     * @param query The search term (e.g., "ocean", "city traffic").
     * @param filters Optional filters for orientation, size, duration, etc.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [CompletableFuture] containing paginated videos.
     */
    @JvmOverloads
    public fun search(
        query: String,
        filters: VideoFilters = VideoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): CompletableFuture<PaginatedResponse<Video>> = CompletableFuture.supplyAsync({
        runBlocking { delegate.search(query, filters, pagination).getOrThrow() }
    }, executor)

    /**
     * Returns popular videos on Pexels.
     *
     * @param filters Optional filters for dimensions and duration.
     * @param pagination Pagination parameters (page, perPage).
     * @return A [CompletableFuture] containing paginated popular videos.
     */
    @JvmOverloads
    public fun popular(
        filters: VideoFilters = VideoFilters(),
        pagination: PaginationParams = PaginationParams(),
    ): CompletableFuture<PaginatedResponse<Video>> = CompletableFuture.supplyAsync({
        runBlocking { delegate.popular(filters, pagination).getOrThrow() }
    }, executor)

    /**
     * Retrieves a specific video by its ID.
     *
     * @param id The video's unique identifier.
     * @return A [CompletableFuture] containing the video.
     */
    public fun get(id: Long): CompletableFuture<Video> = CompletableFuture.supplyAsync({
        runBlocking { delegate.get(id).getOrThrow() }
    }, executor)
}
