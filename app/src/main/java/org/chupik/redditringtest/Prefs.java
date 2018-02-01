package org.chupik.redditringtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;

public class Prefs {

    private final SharedPreferences preferences;

    public Prefs(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getToken(){
        return preferences.getString("access_token", null);
    }

    public long getTokenExpirationDate() {
        return preferences.getLong("token_expires", 0);
    }

    public void store(String token, long expires){
        preferences
                .edit()
                .putString("access_token", token)
                .putLong("token_expires", expires)
                .apply();
    }

    public String getUUID(){
        String uuid = preferences.getString("uuid", null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            preferences
                    .edit()
                    .putString("uuid", uuid)
                    .apply();
        }
        return uuid;
    }
}
