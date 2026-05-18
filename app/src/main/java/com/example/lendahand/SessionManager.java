package com.example.lendahand;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME    = "AsteraSession";
    private static final String KEY_USER_ID  = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE     = "role";
    private static final String KEY_LOGGED_IN = "logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String userId, String username, String role) {
        editor.putString(KEY_USER_ID,  userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE,     role);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public void clearSession() {
        editor.clear().apply();
    }

    public boolean isLoggedIn()  { return prefs.getBoolean(KEY_LOGGED_IN, false); }
    public String  getUserId()   { return prefs.getString(KEY_USER_ID, ""); }
    public String  getUsername() { return prefs.getString(KEY_USERNAME, ""); }
    public String  getRole()     { return prefs.getString(KEY_ROLE, "user"); }
    public boolean isStaff()     { return "stuff".equals(getRole()); }
}