package io.pexkit.api.response

/**
 * Represents the result of a PexKit API call.
 *
 * Use pattern matching to handle success and failure cases:
 * ```kotlin
 * when (val result = pexkit.photos.search("nature")) {
 *     is PexKitResult.Success -> {
 *         val photos = result.data
 *         val remaining = result.rateLimit.remaining
 *     }
 *     is PexKitResult.Failure -> {
 *         when (result.error) {
 *             is PexKitError.RateLimited -> // handle rate limiting
 *             is PexKitError.Unauthorized -> // handle auth error
 *             else -> // handle other errors
 *         }
 *     }
 * }
 * ```
 *
 * @param T The type of data returned on success.
 */
public sealed interface PexKitResult<out T> {
    /**
     * Represents a successful API response.
     *
     * @property data The response data.
     * @property rateLimit Rate limit information from the response headers.
     */
    public data class Success<T>(
        val data: T,
        val rateLimit: RateLimitInfo,
    ) : PexKitResult<T>

    /**
     * Represents a failed API response.
     *
     * @property error The error that occurred.
     */
    public data class Failure(
        val error: PexKitError,
    ) : PexKitResult<Nothing>
}

/**
 * Returns the data if this is a [PexKitResult.Success], or null otherwise.
 */
public fun <T> PexKitResult<T>.getOrNull(): T? = when (this) {
    is PexKitResult.Success -> data
    is PexKitResult.Failure -> null
}

/**
 * Returns the data if this is a [PexKitResult.Success], or throws the error otherwise.
 */
public fun <T> PexKitResult<T>.getOrThrow(): T = when (this) {
    is PexKitResult.Success -> data
    is PexKitResult.Failure -> throw error.toException()
}

/**
 * Returns the data if this is a [PexKitResult.Success], or the [default] value otherwise.
 *
 * Use this when you have a simple, pre-computed fallback value:
 * ```kotlin
 * val photos = result.getOrDefault(emptyList())
 * ```
 *
 * Note: The [default] value is evaluated eagerly, even if the result is a success.
 * Use [getOrElse] if computing the default is expensive or requires the error.
 */
public fun <T> PexKitResult<T>.getOrDefault(default: T): T = when (this) {
    is PexKitResult.Success -> data
    is PexKitResult.Failure -> default
}

/**
 * Returns the data if this is a [PexKitResult.Success], or the result of [default] otherwise.
 *
 * Use this when you need to compute the fallback lazily or need access to the error:
 * ```kotlin
 * val photos = result.getOrElse { error ->
 *     logger.warn("API failed: ${error.message}")
 *     loadPhotosFromCache()
 * }
 * ```
 *
 * The [default] lambda is only called on failure and receives the error for logging or decisions.
 */
public inline fun <T> PexKitResult<T>.getOrElse(default: (PexKitError) -> T): T = when (this) {
    is PexKitResult.Success -> data
    is PexKitResult.Failure -> default(error)
}

/**
 * Transforms the data if this is a [PexKitResult.Success], keeping failures unchanged.
 *
 * Use this to extract or transform data while preserving the result wrapper:
 * ```kotlin
 * val photoCountResult: PexKitResult<Int> = result.map { response ->
 *     response.totalResults
 * }
 * ```
 *
 * If the result is a [PexKitResult.Failure], it passes through unchanged.
 */
public inline fun <T, R> PexKitResult<T>.map(transform: (T) -> R): PexKitResult<R> = when (this) {
    is PexKitResult.Success -> PexKitResult.Success(transform(data), rateLimit)
    is PexKitResult.Failure -> this
}

/**
 * Executes [action] if this is a [PexKitResult.Success].
 */
public inline fun <T> PexKitResult<T>.onSuccess(action: (T) -> Unit): PexKitResult<T> {
    if (this is PexKitResult.Success) action(data)
    return this
}

/**
 * Executes [action] if this is a [PexKitResult.Failure].
 */
public inline fun <T> PexKitResult<T>.onFailure(action: (PexKitError) -> Unit): PexKitResult<T> {
    if (this is PexKitResult.Failure) action(error)
    return this
}
