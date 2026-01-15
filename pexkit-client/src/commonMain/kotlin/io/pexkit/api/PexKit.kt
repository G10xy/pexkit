package io.pexkit.api

import io.ktor.client.HttpClient
import io.pexkit.api.internal.ApiExecutor
import io.pexkit.api.internal.createHttpClient

/**
 * Main entry point for the Pexels API client.
 *
 * Create an instance using the DSL builder:
 * ```kotlin
 * val pexkit = PexKit {
 *     apiKey = "YOUR_API_KEY"
 *     defaultPerPage = 20
 *     timeout = 30.seconds
 *     logLevel = LogLevel.HEADERS
 * }
 * ```
 *
 * Or with just an API key:
 * ```kotlin
 * val pexkit = PexKit("YOUR_API_KEY")
 * ```
 *
 * Then use the fluent API to access different resources:
 * ```kotlin
 * // Search photos
 * val photos = pexkit.photos.search("nature")
 *
 * // Get curated photos
 * val curated = pexkit.photos.curated()
 *
 * // Search videos
 * val videos = pexkit.videos.search("ocean")
 *
 * // Browse collections
 * val collections = pexkit.collections.featured()
 * ```
 *
 * Remember to [close] the client when done to release resources.
 */
public class PexKit private constructor(
    private val config: PexKitConfig,
    private val httpClient: HttpClient,
) {
    private val executor = ApiExecutor(httpClient)

    /**
     * API for searching and retrieving photos.
     */
    public val photos: PhotosApi = PhotosApi(executor, config.defaultPerPage)

    /**
     * API for searching and retrieving videos.
     */
    public val videos: VideosApi = VideosApi(executor, config.defaultPerPage)

    /**
     * API for browsing collections.
     */
    public val collections: CollectionsApi = CollectionsApi(executor, config.defaultPerPage)

    /**
     * Closes the HTTP client and releases resources.
     *
     * After calling this method, the client should not be used.
     */
    public fun close() {
        httpClient.close()
    }

    public companion object {
        /**
         * Creates a PexKit client with the given API key using default settings.
         *
         * @param apiKey Your Pexels API key.
         * @return A configured [PexKit] instance.
         */
        public operator fun invoke(apiKey: String): PexKit {
            return invoke { this.apiKey = apiKey }
        }

        /**
         * Creates a PexKit client using the DSL builder.
         *
         * @param block Configuration block.
         * @return A configured [PexKit] instance.
         */
        public operator fun invoke(block: PexKitConfig.Builder.() -> Unit): PexKit {
            val config = PexKitConfig.Builder().apply(block).build()
            val httpClient = createHttpClient(config)
            return PexKit(config, httpClient)
        }
    }
}
