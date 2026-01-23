# PexKit

A Kotlin Multiplatform client library for the [Pexels API](https://www.pexels.com/api/), targeting Android, iOS, and JVM backends.

PexKit provides a type-safe, coroutine-based API to search and retrieve high-quality stock photos and videos from Pexels.


## Features

- **Kotlin Multiplatform** - Works on Android, iOS, and JVM from a single codebase
- **Type-safe API** - Full Kotlin data classes with serialization
- **Coroutine-based** - All API calls are suspend functions
- **Java-friendly** - Blocking and async APIs for JVM/Java backends
- **Result types** - No exceptions for expected errors, use `PexKitResult` for clean error handling
- **Rate limit aware** - Every response includes rate limit information
- **Minimal dependencies** - Built on Ktor and kotlinx.serialization

<details>
<summary><h2>Installation</h2></summary>

Add the dependency to your `build.gradle.kts`:
```kotlin
// In your shared module or app module
dependencies {
    implementation("io.pexkit:pexkit-client:0.1.0")
}
```

### Platform-specific setup

**Android** - No additional setup required. PexKit uses OkHttp under the hood.

**iOS** - No additional setup required. PexKit uses the Darwin (URLSession) engine.

**JVM** - No additional setup required. PexKit uses the CIO (Coroutine I/O) engine. Requires Java 21+.
</details>
<details>
<summary><h2>Quick Start</h2></summary>

### 1. Get your API key

Sign up at [Pexels API](https://www.pexels.com/api/) to get your free API key.

### 2. Create a client

```kotlin
import io.pexkit.api.PexKit

// Simple initialization
val pexkit = PexKit("YOUR_API_KEY")

// Or with custom configuration
val pexkit = PexKit {
    apiKey = "YOUR_API_KEY"
    defaultPerPage = 20
    timeout = 30.seconds
    logLevel = LogLevel.HEADERS  // NONE, HEADERS, or BODY
}
```

### 3. Make your first request

```kotlin
import io.pexkit.api.response.PexKitResult

when (val result = pexkit.photos.search("nature")) {
    is PexKitResult.Success -> {
        val photos = result.data.data
        photos.forEach { photo ->
            println("${photo.photographer}: ${photo.src.medium}")
        }

        // Rate limit info is always available
        println("Requests remaining: ${result.rateLimit.remaining}")
    }
    is PexKitResult.Failure -> {
        println("Error: ${result.error.message}")
    }
}

// Don't forget to close when done
pexkit.close()
```

</details>

<details>
<summary><h2>API Reference</h2></summary>

### Photos

```kotlin
// Search photos
val result = pexkit.photos.search(
    query = "mountains",
    filters = PhotoFilters(
        orientation = Orientation.LANDSCAPE,
        size = Size.LARGE,
        color = "blue",  // or use Color.BLUE
    ),
    pagination = PaginationParams(page = 1, perPage = 20),
)

// Get curated photos (trending, updated hourly)
val curated = pexkit.photos.curated()

// Get a specific photo by ID
val photo = pexkit.photos.get(2014422)
```

**Photo object properties:**
- `id` - Unique identifier
- `width`, `height` - Dimensions in pixels
- `url` - Pexels page URL
- `photographer`, `photographerUrl`, `photographerId` - Photographer info
- `avgColor` - Average color as hex string
- `src` - Available sizes: `original`, `large2x`, `large`, `medium`, `small`, `portrait`, `landscape`, `tiny`
- `alt` - Alt text description
- `liked` - Whether liked by API key owner

### Videos

```kotlin
// Search videos
val result = pexkit.videos.search(
    query = "ocean waves",
    filters = VideoFilters(
        orientation = Orientation.LANDSCAPE,
        minWidth = 1920,
        minHeight = 1080,
        minDuration = 10,
        maxDuration = 60,
    ),
    pagination = PaginationParams(page = 1, perPage = 15),
)

// Get popular videos
val popular = pexkit.videos.popular()

// Get a specific video by ID
val video = pexkit.videos.get(857251)
```

**Video object properties:**
- `id` - Unique identifier
- `width`, `height` - Dimensions in pixels
- `url` - Pexels page URL
- `image` - Thumbnail URL
- `duration` - Duration in seconds
- `user` - Videographer info (`id`, `name`, `url`)
- `videoFiles` - List of available files with `quality`, `fileType`, `width`, `height`, `fps`, `link`
- `videoPictures` - Preview thumbnails

### Collections

```kotlin
// Get featured collections
val featured = pexkit.collections.featured()

// Get your own collections
val myCollections = pexkit.collections.my()

// Get media from a collection
val media = pexkit.collections.media(
    id = "abc123",
    type = MediaType.PHOTOS,  // or VIDEOS, or null for both
    pagination = PaginationParams(page = 1, perPage = 20),
)

// Collection media can be photos or videos
when (val result = pexkit.collections.media("abc123")) {
    is PexKitResult.Success -> {
        result.data.data.forEach { item ->
            when (item) {
                is CollectionMedia.PhotoMedia -> println("Photo: ${item.photographer}")
                is CollectionMedia.VideoMedia -> println("Video: ${item.user.name}")
            }
        }
    }
    is PexKitResult.Failure -> { /* handle error */ }
}
```

**Collection object properties:**
- `id` - Unique identifier
- `title` - Collection title
- `description` - Collection description
- `private` - Whether the collection is private
- `mediaCount`, `photosCount`, `videosCount` - Media counts

</details>

<details>
<summary><h2>Pagination</h2></summary>

All list endpoints return paginated responses:

```kotlin
val result = pexkit.photos.search("cats")

if (result is PexKitResult.Success) {
    val response = result.data

    println("Page ${response.page} of results")
    println("${response.perPage} items per page")
    println("${response.totalResults} total results")

    if (response.hasNextPage) {
        // Fetch next page
        val nextPage = pexkit.photos.search(
            "cats",
            pagination = PaginationParams(page = response.page + 1)
        )
    }
}
```

</details>

<details>
<summary><h2>Error Handling</h2></summary>

PexKit uses a `PexKitResult` sealed class instead of throwing exceptions:

```kotlin
when (val result = pexkit.photos.search("nature")) {
    is PexKitResult.Success -> {
        // Use result.data
    }
    is PexKitResult.Failure -> {
        when (val error = result.error) {
            is PexKitError.Unauthorized -> {
                // Invalid or missing API key (401)
            }
            is PexKitError.Forbidden -> {
                // Access forbidden (403)
            }
            is PexKitError.NotFound -> {
                // Resource not found (404)
                println("Not found: ${error.resource}")
            }
            is PexKitError.RateLimited -> {
                // Too many requests (429)
                println("Retry after ${error.retryAfter} seconds")
            }
            is PexKitError.ServerError -> {
                // Server error (5xx)
                println("Server error: ${error.statusCode}")
            }
            is PexKitError.NetworkError -> {
                // Connection failed, timeout, etc.
                println("Network error: ${error.cause.message}")
            }
            is PexKitError.Unknown -> {
                // Unexpected error
                println("Unknown error: ${error.statusCode} - ${error.body}")
            }
        }
    }
}
```

### Convenience extensions

```kotlin
// Get data or null
val photos = result.getOrNull()

// Get data or throw exception
val photos = result.getOrThrow()

// Get data or default value
val photos = result.getOrDefault(emptyList())

// Get data or compute fallback (lazy, has access to error)
val photos = result.getOrElse { error ->
    logger.warn("Failed: ${error.message}")
    loadFromCache()
}

// Transform success data
val photoCount = result.map { it.totalResults }

// Side effects
result
    .onSuccess { data -> updateUI(data) }
    .onFailure { error -> showError(error) }
```

</details>

<details>
<summary><h2>Configuration Options</h2></summary>

```kotlin
val pexkit = PexKit {
    // Required: Your Pexels API key
    apiKey = "YOUR_API_KEY"

    // Default results per page (1-80, default: 15)
    defaultPerPage = 20

    // Request timeout (default: 30 seconds)
    timeout = 60.seconds

    // Logging level (default: NONE)
    logLevel = LogLevel.BODY  // NONE, HEADERS, BODY

    // Custom HTTP engine (for testing)
    httpClientEngine = mockEngine
}
```

</details>

<details>
<summary><h2>Rate Limits</h2></summary>

Pexels API has rate limits (default: 200 requests/hour). Every successful response includes rate limit information:

```kotlin
when (val result = pexkit.photos.search("nature")) {
    is PexKitResult.Success -> {
        val rateLimit = result.rateLimit
        println("Limit: ${rateLimit.limit}")
        println("Remaining: ${rateLimit.remaining}")
        println("Resets at: ${rateLimit.reset}")  // Unix timestamp
    }
    is PexKitResult.Failure -> {
        if (result.error is PexKitError.RateLimited) {
            val retryAfter = (result.error as PexKitError.RateLimited).retryAfter
            println("Rate limited. Retry after $retryAfter seconds")
        }
    }
}
```

</details>

<details>
<summary><h2>Filters Reference</h2></summary>

### Photo Filters

| Filter | Values |
|--------|--------|
| `orientation` | `LANDSCAPE`, `PORTRAIT`, `SQUARE` |
| `size` | `LARGE` (24MP), `MEDIUM` (12MP), `SMALL` (4MP) |
| `color` | Hex code without # (e.g., `"FF5733"`) or predefined: `RED`, `ORANGE`, `YELLOW`, `GREEN`, `TURQUOISE`, `BLUE`, `VIOLET`, `PINK`, `BROWN`, `BLACK`, `GRAY`, `WHITE` |
| `locale` | `EN_US`, `DE_DE`, `FR_FR`, `ES_ES`, `IT_IT`, `JA_JP`, and more |

### Video Filters

| Filter | Description |
|--------|-------------|
| `orientation` | `LANDSCAPE`, `PORTRAIT`, `SQUARE` |
| `size` | `LARGE`, `MEDIUM`, `SMALL` |
| `locale` | Same as photo filters |
| `minWidth` | Minimum width in pixels |
| `minHeight` | Minimum height in pixels |
| `minDuration` | Minimum duration in seconds |
| `maxDuration` | Maximum duration in seconds |

</details>

<details>
<summary><h2>Backend Usage (JVM)</h2></summary>

PexKit provides full support for JVM backends, including Spring Boot, Ktor Server, and standalone applications. Choose between coroutine-based, blocking, or async APIs based on your needs.

### Kotlin Backend (Coroutines)

For Kotlin backends using coroutines (recommended):

```kotlin
import io.pexkit.api.PexKit
import io.pexkit.api.response.PexKitResult

class PhotoService {
    private val pexkit = PexKit("YOUR_API_KEY")

    suspend fun searchPhotos(query: String): List<Photo> {
        return when (val result = pexkit.photos.search(query)) {
            is PexKitResult.Success -> result.data.data
            is PexKitResult.Failure -> throw result.error.toException()
        }
    }

    fun close() = pexkit.close()
}
```

### Kotlin Backend (Blocking API)

For Kotlin code that doesn't use coroutines:

```kotlin
import io.pexkit.api.blocking.PexKitBlocking

class PhotoService {
    private val pexkit = PexKitBlocking.create("YOUR_API_KEY")

    fun searchPhotos(query: String): List<Photo> {
        // Blocking call - throws PexKitException on error
        return pexkit.photos.search(query).data
    }

    fun close() = pexkit.close()
}
```

### Java Backend (Blocking API)

For Java applications using blocking calls:

```java
import io.pexkit.api.blocking.PexKitBlocking;
import io.pexkit.api.model.Photo;
import io.pexkit.api.response.PaginatedResponse;
import io.pexkit.api.response.PexKitException;

public class PhotoService implements AutoCloseable {
    private final PexKitBlocking pexkit;

    public PhotoService(String apiKey) {
        this.pexkit = PexKitBlocking.create(apiKey);
    }

    public List<Photo> searchPhotos(String query) throws PexKitException {
        PaginatedResponse<Photo> response = pexkit.photos().search(query);
        return response.getData();
    }

    @Override
    public void close() {
        pexkit.close();
    }
}

// Usage with try-with-resources
try (PexKitBlocking pexkit = PexKitBlocking.create("YOUR_API_KEY")) {
    PaginatedResponse<Photo> photos = pexkit.photos().search("nature");
    photos.getData().forEach(photo ->
        System.out.println(photo.getPhotographer())
    );
}
```

### Java Backend (Async with CompletableFuture)

For Java applications using async/non-blocking patterns:

```java
import io.pexkit.api.blocking.PexKitBlocking;
import java.util.concurrent.CompletableFuture;

public class PhotoService {
    private final PexKitBlocking pexkit = PexKitBlocking.create("YOUR_API_KEY");

    public CompletableFuture<List<Photo>> searchPhotosAsync(String query) {
        return pexkit.photos().searchAsync(query)
            .thenApply(response -> response.getData());
    }

    // Chain multiple async operations
    public CompletableFuture<Void> processPhotos(String query) {
        return pexkit.photos().searchAsync(query)
            .thenAccept(response -> {
                response.getData().forEach(photo ->
                    processPhoto(photo)
                );
            })
            .exceptionally(ex -> {
                logger.error("Failed to search photos", ex);
                return null;
            });
    }
}
```

### Spring Boot Integration

Example service for Spring Boot applications:

```kotlin
import io.pexkit.api.blocking.PexKitBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import jakarta.annotation.PreDestroy

@Service
class PexelsService(
    @Value("\${pexels.api-key}") apiKey: String
) {
    private val pexkit = PexKitBlocking.create(apiKey)

    fun searchPhotos(query: String, page: Int = 1, perPage: Int = 15): PaginatedResponse<Photo> {
        return pexkit.photos.search(
            query = query,
            pagination = PaginationParams(page = page, perPage = perPage)
        )
    }

    fun getPhoto(id: Long): Photo = pexkit.photos.get(id)

    @PreDestroy
    fun cleanup() = pexkit.close()
}
```

Or in Java:

```java
import io.pexkit.api.blocking.PexKitBlocking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

@Service
public class PexelsService {
    private final PexKitBlocking pexkit;

    public PexelsService(@Value("${pexels.api-key}") String apiKey) {
        this.pexkit = PexKitBlocking.create(apiKey);
    }

    public PaginatedResponse<Photo> searchPhotos(String query) {
        return pexkit.photos().search(query);
    }

    @PreDestroy
    public void cleanup() {
        pexkit.close();
    }
}
```

### Thread Safety

PexKit and PexKitBlocking instances are **thread-safe** and can be shared across multiple threads. You should:

- Create a single instance and reuse it (e.g., as a singleton or Spring bean)
- Call `close()` when your application shuts down to release resources
- For Spring applications, use `@PreDestroy` to ensure proper cleanup

### Threading Considerations

#### Blocking API (`PexKitBlocking`)

The blocking API uses `runBlocking` internally to bridge suspend functions to blocking calls. Be aware of the following:

- **Do not call from Android main thread:** Calling blocking methods on the Android main (UI) thread will cause an ANR (Application Not Responding) error.
- **Do not call from coroutine dispatchers:** Calling from within a coroutine context (e.g., `Dispatchers.Default` or `Dispatchers.IO`) may cause deadlocks in some configurations.
- **Recommended:** Use the suspend-based `PexKit` API when working with coroutines, or `PexKitAsync` for Java's `CompletableFuture` pattern.

#### Async API (`PexKitAsync`)

The async API returns `CompletableFuture` for Java interoperability. Note the following limitation:

- **Cancellation does not stop HTTP requests:** Cancelling a `CompletableFuture` returned by this API does **not** cancel the underlying HTTP request. The request will continue to completion even if the future is cancelled. This is a limitation of bridging coroutines to `CompletableFuture`.
- **For proper cancellation:** Use the suspend-based `PexKit` API with coroutines, which supports structured concurrency and cancellation.

### API Comparison

| API Style | Class | Returns | Error Handling |
|-----------|-------|---------|----------------|
| Coroutines (Kotlin) | `PexKit` | `PexKitResult<T>` | Pattern matching |
| Blocking (Kotlin/Java) | `PexKitBlocking` | `T` directly | Throws `PexKitException` |
| Async (Java) | `PexKitAsync` | `CompletableFuture<T>` | `.exceptionally()` / `.handle()` |

</details>


<summary><h2>Official Documentation</h2></summary>

For complete API documentation, rate limit details, and terms of use, visit the official Pexels API documentation:
**[Pexels API Documentation](https://www.pexels.com/api/documentation/)**

