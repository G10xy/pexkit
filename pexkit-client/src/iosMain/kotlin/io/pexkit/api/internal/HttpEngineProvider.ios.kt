package io.pexkit.api.internal

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
    get() = Darwin
