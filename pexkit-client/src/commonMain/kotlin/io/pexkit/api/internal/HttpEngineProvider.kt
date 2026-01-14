package io.pexkit.api.internal

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory

/**
 * Platform-specific HTTP client engine factory.
 *
 * - Android: OkHttp engine
 * - iOS: Darwin engine
 */
internal expect val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
