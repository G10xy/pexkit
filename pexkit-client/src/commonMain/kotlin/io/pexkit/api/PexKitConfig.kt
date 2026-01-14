package io.pexkit.api

import io.ktor.client.engine.HttpClientEngine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Logging level for HTTP requests and responses.
 */
public enum class LogLevel {
    /** No logging. */
    NONE,
    /** Log request and response headers. */
    HEADERS,
    /** Log request and response bodies (includes headers). */
    BODY,
}

/**
 * Configuration for the PexKit client.
 *
 * Use the DSL builder to create a configuration:
 * ```kotlin
 * val client = PexKit {
 *     apiKey = "YOUR_API_KEY"
 *     defaultPerPage = 20
 *     timeout = 30.seconds
 *     logLevel = LogLevel.HEADERS
 * }
 * ```
 *
 * @property apiKey Your Pexels API key (required).
 * @property defaultPerPage Default number of results per page (1-80, default: 15).
 * @property timeout Request timeout duration (default: 30 seconds).
 * @property logLevel Logging verbosity level (default: NONE).
 * @property httpClientEngine Custom HTTP engine for testing (default: platform engine).
 */
public class PexKitConfig internal constructor(
    public val apiKey: String,
    public val defaultPerPage: Int,
    public val timeout: Duration,
    public val logLevel: LogLevel,
    internal val httpClientEngine: HttpClientEngine?,
) {
    init {
        require(apiKey.isNotBlank()) { "API key must not be blank" }
        require(defaultPerPage in 1..80) { "defaultPerPage must be between 1 and 80" }
    }

    public class Builder {

        public var apiKey: String = ""

        /**
         * Default number of results per page.
         */
        public var defaultPerPage: Int = 15

        /**
         * Request timeout duration.
         */
        public var timeout: Duration = 30.seconds

        /**
         * HTTP logging verbosity level.
         */
        public var logLevel: LogLevel = LogLevel.NONE

        /**
         * Custom HTTP client engine.
         * Used for testing with mock engines. Leave null for platform default.
         */
        public var httpClientEngine: HttpClientEngine? = null

        internal fun build(): PexKitConfig = PexKitConfig(
            apiKey = apiKey,
            defaultPerPage = defaultPerPage,
            timeout = timeout,
            logLevel = logLevel,
            httpClientEngine = httpClientEngine,
        )
    }
}
