package io.pexkit.api.internal

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
    get() = OkHttp
