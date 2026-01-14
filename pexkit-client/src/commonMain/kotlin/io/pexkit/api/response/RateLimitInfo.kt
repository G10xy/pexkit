package io.pexkit.api.response

/**
 * Rate limit information extracted from API response headers.
 *
 * @property limit Total number of requests allowed per hour.
 * @property remaining Number of requests remaining in the current window.
 * @property reset Unix timestamp when the rate limit resets.
 */
public data class RateLimitInfo(
    val limit: Int,
    val remaining: Int,
    val reset: Long,
)
