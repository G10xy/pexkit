package io.pexkit.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A paginated response wrapper for list endpoints.
 *
 * @param T The type of items in the response.
 * @property data The list of items for the current page.
 * @property page Current page number.
 * @property perPage Number of items per page.
 * @property totalResults Total number of results available.
 * @property nextPage URL to fetch the next page, if available.
 * @property prevPage URL to fetch the previous page, if available.
 */
@Serializable
public data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("next_page") val nextPage: String? = null,
    @SerialName("prev_page") val prevPage: String? = null,
) {
    /** Returns true if there is a next page available. */
    val hasNextPage: Boolean get() = nextPage != null

    /** Returns true if there is a previous page available. */
    val hasPrevPage: Boolean get() = prevPage != null
}
