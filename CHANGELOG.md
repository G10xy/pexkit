# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- **JVM/Backend support** - PexKit now works on JVM backends (Spring Boot, Ktor Server, etc.)
  - CIO engine for HTTP on JVM (requires Java 21+)
  - `PexKitBlocking` - Java-friendly blocking API wrapper
  - `PhotosApiBlocking`, `VideosApiBlocking`, `CollectionsApiBlocking` - Blocking API wrappers
  - Blocking methods using `runBlocking` for synchronous calls
  - Async methods returning `CompletableFuture` for Java async patterns
  - Implements `AutoCloseable` for try-with-resources support
- **Java interop annotations**
  - `@JvmOverloads` on `PhotoFilters`, `VideoFilters`, `PaginationParams` constructors
  - `@JvmStatic` on `PhotoFilters.withColor()` and `PhotoFilters.withHexColor()`
  - `@JvmStatic` on `PexKitBlocking.create()` factory methods

### Technical Details

- JVM: CIO engine, Java 21+

## [0.1.0] - 15-01-2026

### Added

- Initial release of PexKit
- **Kotlin Multiplatform support** for Android and iOS
- **Photos API**
  - `search()` - Search photos by query with optional filters
  - `curated()` - Get trending photos curated by Pexels
  - `get(id)` - Retrieve a specific photo by ID
- **Videos API**
  - `search()` - Search videos by query with optional filters
  - `popular()` - Get popular videos with dimension/duration filters
  - `get(id)` - Retrieve a specific video by ID
- **Collections API**
  - `featured()` - Get featured collections
  - `my()` - Get collections owned by the API key holder
  - `media(id)` - Get photos and/or videos from a collection
- **Type-safe result handling** with `PexKitResult` sealed class
  - `Success` - Contains data and rate limit info
  - `Failure` - Contains typed `PexKitError`
- **Typed error handling** with `PexKitError` sealed interface
  - `Unauthorized` - Invalid or missing API key (401)
  - `Forbidden` - Access forbidden (403)
  - `NotFound` - Resource not found (404)
  - `RateLimited` - Too many requests with retry info (429)
  - `ServerError` - Server errors (5xx)
  - `NetworkError` - Connection failures
  - `Unknown` - Unexpected errors
- **Pagination support** with `PaginatedResponse<T>`
  - `hasNextPage` / `hasPrevPage` helpers
  - `totalResults` for result count
- **Photo filters**: orientation, size, color, locale
- **Video filters**: orientation, size, locale, min/max dimensions, min/max duration
- **Rate limit information** available on every successful response
- **DSL-style configuration** for client setup
- **Configurable logging** levels: NONE, HEADERS, BODY
- **Convenience extensions** on `PexKitResult`:
  - `getOrNull()`, `getOrThrow()`, `getOrDefault()`, `getOrElse()`
  - `map()`, `onSuccess()`, `onFailure()`

### Technical Details

- Built with Kotlin 2.0.21
- Ktor Client 3.0.2 for HTTP
- kotlinx-serialization 1.7.3 for JSON
- kotlinx-coroutines 1.9.0 for async
- Android: OkHttp engine, minSdk 24
- iOS: Darwin engine (URLSession)

[Unreleased]: https://github.com/G10xy/pexkit/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/G10xy/pexkit/releases/tag/v0.1.0
