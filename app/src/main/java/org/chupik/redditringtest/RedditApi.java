package org.chupik.redditringtest;

import android.util.Log;

import androidx.annotation.WorkerThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class RedditApi {

    private final String APPLICATION_ID = "zz7sp9sNJkMwMA";
    private final String USER_AGENT = "android:" + BuildConfig.APPLICATION_ID + ":v" + BuildConfig.VERSION_NAME + " (by /u/pchupik)";

    private String accessToken;
    private long expiresAt;
    private String uuid;

    private Prefs prefs;
    private final Retrofit retrofit;
    private final RedditService redditService;

    @Inject
    public RedditApi(Prefs prefs, OkHttpClient okHttpClient) {
        this.prefs = prefs;
        this.accessToken = prefs.getToken();
        this.expiresAt = prefs.getTokenExpirationDate();
        this.uuid = prefs.getUUID();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://oauth.reddit.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        redditService = retrofit.create(RedditService.class);
    }

    private boolean isTokenValid(){
        return accessToken != null && Calendar.getInstance().getTimeInMillis() < expiresAt;
    }

    @WorkerThread
    void requestToken() {
        HashMap<String, String> body = new HashMap<String, String>() {{
            put("grant_type", "https://oauth.reddit.com/grants/installed_client");
            put("device_id", uuid);
        }};
        String credential = Credentials.basic(APPLICATION_ID, "");
        Call<AuthResult> tokenCall = redditService.token(body, credential);

        try {
            AuthResult authResult = tokenCall.execute().body();
            accessToken = authResult.getAccessToken();
            expiresAt = authResult.getExpiresAt();

            prefs.store(accessToken, expiresAt);

        } catch (IOException  e) {
            Log.e("RedditApi", "requestToken", e);
        }

    }

    @WorkerThread
    public List<Post> requestTopPosts(int limit, String after){
        if (!isTokenValid())
            requestToken();

        Call<Top> posts = redditService.posts(limit, after, USER_AGENT, "bearer " + accessToken);
        try {
            retrofit2.Response<Top> response = posts.execute();
            Top top = response.body();
            List<Post> list = top.getPosts();
            return list;
        } catch (IOException e) {
            Log.e("RedditApi", "requestData", e);
        }
        return new ArrayList<>();
    }

}
