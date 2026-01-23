package io.pexkit.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.pexkit.api.request.PaginationParams
import io.pexkit.api.request.VideoFilters
import io.pexkit.api.response.PexKitResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class VideosApiTest {

    @Test
    fun `search returns success with videos`() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)

        when (val result = client.videos.search("ocean")) {
            is PexKitResult.Success -> {
                val data = result.data
                assertEquals(1, data.data.size)
                assertEquals(MockData.video.id, data.data.first().id)
                assertEquals(5000, data.totalResults)
                assertTrue(data.hasNextPage)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun `search applies filters to request URL`() = runTest {
        var capturedUrl: String? = null

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = MockResponses.VIDEOS_SEARCH,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val client = PexKit {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        client.videos.search(
            query = "ocean",
            filters = VideoFilters(
                minWidth = 1920,
                minHeight = 1080,
                minDuration = 10,
                maxDuration = 60,
            ),
            pagination = PaginationParams(page = 1, perPage = 20),
        )

        assertNotNull(capturedUrl)
        assertTrue(capturedUrl.contains("query=ocean"))
        assertTrue(capturedUrl.contains("min_width=1920"))
        assertTrue(capturedUrl.contains("min_height=1080"))
        assertTrue(capturedUrl.contains("min_duration=10"))
        assertTrue(capturedUrl.contains("max_duration=60"))

        client.close()
    }

    @Test
    fun `popular returns success with videos`() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEOS_SEARCH)

        when (val result = client.videos.popular()) {
            is PexKitResult.Success -> {
                assertEquals(1, result.data.data.size)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun `popular applies filters to request URL`() = runTest {
        var capturedUrl: String? = null

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = MockResponses.VIDEOS_SEARCH,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val client = PexKit {
            apiKey = "test-key"
            httpClientEngine = mockEngine
        }

        client.videos.popular(
            filters = VideoFilters(
                minWidth = 3840,
                minDuration = 30,
            ),
        )

        assertNotNull(capturedUrl)
        assertTrue(capturedUrl.contains("min_width=3840"))
        assertTrue(capturedUrl.contains("min_duration=30"))

        client.close()
    }

    @Test
    fun `get by ID returns success with video`() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEO)

        when (val result = client.videos.get(MockData.video.id)) {
            is PexKitResult.Success -> {
                val video = result.data
                assertEquals(MockData.video.id, video.id)
                assertEquals(MockData.user.name, video.user.name)
                assertEquals(MockData.video.duration, video.duration)
                assertEquals(2, video.videoFiles.size)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }

    @Test
    fun `video files contain correct data`() = runTest {
        val client = createTestClientWithResponse(MockResponses.VIDEO)

        when (val result = client.videos.get(MockData.video.id)) {
            is PexKitResult.Success -> {
                val hdFile = result.data.videoFiles.first { it.quality == MockData.videoFileHd.quality }
                assertEquals(MockData.videoFileHd.width, hdFile.width)
                assertEquals(MockData.videoFileHd.height, hdFile.height)
                assertEquals(MockData.videoFileHd.fps, hdFile.fps)
                assertEquals(MockData.videoFileHd.fileType, hdFile.fileType)
            }
            is PexKitResult.Failure -> fail("Expected success but got: ${result.error}")
        }

        client.close()
    }
}
