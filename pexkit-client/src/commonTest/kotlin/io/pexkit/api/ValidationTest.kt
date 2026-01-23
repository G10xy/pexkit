package io.pexkit.api

import io.pexkit.api.internal.CollectionMediaApiResponse
import io.pexkit.api.internal.Endpoints
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.model.asPhoto
import io.pexkit.api.model.asUnknown
import io.pexkit.api.model.asVideo
import io.pexkit.api.model.aspectRatio
import io.pexkit.api.response.PexKitError
import io.pexkit.api.response.PexKitException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class ValidationTest {

    @Test
    fun collectionIdValidAlphanumeric() {
        val url = Endpoints.collectionMedia("abc123")
        assertEquals("https://api.pexels.com/v1/collections/abc123", url)
    }

    @Test
    fun collectionIdValidUppercase() {
        val url = Endpoints.collectionMedia("ABC123")
        assertEquals("https://api.pexels.com/v1/collections/ABC123", url)
    }

    @Test
    fun collectionIdValidMixedCase() {
        val url = Endpoints.collectionMedia("AbC123xYz")
        assertEquals("https://api.pexels.com/v1/collections/AbC123xYz", url)
    }

    @Test
    fun collectionIdRejectsSpecialCharacters() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("abc-123")
        }.also {
            assertEquals(it.message?.contains("alphanumeric"), true)
        }
    }

    @Test
    fun collectionIdRejectsPathTraversal() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("../etc/passwd")
        }
    }

    @Test
    fun collectionIdRejectsSlash() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("abc/123")
        }
    }

    @Test
    fun collectionIdRejectsSpaces() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("abc 123")
        }
    }

    @Test
    fun collectionIdRejectsEmpty() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("")
        }
    }


    @Test
    fun photosSearchRejectsBlankQuery() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.photos.search("")
        }.also {
            assertEquals(it.message?.contains("blank"), true)
        }

        client.close()
    }

    @Test
    fun photosSearchRejectsWhitespaceOnlyQuery() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.photos.search("   ")
        }

        client.close()
    }

    @Test
    fun photosSearchRejectsQueryExceeding200Characters() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)
        val longQuery = "a".repeat(201)

        assertFailsWith<IllegalArgumentException> {
            client.photos.search(longQuery)
        }.also {
            assertEquals(it.message?.contains("200"), true)
        }

        client.close()
    }

    @Test
    fun photosSearchAcceptsQueryAt200Characters() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)
        val query200 = "a".repeat(200)

        val result = client.photos.search(query200)
        assertTrue(result is io.pexkit.api.response.PexKitResult.Success)

        client.close()
    }

    @Test
    fun videosSearchRejectsBlankQuery() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.videos.search("")
        }.also {
            assertEquals(it.message?.contains("blank"), true)
        }

        client.close()
    }

    @Test
    fun videosSearchRejectsWhitespaceOnlyQuery() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.videos.search("   ")
        }

        client.close()
    }

    @Test
    fun videosSearchRejectsQueryExceeding200Characters() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)
        val longQuery = "a".repeat(201)

        assertFailsWith<IllegalArgumentException> {
            client.videos.search(longQuery)
        }.also {
            assertEquals(it.message?.contains("200"), true)
        }

        client.close()
    }

    @Test
    fun videosSearchAcceptsQueryAt200Characters() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)
        val query200 = "a".repeat(200)

        val result = client.videos.search(query200)
        assertTrue(result is io.pexkit.api.response.PexKitResult.Success)

        client.close()
    }


    @Test
    fun asPhotoReturnsNullForUnknown() {
        val unknown: CollectionMedia = CollectionMedia.Unknown(
            id = 1L,
            width = 100,
            height = 100,
            url = "https://example.com",
            originalType = "NewType",
        )

        assertNull(unknown.asPhoto())
    }

    @Test
    fun asVideoReturnsNullForUnknown() {
        val unknown: CollectionMedia = CollectionMedia.Unknown(
            id = 1L,
            width = 100,
            height = 100,
            url = "https://example.com",
            originalType = "NewType",
        )

        assertNull(unknown.asVideo())
    }

    @Test
    fun photoAspectRatioCalculatesCorrectly() {
        val photo = MockData.photo
        val expectedRatio = photo.width.toFloat() / photo.height
        assertEquals(expectedRatio, photo.aspectRatio(), 0.001f)
    }

    @Test
    fun photoAspectRatioThrowsWhenHeightIsZero() {
        val photo = MockData.photo.copy(height = 0)

        assertFailsWith<IllegalStateException> {
            photo.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun photoAspectRatioThrowsWhenHeightIsNegative() {
        val photo = MockData.photo.copy(height = -1)

        assertFailsWith<IllegalStateException> {
            photo.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun videoAspectRatioCalculatesCorrectly() {
        val video = MockData.video
        val expectedRatio = video.width.toFloat() / video.height
        assertEquals(expectedRatio, video.aspectRatio(), 0.001f)
    }

    @Test
    fun videoAspectRatioThrowsWhenHeightIsZero() {
        val video = MockData.video.copy(height = 0)

        assertFailsWith<IllegalStateException> {
            video.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun videoAspectRatioThrowsWhenHeightIsNegative() {
        val video = MockData.video.copy(height = -1)

        assertFailsWith<IllegalStateException> {
            video.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun networkErrorPreservesCauseInException() {
        val originalException = RuntimeException("Connection refused")
        val networkError = PexKitError.NetworkError(cause = originalException)
        val pexKitException = PexKitException(networkError)

        assertEquals(originalException, pexKitException.cause)
        assertEquals("Connection refused", pexKitException.cause?.message)
    }

    @Test
    fun unauthorizedErrorHasNullCause() {
        val error = PexKitError.Unauthorized()
        val exception = PexKitException(error)

        assertNull(exception.cause)
        assertEquals("Invalid or missing API key", exception.message)
    }

    @Test
    fun forbiddenErrorHasNullCause() {
        val error = PexKitError.Forbidden()
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun notFoundErrorHasNullCause() {
        val error = PexKitError.NotFound(resource = "photo/123")
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun rateLimitedErrorHasNullCause() {
        val error = PexKitError.RateLimited(retryAfter = 60)
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun serverErrorHasNullCause() {
        val error = PexKitError.ServerError(statusCode = 500)
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun unknownErrorHasNullCause() {
        val error = PexKitError.Unknown(statusCode = 418, body = "I'm a teapot")
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun exceptionCauseChainIsPreserved() {
        val rootCause = IllegalStateException("Root cause")
        val intermediateCause = RuntimeException("Intermediate", rootCause)
        val networkError = PexKitError.NetworkError(cause = intermediateCause)
        val pexKitException = PexKitException(networkError)

        assertEquals(intermediateCause, pexKitException.cause)
        assertEquals(rootCause, pexKitException.cause?.cause)
    }
}
