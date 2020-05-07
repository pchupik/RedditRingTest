package org.chupik.redditringtest

import okhttp3.*
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.IOException
import java.util.*
import org.mockito.Mockito.`when` as whenever


class RedditApiTest {

    @Test
    fun requestToken(){
        val prefsMock = mock(Prefs::class.java).apply {
            whenever(uuid).thenReturn("423e8944-908a-11ea-bb37-0242ac130002")
        }
        val okHttpClientMock = mockOkHttpSingleResponse("sample_token.json")
        val api = RedditApi(prefsMock, okHttpClientMock)

        api.requestToken()

        verify(prefsMock).store(
                eq("the_token"),
                longThat { it/1000 == expiresAt(3600) / 1000 }
        )
    }

    fun expiresAt(expiresIn : Int) : Long {
        val instance = Calendar.getInstance()
        instance.add(Calendar.SECOND, expiresIn)
        return instance.timeInMillis
    }

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
        assertEquals(1588792532 * 1000L, posts[0].dateInMiliseconds)
        assertEquals("https://b.thumbs.redditmedia.com/4lCajpDiKM6Qg53gLSMpraM-apNnDEhfah_a6YqcYHI.jpg", posts[0].thumbnail)
        assertEquals("https://i.redd.it/kvb5q07e27x41.jpg", posts[0].fullImageIfEnabled)
        assertEquals(1477, posts[0].commentsNumber)
        assertEquals("https://www.reddit.com/r/memes/comments/geqgdh/15_years_from_now/", posts[0].fullPermaLink)

        assertEquals("t3_gelw3r", posts[1].name)
        assertNull("Full image should be null because of enabled:false", posts[1].fullImageIfEnabled)

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