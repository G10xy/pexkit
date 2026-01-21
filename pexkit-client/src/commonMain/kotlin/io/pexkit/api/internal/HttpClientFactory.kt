package io.pexkit.api.internal

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel as KtorLogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.pexkit.api.LogLevel
import io.pexkit.api.PexKitConfig
import kotlinx.serialization.json.Json


internal fun createHttpClient(config: PexKitConfig): HttpClient {
    val engine = config.httpClientEngine

    return if (engine != null) {
        HttpClient(engine) { configureClient(config) }
    } else {
        HttpClient(httpClientEngineFactory) { configureClient(config) }
    }
}

private fun io.ktor.client.HttpClientConfig<*>.configureClient(config: PexKitConfig) {
    defaultRequest {
        header("Authorization", config.apiKey)
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = config.timeout.inWholeMilliseconds
        connectTimeoutMillis = config.timeout.inWholeMilliseconds
        socketTimeoutMillis = config.timeout.inWholeMilliseconds
    }

    if (config.logLevel != LogLevel.NONE) {
        install(Logging) {
            logger = Logger.SIMPLE
            level = when (config.logLevel) {
                LogLevel.NONE -> KtorLogLevel.NONE
                LogLevel.HEADERS -> KtorLogLevel.HEADERS
                LogLevel.BODY -> KtorLogLevel.BODY
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }
}
