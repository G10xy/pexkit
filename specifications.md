# specifications.md - PexKit

> **Purpose**: This file provides context and specifications for building this library.
> **Project**: A Kotlin Multiplatform client library for the Pexels API, targeting mobile platforms (Android & iOS) and JVM backends.

---

## 🎯 Project Overview

**Name**: `pexkit`  
**Description**: An idiomatic Kotlin Multiplatform client library for the Pexels API, enabling mobile developers and backend engineers to easily search and retrieve high-quality stock photos and videos.

**Goals**:
- Provide a type-safe, coroutine-based API
- Support Android, iOS, JVM, and JavaScript platforms
- Follow Kotlin idioms and best practices
- Minimize dependencies
- Make integration dead simple
- Provide Java-friendly APIs for backend usage
- Provide Promise-based APIs for JavaScript/TypeScript usage

---

## 🛠 Tech Stack

| Component | Choice | Rationale |
|-----------|--------|-----------|
| **Language** | Kotlin 2.0+ | Target language, KMP support |
| **HTTP Client** | Ktor Client 3.x | Best KMP HTTP client, native engine support |
| **Serialization** | kotlinx-serialization | Official Kotlin solution, compile-time safe |
| **Async** | Kotlin Coroutines | Standard for Kotlin async, Flow for streaming |
| **Build** | Gradle with Kotlin DSL | Standard for KMP, version catalogs |
| **Testing** | kotlin.test + Ktor Mock | Cross-platform testing |

---

## 📱 Target Platforms

| Platform | Engine | Min Version |
|----------|--------|-------------|
| **Android** | Ktor OkHttp | minSdk 24 (Android 7.0) |
| **iOS** | Ktor Darwin | iOS 14.0+ |
| **JVM** | Ktor CIO | Java 21+ |
| **JS/Browser** | Ktor JS | Modern browsers (ES2015+) |
| **JS/Node.js** | Ktor JS | Node.js 18+ |

---

## 📁 Project Structure

```
pexkit/
├── specifications.md            # This file
├── README.md                    # User-facing documentation
├── LICENSE                      # Apache 2.0
├── gradle/
│   └── libs.versions.toml       # Version catalog
├── build.gradle.kts             # Root build file
├── settings.gradle.kts          # Project settings
├── convention-plugins/          # Shared build logic (optional)
├── pexkit-client/               # Main library module
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/
│       │       └── io/pexkit/api/
│       │           ├── PexKit.kt                 # Main entry point
│       │           ├── PexKitConfig.kt           # Configuration
│       │           ├── model/                    # Data classes
│       │           │   ├── Photo.kt
│       │           │   ├── Video.kt
│       │           │   ├── Collection.kt
│       │           │   ├── User.kt
│       │           │   └── common/               # Shared types
│       │           │       ├── Pagination.kt
│       │           │       └── Source.kt
│       │           ├── request/                  # Request builders
│       │           │   ├── SearchRequest.kt
│       │           │   ├── PaginationParams.kt
│       │           │   └── PhotoFilters.kt
│       │           ├── response/                 # Response wrappers
│       │           │   ├── PexKitResponse.kt
│       │           │   ├── PhotosResponse.kt
│       │           │   ├── VideosResponse.kt
│       │           │   └── CollectionsResponse.kt
│       │           ├── exception/                # Custom exceptions
│       │           │   └── PexKitException.kt
│       │           └── internal/                 # Internal utilities
│       │               ├── HttpClientFactory.kt
│       │               ├── Endpoints.kt
│       │               └── Logging.kt
│       ├── commonTest/
│       │   └── kotlin/
│       │       └── io/pexkit/api/
│       │           ├── SerializationTest.kt
│       │           ├── PexKitClientTest.kt
│       │           └── MockResponses.kt
│       ├── androidMain/
│       │   └── kotlin/
│       │       └── io/pexkit/api/
│       │           └── HttpEngineProvider.android.kt
│       ├── iosMain/
│       │   └── kotlin/
│       │       └── io/pexkit/api/
│       │           └── HttpEngineProvider.ios.kt
│       ├── jvmMain/
│       │   └── kotlin/
│       │       └── io/pexkit/api/
│       │           ├── internal/
│       │           │   └── HttpEngineProvider.jvm.kt
│       │           ├── blocking/                 # Java-friendly blocking APIs
│       │           │   ├── PexKitBlocking.kt
│       │           │   ├── PhotosApiBlocking.kt
│       │           │   ├── VideosApiBlocking.kt
│       │           │   └── CollectionsApiBlocking.kt
│       │           └── async/                    # Java-friendly async APIs (CompletableFuture)
│       │               ├── PexKitAsync.kt
│       │               ├── PhotosApiAsync.kt
│       │               ├── VideosApiAsync.kt
│       │               └── CollectionsApiAsync.kt
│       ├── jvmTest/
│       │   └── kotlin/
│       │       └── io/pexkit/api/
│       │           ├── BlockingApiTest.kt
│       │           └── AsyncApiTest.kt
│       ├── jsMain/
│       │   └── kotlin/
│       │       └── io/pexkit/api/
│       │           ├── internal/
│       │           │   └── HttpEngineProvider.js.kt
│       │           └── js/                       # JavaScript-friendly Promise APIs
│       │               ├── PexKitJs.kt
│       │               ├── PhotosApiJs.kt
│       │               ├── VideosApiJs.kt
│       │               ├── CollectionsApiJs.kt
│       │               └── OptionsJs.kt
│       └── jsTest/
│           └── kotlin/
│               └── io/pexkit/api/
│                   └── JsApiTest.kt
└── sample/                      # Sample usage (optional)
    └── shared/                  # Shared sample code
```

