package io.pexkit.api

import io.pexkit.api.internal.Endpoints
import io.pexkit.api.model.CollectionMedia
import io.pexkit.api.model.asPhoto
import io.pexkit.api.model.asVideo
import io.pexkit.api.model.aspectRatio
import io.pexkit.api.response.PexKitError
import io.pexkit.api.response.PexKitException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue


class ValidationTest {

    @Test
    fun `collection ID accepts valid alphanumeric lowercase`() {
        val url = Endpoints.collectionMedia("abc123")
        assertEquals("https://api.pexels.com/v1/collections/abc123", url)
    }

    @Test
    fun `collection ID accepts valid alphanumeric uppercase`() {
        val url = Endpoints.collectionMedia("ABC123")
        assertEquals("https://api.pexels.com/v1/collections/ABC123", url)
    }

    @Test
    fun `collection ID accepts valid alphanumeric mixed case`() {
        val url = Endpoints.collectionMedia("AbC123xYz")
        assertEquals("https://api.pexels.com/v1/collections/AbC123xYz", url)
    }

    @Test
    fun `collection ID rejects special characters`() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("abc-123")
        }.also {
            assertEquals(it.message?.contains("alphanumeric"), true)
        }
    }

    @Test
    fun `collection ID rejects path traversal`() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("../etc/passwd")
        }
    }

    @Test
    fun `collection ID rejects slash`() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("abc/123")
        }
    }

    @Test
    fun `collection ID rejects spaces`() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("abc 123")
        }
    }

    @Test
    fun `collection ID rejects empty string`() {
        assertFailsWith<IllegalArgumentException> {
            Endpoints.collectionMedia("")
        }
    }


    @Test
    fun `photos search rejects blank query`() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.photos.search("")
        }.also {
            assertEquals(it.message?.contains("blank"), true)
        }

        client.close()
    }

    @Test
    fun `photos search rejects whitespace only query`() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.photos.search("   ")
        }

        client.close()
    }

    @Test
    fun `photos search rejects query exceeding 200 characters`() = runTest {
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
    fun `photos search accepts query at 200 characters`() = runTest {
        val client = createTestClientWithResponse(MockResponses.PHOTOS_SEARCH)
        val query200 = "a".repeat(200)

        val result = client.photos.search(query200)
        assertTrue(result is io.pexkit.api.response.PexKitResult.Success)

        client.close()
    }

    @Test
    fun `videos search rejects blank query`() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.videos.search("")
        }.also {
            assertEquals(it.message?.contains("blank"), true)
        }

        client.close()
    }

    @Test
    fun `videos search rejects whitespace only query`() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)

        assertFailsWith<IllegalArgumentException> {
            client.videos.search("   ")
        }

        client.close()
    }

    @Test
    fun `videos search rejects query exceeding 200 characters`() = runTest {
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
    fun `videos search accepts query at 200 characters`() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)
        val query200 = "a".repeat(200)

        val result = client.videos.search(query200)
        assertTrue(result is io.pexkit.api.response.PexKitResult.Success)

        client.close()
    }


    @Test
    fun `asPhoto returns null for Unknown media type`() {
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
    fun `asVideo returns null for Unknown media type`() {
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
    fun `photo aspectRatio calculates correctly`() {
        val photo = MockData.photo
        val expectedRatio = photo.width.toFloat() / photo.height
        assertEquals(expectedRatio, photo.aspectRatio(), 0.001f)
    }

    @Test
    fun `photo aspectRatio throws when height is zero`() {
        val photo = MockData.photo.copy(height = 0)

        assertFailsWith<IllegalStateException> {
            photo.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun `photo aspectRatio throws when height is negative`() {
        val photo = MockData.photo.copy(height = -1)

        assertFailsWith<IllegalStateException> {
            photo.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun `video aspectRatio calculates correctly`() {
        val video = MockData.video
        val expectedRatio = video.width.toFloat() / video.height
        assertEquals(expectedRatio, video.aspectRatio(), 0.001f)
    }

    @Test
    fun `video aspectRatio throws when height is zero`() {
        val video = MockData.video.copy(height = 0)

        assertFailsWith<IllegalStateException> {
            video.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun `video aspectRatio throws when height is negative`() {
        val video = MockData.video.copy(height = -1)

        assertFailsWith<IllegalStateException> {
            video.aspectRatio()
        }.also {
            assertEquals(it.message?.contains("height"), true)
        }
    }

    @Test
    fun `NetworkError preserves cause in exception`() {
        val originalException = RuntimeException("Connection refused")
        val networkError = PexKitError.NetworkError(cause = originalException)
        val pexKitException = PexKitException(networkError)

        assertEquals(originalException, pexKitException.cause)
        assertEquals("Connection refused", pexKitException.cause?.message)
    }

    @Test
    fun `Unauthorized error has null cause`() {
        val error = PexKitError.Unauthorized()
        val exception = PexKitException(error)

        assertNull(exception.cause)
        assertEquals("Invalid or missing API key", exception.message)
    }

    @Test
    fun `Forbidden error has null cause`() {
        val error = PexKitError.Forbidden()
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun `NotFound error has null cause`() {
        val error = PexKitError.NotFound(resource = "photo/123")
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun `RateLimited error has null cause`() {
        val error = PexKitError.RateLimited(retryAfter = 60)
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun `ServerError has null cause`() {
        val error = PexKitError.ServerError(statusCode = 500)
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun `Unknown error has null cause`() {
        val error = PexKitError.Unknown(statusCode = 418, body = "I'm a teapot")
        val exception = PexKitException(error)

        assertNull(exception.cause)
    }

    @Test
    fun `exception cause chain is fully preserved`() {
        val rootCause = IllegalStateException("Root cause")
        val intermediateCause = RuntimeException("Intermediate", rootCause)
        val networkError = PexKitError.NetworkError(cause = intermediateCause)
        val pexKitException = PexKitException(networkError)

        assertEquals(intermediateCause, pexKitException.cause)
        assertEquals(rootCause, pexKitException.cause?.cause)
    }
}
