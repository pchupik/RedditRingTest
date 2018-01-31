package org.chupik.redditringtest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final String APPLICATION_ID = "zz7sp9sNJkMwMA";
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipe = findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String uuid = preferences.getString("uuid", null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            preferences
                    .edit()
                    .putString("uuid", uuid)
                    .apply();
        }

        String accessToken = preferences.getString("access_token", null);
        long expiresAt = preferences.getLong("token_expires", 0);
        boolean isValid = Calendar.getInstance().getTimeInMillis() < expiresAt;

        if (accessToken == null || !isValid) {
            requestToken(okHttpClient, uuid);
        } else {
            requestData(okHttpClient, accessToken);
        }
    }

    private void requestToken(final OkHttpClient okHttpClient, String uuid) {
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

        okHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String string = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            final String accessToken = jsonObject.optString("access_token");

                            int expiresIn = jsonObject.optInt("expires_in", 0);
                            Calendar instance = Calendar.getInstance();
                            instance.add(Calendar.SECOND, expiresIn);
                            long expiresAt = instance.getTimeInMillis();

                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .edit()
                                    .putString("access_token", accessToken)
                                    .putLong("token_expires", expiresAt)
                                    .apply();


                            runOnUiThread(() -> Toast.makeText(MainActivity.this, accessToken, Toast.LENGTH_SHORT).show());
                            requestData(okHttpClient, accessToken);

                        } catch (JSONException e) {

                        }
                    }
                });
    }

    private void requestData(OkHttpClient okHttpClient, String accessToken){
        Request request = new Request.Builder()
                .url("https://oauth.reddit.com/top?limit=50")
                .header("User-Agent", "android:org.chupik.redditringtest:v1.0 (by /u/pchupik)")
                .header("Authorization", "bearer "+ accessToken)
                .build();
        okHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            int error = jsonObject.optInt("error");
                            if (error != 0) {
                                Log.e("Response", error + " " + jsonObject.optString("message"));
                            }
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (data != null) {
                                JSONArray children = data.optJSONArray("children");
                                ArrayList<Post> posts = new ArrayList<>();
                                for (int i = 0; i < children.length(); i++) {
                                    JSONObject jsonPost = children.getJSONObject(i);
                                    JSONObject postData = jsonPost.getJSONObject("data");

                                    String fullImage = null;
                                    JSONObject preview = postData.optJSONObject("preview");
                                    if (preview != null) {
                                        boolean enabled = preview.optBoolean("enabled", false);

                                        if (enabled) {
                                            fullImage = postData.optString("url");
                                        }

//                                        JSONArray images = preview.optJSONArray("images");
//                                        if (enabled && images.length() > 0) {
//                                            JSONObject source = images.getJSONObject(0).optJSONObject("source");
//                                            fullImage = source.optString("url");
//                                        }
                                    }

                                    Post post = new Post(
                                            postData.optString("name"),
                                            postData.optString("title"),
                                            postData.optString("author"),
                                            postData.optLong("created_utc") * 1000,
                                            postData.optString("thumbnail"),
                                            fullImage,
                                            postData.optLong("num_comments")
                                    );

                                    posts.add(post);
                                }


                                final PostsAdapter postsAdapter = new PostsAdapter(posts);
                                runOnUiThread(() -> {
                                    RecyclerView list = findViewById(R.id.list);
                                    list.setAdapter(postsAdapter);
                                    swipe.setRefreshing(false);
                                });

                            }

                        } catch (JSONException e) {

                        }
                    }
                });
    }
}
