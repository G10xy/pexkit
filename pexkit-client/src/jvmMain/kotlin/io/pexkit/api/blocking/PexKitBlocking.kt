package io.pexkit.api.blocking

import io.pexkit.api.PexKit
import io.pexkit.api.PexKitConfig

/**
 * Blocking wrapper for [PexKit] that provides Java-friendly APIs.
 *
 * This class wraps the suspend-based [PexKit] API with blocking methods for use in
 * Java code or Kotlin code that doesn't use coroutines.
 *
 * **Kotlin with coroutines (recommended):**
 * ```kotlin
 * val pexkit = PexKit("API_KEY")
 * val result = pexkit.photos.search("nature")
 * ```
 *
 * **Kotlin without coroutines:**
 * ```kotlin
 * val pexkit = PexKitBlocking.create("API_KEY")
 * val photos = pexkit.photos.search("nature")
 * ```
 *
 * **Java:**
 * ```java
 * try (PexKitBlocking pexkit = PexKitBlocking.create("API_KEY")) {
 *     PaginatedResponse<Photo> photos = pexkit.photos().search("nature");
 * }
 * ```
 *
 * For async APIs with [java.util.concurrent.CompletableFuture], see [io.pexkit.api.async.PexKitAsync].
 *
 * Remember to [close] the client when done to release resources.
 * Implements [AutoCloseable] for use with try-with-resources in Java.
 */
public class PexKitBlocking private constructor(
    private val delegate: PexKit,
) : AutoCloseable {

    /**
     * Blocking API for searching and retrieving photos.
     */
    public val photos: PhotosApiBlocking = PhotosApiBlocking(delegate.photos)

    /**
     * Blocking API for searching and retrieving videos.
     */
    public val videos: VideosApiBlocking = VideosApiBlocking(delegate.videos)

    /**
     * Blocking API for browsing collections.
     */
    public val collections: CollectionsApiBlocking = CollectionsApiBlocking(delegate.collections)

    /**
     * Closes the HTTP client and releases resources.
     *
     * After calling this method, the client should not be used.
     */
    override fun close() {
        delegate.close()
    }

    public companion object {
        /**
         * Creates a PexKitBlocking client with the given API key using default settings.
         *
         * @param apiKey Your Pexels API key.
         * @return A configured [PexKitBlocking] instance.
         */
        @JvmStatic
        public fun create(apiKey: String): PexKitBlocking {
            return create { this.apiKey = apiKey }
        }

        /**
         * Creates a PexKitBlocking client using the DSL builder.
         *
         * @param block Configuration block.
         * @return A configured [PexKitBlocking] instance.
         */
        @JvmStatic
        public fun create(block: PexKitConfig.Builder.() -> Unit): PexKitBlocking {
            val delegate = PexKit(block)
            return PexKitBlocking(delegate)
        }
    }
}
