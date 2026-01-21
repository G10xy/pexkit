package io.pexkit.api.response

/**
 * Represents errors that can occur when calling the Pexels API.
 */
public sealed interface PexKitError {
    /** Human-readable error message. */
    public val message: String

    /**
     * The API key is missing or invalid.
     * HTTP status: 401
     */
    public data class Unauthorized(
        override val message: String = "Invalid or missing API key",
    ) : PexKitError

    /**
     * Access to the requested resource is forbidden.
     * HTTP status: 403
     */
    public data class Forbidden(
        override val message: String = "Access forbidden",
    ) : PexKitError

    /**
     * The requested resource was not found.
     * HTTP status: 404
     *
     * @property resource Description of the resource that was not found.
     */
    public data class NotFound(
        val resource: String,
        override val message: String = "Resource not found: $resource",
    ) : PexKitError

    /**
     * Rate limit exceeded. Wait before making more requests.
     * HTTP status: 429
     *
     * @property retryAfter Seconds to wait before retrying.
     */
    public data class RateLimited(
        val retryAfter: Int?,
        override val message: String = "Rate limit exceeded" +
            (retryAfter?.let { ". Retry after $it seconds" } ?: ""),
    ) : PexKitError

    /**
     * Server error on Pexels' side.
     * HTTP status: 5xx
     *
     * @property statusCode The HTTP status code.
     */
    public data class ServerError(
        val statusCode: Int,
        override val message: String = "Server error: $statusCode",
    ) : PexKitError

    /**
     * Network error (connection failed, timeout, etc.).
     *
     * @property cause The underlying exception.
     */
    public data class NetworkError(
        val cause: Throwable,
        override val message: String = cause.message ?: "Network error",
    ) : PexKitError

    /**
     * An unknown error occurred.
     *
     * @property statusCode The HTTP status code, if available.
     * @property body The response body, if available.
     */
    public data class Unknown(
        val statusCode: Int?,
        val body: String?,
        override val message: String = "Unknown error" +
            (statusCode?.let { " (status: $it)" } ?: "") +
            (body?.let { ": $it" } ?: ""),
    ) : PexKitError
}

/**
 * Converts this error to an exception for use with throwing APIs.
 */
public fun PexKitError.toException(): PexKitException = PexKitException(this)

/**
 * Exception wrapper for [PexKitError].
 *
 * This is provided for interoperability with code that expects exceptions.
 * Prefer using [PexKitResult] pattern matching when possible.
 *
 * For [PexKitError.NetworkError], the original exception is preserved as the cause.
 */
public class PexKitException(
    public val error: PexKitError,
) : Exception(error.message, (error as? PexKitError.NetworkError)?.cause)
