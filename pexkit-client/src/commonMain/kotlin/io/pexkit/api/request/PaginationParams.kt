package io.pexkit.api.request

import kotlin.jvm.JvmOverloads

/**
 * Pagination parameters for list endpoints.
 *
 * @property page Page number (1-indexed). Default: 1.
 * @property perPage Number of results per page (1-80). Default: uses client's defaultPerPage.
 */
public data class PaginationParams @JvmOverloads constructor(
    val page: Int = 1,
    val perPage: Int? = null,
) {
    init {
        require(page >= 1) { "page must be >= 1" }
        perPage?.let { require(it in 1..80) { "perPage must be between 1 and 80" } }
    }
}
