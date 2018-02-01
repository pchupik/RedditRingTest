package org.chupik.redditringtest;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RedditApi {

    private final String APPLICATION_ID = "zz7sp9sNJkMwMA";
    private final String USER_AGENT = "android:" + BuildConfig.APPLICATION_ID + ":v" + BuildConfig.VERSION_NAME + " (by /u/pchupik)";

    private String accessToken;
    private long expiresAt;
    private String uuid;

    private final OkHttpClient okHttpClient;

    private Prefs prefs;


    public RedditApi(Prefs prefs) {
        this.prefs = prefs;
        this.accessToken = prefs.getToken();
        this.expiresAt = prefs.getTokenExpirationDate();
        this.uuid = prefs.getUUID();
        okHttpClient = new OkHttpClient.Builder().build();
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

        Request request = new Request.Builder()
                .url("https://oauth.reddit.com/top?limit="+limit + ((after != null)? "&after="+after : ""))
                .header("User-Agent", USER_AGENT)
                .header("Authorization", "bearer "+ accessToken)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String string = response.body().string();
            return parsePostsList(string);

        } catch (IOException | JSONException e) {
            Log.e("RedditApi", "requestData", e);
        }
        return new ArrayList<>();
    }

    private static List<Post> parsePostsList(String string) throws JSONException {
        ArrayList<Post> posts = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(string);
        int error = jsonObject.optInt("error");
        if (error != 0) {
            Log.e("RedditApi", error + " " + jsonObject.optString("message"));
        }
        JSONObject data = jsonObject.optJSONObject("data");
        if (data != null) {
            JSONArray children = data.optJSONArray("children");
            if (children != null) {
                for (int i = 0; i < children.length(); i++) {
                    JSONObject jsonPost = children.getJSONObject(i);
                    JSONObject postData = jsonPost.getJSONObject("data");
                    String fullImage = parseFullImage(postData);

                    Post post = new Post(
                            postData.optString("name"),
                            postData.optString("title"),
                            postData.optString("author"),
                            postData.optLong("created_utc") * 1000,
                            postData.optString("thumbnail"),
                            fullImage,
                            postData.optLong("num_comments"),
                            "https://www.reddit.com" + postData.optString("permalink")
                    );

                    posts.add(post);
                }
            }
        }
        return posts;
    }

    @Nullable
    private static String parseFullImage(JSONObject postData) {
        String fullImage = null;
        JSONObject preview = postData.optJSONObject("preview");
        if (preview != null) {
            boolean enabled = preview.optBoolean("enabled", false);
            if (enabled) {
                fullImage = postData.optString("url");
            }
//                JSONArray images = preview.optJSONArray("images");
//                if (enabled && images.length() > 0) {
//                    JSONObject source = images.getJSONObject(0).optJSONObject("source");
//                    fullImage = source.optString("url");
//                }
        }
        return fullImage;
    }
}
