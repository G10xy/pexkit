package io.pexkit.api.response

/**
 * Rate limit information extracted from API response headers.
 *
 * @property limit Total number of requests allowed per hour. Returns `0` if the header is missing.
 * @property remaining Number of requests remaining in the current window. Returns `0` if the header is missing.
 * @property reset Unix timestamp when the rate limit resets. Returns `0L` if the header is missing.
 */
public data class RateLimitInfo(
    val limit: Int,
    val remaining: Int,
    val reset: Long,
)
