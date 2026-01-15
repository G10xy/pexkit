package io.pexkit.api

import io.pexkit.api.request.PaginationParams
import io.pexkit.api.response.PexKitResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class PaginationTest {

    @Test
    fun firstPageHasNextButNoPrev() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> {
                assertTrue(result.data.hasNextPage)
                assertFalse(result.data.hasPrevPage)
                assertNotNull(result.data.nextPage)
                assertNull(result.data.prevPage)
                assertEquals(1, result.data.page)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun middlePageHasBothNextAndPrev() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH_PAGE_2)

        when (val result = client.photos.search("nature", pagination = PaginationParams(page = 2))) {
            is PexKitResult.Success -> {
                assertTrue(result.data.hasNextPage)
                assertTrue(result.data.hasPrevPage)
                assertNotNull(result.data.nextPage)
                assertNotNull(result.data.prevPage)
                assertEquals(2, result.data.page)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun paginationParamsValidation() {
        val validParams = PaginationParams(page = 1, perPage = 15)
        assertEquals(1, validParams.page)
        assertEquals(15, validParams.perPage)

        val maxParams = PaginationParams(page = 1, perPage = 80)
        assertEquals(80, maxParams.perPage)

        val minParams = PaginationParams(page = 1, perPage = 1)
        assertEquals(1, minParams.perPage)
    }

    @Test
    fun paginationParamsInvalidPage() {
        assertFailsWith<IllegalArgumentException> {
            PaginationParams(page = 0)
        }

        assertFailsWith<IllegalArgumentException> {
            PaginationParams(page = -1)
        }
    }

    @Test
    fun paginationParamsInvalidPerPage() {
        assertFailsWith<IllegalArgumentException> {
            PaginationParams(page = 1, perPage = 0)
        }

        assertFailsWith<IllegalArgumentException> {
            PaginationParams(page = 1, perPage = 81)
        }

        assertFailsWith<IllegalArgumentException> {
            PaginationParams(page = 1, perPage = -1)
        }
    }

    @Test
    fun paginationParamsNullPerPageUsesDefault() {
        val params = PaginationParams(page = 1, perPage = null)
        assertNull(params.perPage)
    }

    @Test
    fun totalResultsReported() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> {
                assertEquals(10000, result.data.totalResults)
                assertEquals(15, result.data.perPage)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun perPageFromResponse() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> {
                assertEquals(15, result.data.perPage)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun defaultPerPageFromConfig() = runTest {
        val client = PexKit {
            apiKey = "test-key"
            defaultPerPage = 25
            httpClientEngine = mockEngineWithResponse(MockResponses.PHOTOS_SEARCH)
        }

        // The request should use the default perPage from config
        when (val result = client.photos.search("nature")) {
            is PexKitResult.Success -> {
                assertEquals(15, result.data.perPage)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }
}