---

## 🏗 Architecture Decisions

### 1. Client Design Pattern
Use a **single entry point** with logical grouping:

```kotlin
val pexkit = PexKit("API_KEY")

// Fluent API with named groups
pexkit.photos.search("nature")
pexkit.photos.curated()
pexkit.photos.get(12345)

pexkit.videos.search("ocean")
pexkit.videos.popular()
pexkit.videos.get(67890)

pexkit.collections.featured()
pexkit.collections.my()
pexkit.collections.media("abc123")
```

### 2. Response Handling
Use **Result-based returns** with sealed classes:

```kotlin
// Prefer kotlin.Result or custom sealed class
sealed interface PexKitResult<out T> {
    data class Success<T>(val data: T, val rateLimit: RateLimitInfo) : PexKitResult<T>
    data class Failure(val error: PexKitError) : PexKitResult<Nothing>
}

// Errors are typed, not just exceptions
sealed interface PexKitError {
    data class Unauthorized(val message: String) : PexKitError
    data class RateLimited(val retryAfter: Int) : PexKitError
    data class NotFound(val resource: String) : PexKitError
    data class NetworkError(val cause: Throwable) : PexKitError
    data class Unknown(val code: Int, val body: String?) : PexKitError
}
```

### 3. Pagination Strategy
Return **paginated wrapper** that enables easy iteration:

```kotlin
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val perPage: Int,
    val totalResults: Int,
    val nextPage: String?,
    val prevPage: String?
) {
    val hasNextPage: Boolean get() = nextPage != null
    val hasPrevPage: Boolean get() = prevPage != null
}

// Extension for Flow-based iteration
fun <T> PexKit.Photos.searchAsFlow(query: String): Flow<T>
```

### 4. Configuration
Immutable config with builder:

```kotlin
val client = PexKit {
    apiKey = "YOUR_KEY"
    defaultPerPage = 20
    timeout = 30.seconds
    logging = LogLevel.BODY
}
```

### 5. JVM/Java Interoperability (Phase 7)
Provide **blocking wrappers** for Java compatibility:

