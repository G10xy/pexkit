package io.pexkit.api.internal

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

internal actual val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
    get() = CIO
