package org.chupik.redditringtest;

import androidx.annotation.WorkerThread;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    private final OkHttpClient okHttpClient;

    private Prefs prefs;
    private final Retrofit retrofit;
    private final RedditService redditService;

    @Inject
    public RedditApi(Prefs prefs, OkHttpClient okHttpClient) {
        this.prefs = prefs;
        this.accessToken = prefs.getToken();
        this.expiresAt = prefs.getTokenExpirationDate();
        this.uuid = prefs.getUUID();
        this.okHttpClient = okHttpClient;
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
    private void requestToken() {
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "https://oauth.reddit.com/grants/installed_client")
                .add("device_id", uuid)
                .build();
        String credential = Credentials.basic(APPLICATION_ID, "");
        Request request = new Request.Builder()
                .url("https://www.reddit.com/api/v1/access_token")
                .header("Authorization", credential)
                .post(formBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();

            accessToken = parseToken(responseString);
            expiresAt = parseExpiresAt(responseString);

            prefs.store(accessToken, expiresAt);

        } catch (IOException | JSONException e) {
            Log.e("RedditApi", "requestToken", e);
        }

    }

    private static String parseToken(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.optString("access_token");
    }

    private static long parseExpiresAt(String json) throws JSONException  {
        JSONObject jsonObject = new JSONObject(json);
        int expiresIn = jsonObject.optInt("expires_in", 0);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, expiresIn);
        return instance.getTimeInMillis();
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
