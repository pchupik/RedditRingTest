package org.chupik.redditringtest

import okhttp3.*
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import java.io.IOException
import java.util.*
import org.mockito.Mockito.`when` as whenever


class RedditApiTest {

    @Test
    @Throws(IOException::class)
    fun requestTopPosts() {
        val prefsMock = mock(Prefs::class.java).apply {
            whenever(token).thenReturn("fake_token")
            whenever(tokenExpirationDate).thenReturn(Calendar.getInstance().timeInMillis + 60 * 1000)
        }
        val okHttpClientMock = mockOkHttpSingleResponse("sample_top_3.json")
        val api = RedditApi(prefsMock, okHttpClientMock)

        val posts = api.requestTopPosts(3, null)
        assertNotNull(posts)
        assertEquals(3, posts.size.toLong())

        assertEquals("t3_geqgdh",posts[0].name)
        assertEquals("15 years from now",posts[0].title)
        assertEquals("AwesomeAdviceBot", posts[0].author)
        assertEquals(1588792532 * 1000L, posts[0].date)
        assertEquals("https://b.thumbs.redditmedia.com/4lCajpDiKM6Qg53gLSMpraM-apNnDEhfah_a6YqcYHI.jpg", posts[0].thumbnail)
        assertEquals("https://i.redd.it/kvb5q07e27x41.jpg", posts[0].fullImage)
        assertEquals(1477, posts[0].commentsNumber)
        assertEquals("https://www.reddit.com/r/memes/comments/geqgdh/15_years_from_now/", posts[0].permalink)

        assertEquals("t3_gelw3r", posts[1].name)
        assertNull("Full image should be null because of enabled:false", posts[1].fullImage)

        assertEquals("t3_gepkkj", posts[2].name)
        assertEquals("To attack Costco", posts[2].title)

    }

    private fun mockOkHttpSingleResponse(responseFile: String) : OkHttpClient {
        val call = mock(Call::class.java).apply {
            whenever(execute()).thenReturn(mockResponse(getTextFromFile(responseFile)))
        }
        return mock(OkHttpClient::class.java).apply {
            whenever(newCall(any())).thenReturn(call)
        }
    }

    private fun mockResponse(content: String?): Response {
        val mockRequest = Request.Builder()
                .url("https://some-url.com")
                .build()
        return Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("")
                .body(ResponseBody.create(
                        MediaType.parse("application/json; charset=utf-8"), content))
                .build()
    }

    private fun getTextFromFile(fileName: String): String? = ClassLoader.getSystemResource(fileName).readText()

}