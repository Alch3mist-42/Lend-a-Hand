package com.example.lendahand;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Singleton Volley queue with cookie support.
 * Cookies keep the PHP session alive across all requests.
 * Use VolleySingleton.getInstance(context).getRequestQueue()
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private final RequestQueue requestQueue;

    private VolleySingleton(Context context) {
        // Enable cookie management so PHP sessions persist
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null)
            instance = new VolleySingleton(context);
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}