```kotlin
// Kotlin coroutine API (primary)
suspend fun search(query: String): PexKitResult<PaginatedResponse<Photo>>

// JVM blocking API (for Java interop)
fun searchBlocking(query: String): PaginatedResponse<Photo>  // throws PexKitException on error

// JVM CompletableFuture API (for async Java)
fun searchAsync(query: String): CompletableFuture<PaginatedResponse<Photo>>
```

---

## 🎨 Kotlin Style Guidelines

### General Principles
- **Immutability first**: Use `val`, immutable collections, `data class`
- **Null safety**: Avoid `!!`, prefer `?.`, `?:`, and `let`
- **Explicit types**: Always declare return types on public APIs
- **Named arguments**: Use for functions with 3+ parameters
- **Trailing lambdas**: Use for DSL-style builders

### Naming Conventions
```kotlin
// Classes: PascalCase
class PexKit
data class PhotoSource

// Functions/Properties: camelCase
fun searchPhotos()
val totalResults: Int

// Constants: SCREAMING_SNAKE_CASE
const val DEFAULT_PER_PAGE = 15

// Type parameters: Single uppercase letter or descriptive
class PaginatedResponse<T>
interface Mapper<Input, Output>
```

### Data Classes
```kotlin
// Always use @Serializable for API models
@Serializable
data class Photo(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerialName("photographer_url") val photographerUrl: String,
    @SerialName("photographer_id") val photographerId: Long,
    @SerialName("avg_color") val avgColor: String,
    val src: PhotoSource,
    val alt: String,
    val liked: Boolean = false,  // Default for optional fields
)
```

### Extension Functions
Prefer extensions for utility operations:
```kotlin
// Good: Extension keeps Photo class clean
fun Photo.aspectRatio(): Float = width.toFloat() / height

// Good: Extension for platform-specific conversions
expect fun Photo.toUri(): PlatformUri
```

### Coroutines
```kotlin
// All API methods are suspend functions
suspend fun search(query: String): PexKitResult<PaginatedResponse<Photo>>

// Use Flow for streaming/pagination
fun searchAsFlow(query: String): Flow<Photo>

// Internal: Use withContext for thread switching
internal suspend fun executeRequest(): Response = withContext(Dispatchers.IO) {
    // ...
}
```

### Documentation
Use KDoc for all public APIs:
```kotlin
/**
 * Searches for photos matching the given query.
 *
 * @param query The search term (e.g., "nature", "office workspace")
 * @param filters Optional filters for orientation, size, and color
 * @param pagination Pagination parameters (page, perPage)
 * @return A [PexKitResult] containing paginated photos or an error
 *
 * @sample io.pexkit.api.samples.searchPhotosSample
 * @see [Pexels API Documentation](https://www.pexels.com/api/documentation/#photos-search)
 */
suspend fun search(
    query: String,
    filters: PhotoFilters = PhotoFilters(),
    pagination: PaginationParams = PaginationParams(),
): PexKitResult<PaginatedResponse<Photo>>
```

---

## 🌐 Pexels API Reference

### Base URLs
```
Photos API: https://api.pexels.com/v1/
Videos API: https://api.pexels.com/videos/
```

### Authentication
All requests require the API key in the header:
```
Authorization: YOUR_API_KEY
```

### Rate Limits
- **Default**: 200 requests/hour, 20,000 requests/month
- **Headers in response**:
  - `X-Ratelimit-Limit`: Total allowed
  - `X-Ratelimit-Remaining`: Remaining requests
  - `X-Ratelimit-Reset`: Unix timestamp for reset

### Pagination (All List Endpoints)
| Parameter | Type | Default | Max | Description |
|-----------|------|---------|-----|-------------|
| `page` | Int | 1 | — | Page number |
| `per_page` | Int | 15 | 80 | Results per page |

Response includes:
```json
{
  "page": 1,
  "per_page": 15,
  "total_results": 10000,
  "prev_page": null,
  "next_page": "https://api.pexels.com/v1/search?page=2&query=nature"
}
```

---

### 📸 Photos Endpoints

