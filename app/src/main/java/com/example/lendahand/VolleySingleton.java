package com.example.lendahand;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class VolleySingleton {

    private static VolleySingleton instance;
    private final RequestQueue requestQueue;

    private VolleySingleton(Context context) {
        // Set up cookie manager to persist session cookies
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        requestQueue = Volley.newRequestQueue(
                context.getApplicationContext(),
                new HurlStack()
        );
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}