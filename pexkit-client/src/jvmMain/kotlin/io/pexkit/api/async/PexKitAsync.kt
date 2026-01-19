package io.pexkit.api.async

import io.pexkit.api.PexKit
import io.pexkit.api.PexKitConfig
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Async wrapper for [PexKit] that provides Java-friendly APIs using [java.util.concurrent.CompletableFuture].
 *
 * This class wraps the suspend-based [PexKit] API with async methods for use in
 * Java code that prefers non-blocking, future-based APIs.
 *
 * **Java usage:**
 * ```java
 * PexKitAsync pexkit = PexKitAsync.create("API_KEY");
 * pexkit.photos().search("nature")
 *     .thenAccept(photos -> System.out.println(photos.getData().size()));
 *
 * // Or with chaining
 * pexkit.photos().search("nature")
 *     .thenCompose(photos -> pexkit.photos().get(photos.getData().get(0).getId()))
 *     .thenAccept(photo -> System.out.println(photo.getPhotographer()));
 * ```
 *
 * **Kotlin usage:**
 * ```kotlin
 * val pexkit = PexKitAsync.create("API_KEY")
 * pexkit.photos.search("nature")
 *     .thenAccept { photos -> println(photos.data.size) }
 * ```
 *
 * Remember to [close] the client when done to release resources.
 * Implements [AutoCloseable] for use with try-with-resources in Java.
 */
public class PexKitAsync private constructor(
    private val delegate: PexKit,
    private val executor: ExecutorService,
) : AutoCloseable {

    /**
     * Async API for searching and retrieving photos.
     */
    public val photos: PhotosApiAsync = PhotosApiAsync(delegate.photos, executor)

    /**
     * Async API for searching and retrieving videos.
     */
    public val videos: VideosApiAsync = VideosApiAsync(delegate.videos, executor)

    /**
     * Async API for browsing collections.
     */
    public val collections: CollectionsApiAsync = CollectionsApiAsync(delegate.collections, executor)

    /**
     * Closes the HTTP client, shuts down the executor, and releases resources.
     *
     * After calling this method, the client should not be used.
     */
    override fun close() {
        delegate.close()
        executor.shutdown()
    }

    public companion object {
        /**
         * Creates a PexKitAsync client with the given API key using default settings.
         *
         * @param apiKey Your Pexels API key.
         * @return A configured [PexKitAsync] instance.
         */
        @JvmStatic
        public fun create(apiKey: String): PexKitAsync {
            return create { this.apiKey = apiKey }
        }

        /**
         * Creates a PexKitAsync client using the DSL builder.
         *
         * @param block Configuration block.
         * @return A configured [PexKitAsync] instance.
         */
        @JvmStatic
        public fun create(block: PexKitConfig.Builder.() -> Unit): PexKitAsync {
            val delegate = PexKit(block)
            val executor = Executors.newCachedThreadPool()
            return PexKitAsync(delegate, executor)
        }
    }
}