#### 1. Search Photos
```
GET /v1/search
```
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | String | ✅ | Search term |
| `orientation` | String | ❌ | `landscape`, `portrait`, `square` |
| `size` | String | ❌ | `large` (24MP), `medium` (12MP), `small` (4MP) |
| `color` | String | ❌ | Hex code (without #) or color name: `red`, `orange`, `yellow`, `green`, `turquoise`, `blue`, `violet`, `pink`, `brown`, `black`, `gray`, `white` |
| `locale` | String | ❌ | Language code: `en-US`, `pt-BR`, `es-ES`, `ca-ES`, `de-DE`, `it-IT`, `fr-FR`, `sv-SE`, `id-ID`, `pl-PL`, `ja-JP`, `zh-TW`, `zh-CN`, `ko-KR`, `th-TH`, `nl-NL`, `hu-HU`, `vi-VN`, `cs-CZ`, `da-DK`, `fi-FI`, `uk-UA`, `el-GR`, `ro-RO`, `nb-NO`, `sk-SK`, `tr-TR`, `ru-RU` |

#### 2. Curated Photos
```
GET /v1/curated
```
Returns trending photos curated by the Pexels team. Updated hourly.

#### 3. Get Photo
```
GET /v1/photos/{id}
```

---

### 🎬 Videos Endpoints

#### 1. Search Videos
```
GET /videos/search
```
Same parameters as photo search.

#### 2. Popular Videos
```
GET /videos/popular
```
Additional parameters:
| Parameter | Type | Description |
|-----------|------|-------------|
| `min_width` | Int | Minimum width in pixels |
| `min_height` | Int | Minimum height in pixels |
| `min_duration` | Int | Minimum duration in seconds |
| `max_duration` | Int | Maximum duration in seconds |

#### 3. Get Video
```
GET /videos/videos/{id}
```

---

### 📁 Collections Endpoints

#### 1. Featured Collections
```
GET /v1/collections/featured
```

#### 2. My Collections
```
GET /v1/collections
```
Returns collections belonging to the API key owner.

#### 3. Collection Media
```
GET /v1/collections/{id}
```
| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | String | `photos`, `videos`, or omit for both |

---

### 📦 Response Models

#### Photo Object
```json
{
  "id": 2014422,
  "width": 3024,
  "height": 4032,
  "url": "https://www.pexels.com/photo/2014422/",
  "photographer": "Joey Doe",
  "photographer_url": "https://www.pexels.com/@joey",
  "photographer_id": 123456,
  "avg_color": "#978E82",
  "src": {
    "original": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg",
    "large2x": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940",
    "large": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&h=650&w=940",
    "medium": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&h=350",
    "small": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&h=130",
    "portrait": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&fit=crop&h=1200&w=800",
    "landscape": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&fit=crop&h=627&w=1200",
    "tiny": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280"
  },
  "liked": false,
  "alt": "Brown rocks during golden hour"
}
```

#### Video Object
```json
{
  "id": 857251,
  "width": 1920,
  "height": 1080,
  "url": "https://www.pexels.com/video/857251/",
  "image": "https://images.pexels.com/videos/857251/free-video-857251.jpg",
  "full_res": null,
  "tags": [],
  "duration": 35,
  "user": {
    "id": 123456,
    "name": "Jane Doe",
    "url": "https://www.pexels.com/@jane"
  },
  "video_files": [
    {
      "id": 123456,
      "quality": "hd",
      "file_type": "video/mp4",
      "width": 1920,
      "height": 1080,
      "fps": 25.0,
      "link": "https://player.vimeo.com/external/..."
    }
  ],
  "video_pictures": [
    {
      "id": 123456,
      "picture": "https://images.pexels.com/videos/857251/...",
      "nr": 0
    }
  ]
}
```

#### Collection Object
```json
{
  "id": "abc123",
  "title": "Nature",
  "description": "Beautiful nature photos",
  "private": false,
  "media_count": 250,
  "photos_count": 200,
  "videos_count": 50
}
```

---

## ✅ Implementation Phases

### Phase 1: Project Setup 
- [✅] Initialize Gradle with Kotlin DSL
- [✅] Configure KMP for Android + iOS targets
- [✅] Add dependencies (Ktor, Serialization, Coroutines)
- [✅] Set up version catalog (`libs.versions.toml`)
- [✅] Configure publishing (Maven coordinates, signing, repository config)

### Phase 2: Core Models 
- [✅] data classes: `Photo`, `PhotoSource`, `Video`, `VideoFile`, `VideoPicture`, `Collection`, `User`, `RateLimitInfo`
- [✅] `PaginatedResponse<T>` generic wrapper
- [✅] `PexKitError` sealed hierarchy

### Phase 3: HTTP Client Core 
- [✅] Platform-specific engine providers (expect/actual)
- [✅] `PexKitConfig` configuration class
- [✅] Base HTTP client factory with auth interceptor
- [✅] Request/response logging
- [✅] Error response parsing

### Phase 4: API Implementation 
- [✅] `PhotosApi` interface + implementation
  - [✅] `search()`
  - [✅] `curated()`
  - [✅] `get(id)`
- [✅] `VideosApi` interface + implementation
  - [✅] `search()`
  - [✅] `popular()`
  - [✅] `get(id)`
- [✅] `CollectionsApi` interface + implementation
  - [✅] `featured()`
  - [✅] `my()`
  - [✅] `media(id)`
- [✅] `PexKit` as main entry point

### Phase 5: Testing 
- [✅] Unit tests for JSON serialization
- [✅] Mock responses for all endpoints by using data classes
- [✅] Mock client tests for each endpoint
- [✅] Error handling tests (401, 403, 429, 500)
- [✅] Pagination tests

### Phase 6: Documentation & Publishing 
- [✅] README with usage examples
- [✅] CHANGELOG.md
- [-] Publish to Maven Central

### Phase 7: JVM/Backend Support 🆕
Add JVM target to enable usage in Kotlin/Java backend applications (Spring Boot, Ktor Server, etc.).

#### 7.1 Add JVM Target and HTTP Engine
- [✅] Add `jvm()` target in `pexkit-client/build.gradle.kts` with Java 21 support
- [✅] Enable Java interop with `withJava()`
- [✅] Configure JVM toolchain for Java 21
- [✅] Add `ktor-client-cio` dependency to `libs.versions.toml`
- [✅] Add CIO engine dependency in `jvmMain.dependencies` block
- [✅] Create `HttpEngineProvider.jvm.kt` in `src/jvmMain/kotlin/io/pexkit/api/internal/` using CIO engine (follow the same pattern as Android and iOS providers)

#### 7.2 Create JVM API Wrappers (Java-friendly)
Create separate blocking and async wrappers in `src/jvmMain/kotlin/io/pexkit/api/` for Java interoperability. The APIs are split into two distinct packages for cleaner usage:

Pure blocking wrappers using `runBlocking` — for synchronous Java code:

- [✅] Create `PexKitBlocking.kt` — Main entry point wrapping `PexKit`, implementing `AutoCloseable`, with `@JvmStatic` factory methods
- [✅] Create `PhotosApiBlocking.kt` — Wrapper for `PhotosApi` with: `search()`, `curated()`, `get()`
- [✅] Create `VideosApiBlocking.kt` — Wrapper for `VideosApi` with: `search()`, `popular()`, `get()`
- [✅] Create `CollectionsApiBlocking.kt` — Wrapper for `CollectionsApi` with: `featured()`, `my()`, `media()`

**Requirements for blocking wrappers:**
- Use `runBlocking` for all methods
- Throw `PexKitException` on errors (use `getOrThrow()`)
- Add `@JvmOverloads` for methods with default parameters
- Add KDoc documentation

Pure async wrappers returning `CompletableFuture` — for async Java code:

- [✅] Create `PexKitAsync.kt` — Main entry point wrapping `PexKit`, implementing `AutoCloseable`, with `@JvmStatic` factory methods, owns `ExecutorService`
- [✅] Create `PhotosApiAsync.kt` — Wrapper for `PhotosApi` with: `search()`, `curated()`, `get()` → all return `CompletableFuture`
- [✅] Create `VideosApiAsync.kt` — Wrapper for `VideosApi` with: `search()`, `popular()`, `get()` → all return `CompletableFuture`
- [✅] Create `CollectionsApiAsync.kt` — Wrapper for `CollectionsApi` with: `featured()`, `my()`, `media()` → all return `CompletableFuture`

**Requirements for async wrappers:**
- Use `CompletableFuture.supplyAsync()` with a cached thread pool
- Complete exceptionally with `PexKitException` on errors
- Add `@JvmOverloads` for methods with default parameters
- Add KDoc documentation
- Shutdown executor in `close()` method

#### 7.3 Add Java Interop Annotations
- [✅] Add `@JvmStatic` to companion object methods in existing code where appropriate
- [✅] Add `@JvmOverloads` to functions with default parameters for better Java interop
- [✅] Ensure enum values are accessible from Java

#### 7.4 JVM-Specific Tests
Create separate test files for blocking and async APIs:

**`src/jvmTest/kotlin/io/pexkit/api/BlockingApiTest.kt`:**
- [✅] Blocking search returns photos
- [✅] Blocking curated returns photos
- [✅] Blocking get photo/video by ID works
- [✅] Blocking API throws `PexKitException` on error (401, 404, 429)
- [✅] `AutoCloseable` works correctly (try-with-resources pattern)

**`src/jvmTest/kotlin/io/pexkit/api/AsyncApiTest.kt`:**
- [✅] Async search returns `CompletableFuture` that resolves correctly
- [✅] Async curated returns `CompletableFuture` that resolves correctly
- [✅] Async get photo/video by ID returns `CompletableFuture` that resolves correctly
- [✅] Async API completes exceptionally with `PexKitException` on error (401, 404, 429)
- [✅] `AutoCloseable` works correctly (try-with-resources pattern)

#### 7.5 Update Documentation
- [✅] Add "Backend Usage" section to README.md with examples for:
  - Kotlin backend usage (coroutines)
  - Kotlin backend usage (blocking API)
  - Java backend usage (blocking API)
  - Java backend usage (async with `CompletableFuture`)
- [✅] Document thread-safety considerations
- [✅] Add Spring Boot integration example

### Phase 8: JavaScript/Browser/Node.js Support 🆕
Add JS target to enable usage in browser applications (React, Vue, Angular) and Node.js backends.

#### 8.1 Add JS Target
- [ ] Add `js()` target in `pexkit-client/build.gradle.kts` with both browser and Node.js support
- [ ] Configure IR compiler (default in Kotlin 2.0+)
- [ ] Configure browser and Node.js test environments
- [ ] Generate TypeScript declarations for better IDE support

#### 8.2 Add JS HTTP Engine
- [ ] Add `ktor-client-js` dependency to `libs.versions.toml`
- [ ] Add JS engine dependency in `jsMain.dependencies` block
- [ ] Create `HttpEngineProvider.js.kt` in `src/jsMain/kotlin/io/pexkit/api/internal/` using Ktor JS engine (follow the same pattern as other platform providers)

#### 8.3 Create Promise-based API Wrappers (JavaScript-friendly)
Create JS-friendly wrappers in `src/jsMain/kotlin/io/pexkit/api/js/` for JavaScript interoperability. All methods must return `Promise` (no blocking APIs possible in JS):

- [ ] Create `PexKitJs.kt` — Main entry point for JS API with `@JsExport` annotation, exposing:
  - `photos` property returning `PhotosApiJs`
  - `videos` property returning `VideosApiJs`
  - `collections` property returning `CollectionsApiJs`
  - `close()` method
- [ ] Create `PhotosApiJs.kt` — Wrapper for `PhotosApi` returning `Promise`:
  - `search(query, options?)` → `Promise<PaginatedResponse<Photo>>`
  - `curated(options?)` → `Promise<PaginatedResponse<Photo>>`
  - `get(id)` → `Promise<Photo>`
- [ ] Create `VideosApiJs.kt` — Wrapper for `VideosApi` returning `Promise`:
  - `search(query, options?)` → `Promise<PaginatedResponse<Video>>`
  - `popular(options?)` → `Promise<PaginatedResponse<Video>>`
  - `get(id)` → `Promise<Video>`
- [ ] Create `CollectionsApiJs.kt` — Wrapper for `CollectionsApi` returning `Promise`:
  - `featured(options?)` → `Promise<PaginatedResponse<Collection>>`
  - `my(options?)` → `Promise<PaginatedResponse<Collection>>`
  - `media(id, options?)` → `Promise<PaginatedResponse<CollectionMedia>>`

**Requirements for JS wrappers:**
- Use `@JsExport` annotation on all public classes and functions
- Use `GlobalScope.promise { }` or `Promise` from `kotlin.js` to wrap coroutines
- Use `@JsName` annotation where needed to control generated JS names
- Create JS-friendly option objects instead of multiple parameters with defaults
- Handle errors by rejecting promises with meaningful error objects
- Add KDoc documentation

#### 8.4 Create JS-friendly Option/Config Classes
- [ ] Create `PexKitOptionsJs.kt` — Configuration object for JS with simple properties (apiKey, timeout, etc.)
- [ ] Create `SearchOptionsJs.kt` — Combined filters and pagination options for search methods
- [ ] Create `PaginationOptionsJs.kt` — Simple pagination object (page, perPage)
- [ ] Ensure all option classes are `@JsExport` annotated and use JS-friendly types

#### 8.5 TypeScript Declarations
- [ ] Enable TypeScript declaration generation in build.gradle.kts
- [ ] Verify generated `.d.ts` files are correct and usable
- [ ] Add manual type refinements if needed for better TypeScript experience

#### 8.6 JS-Specific Tests
Create `src/jsTest/kotlin/io/pexkit/api/JsApiTest.kt` with tests for:
- [ ] Promise-based search resolves correctly
- [ ] Promise-based search rejects on error (401, 404, etc.)
- [ ] Options objects work correctly
- [ ] All exported classes are accessible

#### 8.7 Update Documentation
- [ ] Add "Browser/Node.js Usage" section to README.md with examples for:
  - Installation via npm (if published) or direct usage
  - Browser usage (ES modules)
  - Node.js usage (CommonJS and ES modules)
  - TypeScript usage with type definitions
  - React/Vue integration example (optional)
- [ ] Document browser-specific considerations (CORS, etc.)
- [ ] Document Node.js-specific considerations

#### 8.8 Update Build Configuration
- [ ] Configure JS artifact output (UMD, CommonJS, ES modules)
- [ ] Ensure JS artifact is published alongside other platforms
- [ ] Configure npm publication settings (optional)
- [ ] Update CHANGELOG.md with JS support

#### 8.9 Verification
- [ ] Run `./gradlew jsTest` — all JS tests pass
- [ ] Run `./gradlew jsBrowserTest` — browser tests pass
- [ ] Run `./gradlew jsNodeTest` — Node.js tests pass
- [ ] Run `./gradlew build` — all platforms build successfully
- [ ] Test importing in a sample browser project (vanilla JS or React)
- [ ] Test importing in a sample Node.js project
- [ ] Verify TypeScript declarations work in a TS project

---

## 🧪 Testing Strategy

### Unit Tests (commonTest)
```kotlin
class PhotoSerializationTest {
    @Test
    fun `deserialize photo response correctly`() {
        val json = """{"id": 123, "width": 1920, ...}"""
        val photo = Json.decodeFromString<Photo>(json)
        assertEquals(123, photo.id)
    }
}
```

### Mock HTTP Tests
```kotlin
class PexKitClientTest {
    private val mockEngine = MockEngine { request ->
        when (request.url.encodedPath) {
            "/v1/search" -> respond(
                content = MockResponses.SEARCH_PHOTOS,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
            else -> respondError(HttpStatusCode.NotFound)
        }
    }
    
    private val client = PexKit("test-key") {
        httpClientEngine = mockEngine
    }
    
    @Test
    fun `search returns paginated photos`() = runTest {
        val result = client.photos.search("nature")
        assertIs<PexKitResult.Success<*>>(result)
        assertTrue(result.data.data.isNotEmpty())
    }
}
```

### JVM Blocking API Tests (jvmTest)
```kotlin
class BlockingApiTest {
    @Test
    fun `blocking search works`() {
        val client = PexKitBlocking.create {
            apiKey = "test"
            httpClientEngine = mockEngine
        }
        val result = client.photos.search("nature")
        assertTrue(result.data.isNotEmpty())
    }
}
```

### JVM Async API Tests (jvmTest)
```kotlin
class AsyncApiTest {
    @Test
    fun `async search returns CompletableFuture that resolves`() {
        val client = PexKitAsync.create {
            apiKey = "test"
            httpClientEngine = mockEngine
        }
        val future = client.photos.search("nature")
        val result = future.get() // Blocking wait
        assertTrue(result.data.isNotEmpty())
    }
}
```

---

## 🚀 Build Commands

```bash
# Build all targets
./gradlew build

# Run all tests
./gradlew allTests

# Run only common tests
./gradlew :pexkit-client:testDebugUnitTest

# Run only JVM tests
./gradlew :pexkit-client:jvmTest

# Run only JS tests (all)
./gradlew :pexkit-client:jsTest

# Run only JS browser tests
./gradlew :pexkit-client:jsBrowserTest

# Run only JS Node.js tests
./gradlew :pexkit-client:jsNodeTest

# Publish to local Maven
./gradlew publishToMavenLocal

# Publish to Maven Central (requires signing configured)
./gradlew publishAllPublicationsToMavenCentralRepository

# Check for dependency updates
./gradlew dependencyUpdates

# Format code (if using ktlint)
./gradlew ktlintFormat
```

---

## 📝 Notes for Development

1. **Always prefer idiomatic Kotlin** over Java-style code
2. **Use explicit visibility modifiers** (`public`, `internal`, `private`)
3. **Avoid `lateinit`** — prefer lazy initialization or nullable with defaults
4. **All API methods must be `suspend`** — no blocking calls in commonMain
5. **Return `Result` types** — don't throw exceptions for expected errors
6. **Keep platform-specific code minimal** — only HTTP engine differs
7. **Test serialization thoroughly** — API responses are the contract
8. **Document nullability** — when can fields be null?
9. **Include request/response logging** — essential for debugging
10. **For JVM blocking APIs** — use `runBlocking` and `@JvmOverloads` for Java interop
11. **Thread safety** — document that PexKit instances are thread-safe
12. **Resource cleanup** — ensure `close()` is called, implement `AutoCloseable` for Java try-with-resources
13. **For JS APIs** — use `@JsExport`, return `Promise`, no blocking operations allowed
14. **JS naming** — use `@JsName` to ensure clean JavaScript API names
15. **TypeScript** — ensure generated declarations are usable and well-typed

---

## 🔗 Useful Links

- [Pexels API Documentation](https://www.pexels.com/api/documentation/)
- [Ktor Client Documentation](https://ktor.io/docs/client.html)
- [Kotlin Multiplatform Guide](https://kotlinlang.org/docs/multiplatform.html)
- [kotlinx.serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md)
- [Publishing to Maven Central](https://central.sonatype.org/publish/publish-guide/)
- [Kotlin/Java Interop](https://kotlinlang.org/docs/java-to-kotlin-interop.html)

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.