package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CommunityRequestsActivity extends BaseActivity {

    private static final String REQUESTS_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/requests.php";

    private SessionManager sessionManager;

    // Card 1 data
    private String requestId1 = "", requesterId1 = "", resourceId1 = "", requesterName1 = "";
    private int qty1 = 0;

    // Card 2 data
    private String requestId2 = "", requesterId2 = "", resourceId2 = "", requesterName2 = "";
    private int qty2 = 0;

    // Card 3 data
    private String requestId3 = "", requesterId3 = "", resourceId3 = "", requesterName3 = "";
    private int qty3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_requests);

        sessionManager = new SessionManager(this);

        // Post a request → Recipient form
        View btnPost = findViewById(R.id.btnPostRequest);
        if (btnPost != null)
            btnPost.setOnClickListener(v ->
                    startActivity(new Intent(this, RecipientRequestActivity.class)));

        // Donate buttons → Add Items with full data
        setDonateClick(R.id.btnDonateRequest1, 1);
        setDonateClick(R.id.btnDonateRequest2, 2);
        setDonateClick(R.id.btnDonateRequest3, 3);

        // Card animations
        addReboundCard(R.id.cardRequest1);
        addReboundCard(R.id.cardRequest2);
        addReboundCard(R.id.cardRequest3);

        // Bottom nav
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () -> {});
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));

        loadRequests();
    }

    private void loadRequests() {
        StringRequest request = new StringRequest(Request.Method.POST, REQUESTS_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.has("error")) {
                            Toast.makeText(this,
                                    json.optString("error", "Could not load requests"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray requests = json.getJSONArray("requests");

                        if (requests.length() > 0)
                            populateCard(requests.getJSONObject(0), 1);
                        if (requests.length() > 1)
                            populateCard(requests.getJSONObject(1), 2);
                        if (requests.length() > 2)
                            populateCard(requests.getJSONObject(2), 3);

                    } catch (Exception e) {
                        Toast.makeText(this, "Could not load requests",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Cannot connect to server",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getUserId());
                params.put("token",   sessionManager.getToken());
                return params;
            }
        };

        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void populateCard(JSONObject item, int cardNum) {
        try {
            String resName  = item.optString("resource_name",  "Unknown item");
            String reqName  = item.optString("requester_name", "Someone");
            String reqId    = item.optString("request_id",     "");
            String reqerId  = item.optString("requester_id",   "");
            String resId    = item.optString("resource_id",    "");
            int    qty      = item.optInt("quantity_needed",   0);

            // Store all data per card
            switch (cardNum) {
                case 1:
                    requestId1    = reqId;
                    requesterId1  = reqerId;
                    resourceId1   = resId;
                    requesterName1 = reqName;
                    qty1          = qty;
                    break;
                case 2:
                    requestId2    = reqId;
                    requesterId2  = reqerId;
                    resourceId2   = resId;
                    requesterName2 = reqName;
                    qty2          = qty;
                    break;
                case 3:
                    requestId3    = reqId;
                    requesterId3  = reqerId;
                    resourceId3   = resId;
                    requesterName3 = reqName;
                    qty3          = qty;
                    break;
            }

            // Update TextViews
            int nameViewId = cardNum == 1 ? R.id.tvRequestName1
                    : cardNum == 2 ? R.id.tvRequestName2
                    : R.id.tvRequestName3;
            int qtyViewId  = cardNum == 1 ? R.id.tvRequestQty1
                    : cardNum == 2 ? R.id.tvRequestQty2
                    : R.id.tvRequestQty3;

            TextView tvName = findViewById(nameViewId);
            TextView tvQty  = findViewById(qtyViewId);

            if (tvName != null) tvName.setText(reqName + " — " + resName);
            if (tvQty  != null) tvQty.setText(qty + " items needed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDonateClick(int btnId, int cardNum) {
        View btn = findViewById(btnId);
        if (btn == null) return;
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddItemsActivity.class);
            switch (cardNum) {
                case 1:
                    intent.putExtra("request_id",      requestId1);
                    intent.putExtra("requester_id",    requesterId1);
                    intent.putExtra("resource_id",     resourceId1);
                    intent.putExtra("requester_name",  requesterName1);
                    intent.putExtra("quantity_needed", qty1);
                    break;
                case 2:
                    intent.putExtra("request_id",      requestId2);
                    intent.putExtra("requester_id",    requesterId2);
                    intent.putExtra("resource_id",     resourceId2);
                    intent.putExtra("requester_name",  requesterName2);
                    intent.putExtra("quantity_needed", qty2);
                    break;
                case 3:
                    intent.putExtra("request_id",      requestId3);
                    intent.putExtra("requester_id",    requesterId3);
                    intent.putExtra("resource_id",     resourceId3);
                    intent.putExtra("requester_name",  requesterName3);
                    intent.putExtra("quantity_needed", qty3);
                    break;
            }
            startActivity(intent);
        });
    }

    private void addReboundCard(int cardId) {
        CardView card = findViewById(cardId);
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                    break;
            }
            return false;
        });
    }

    private void setupNavItem(int navId, Runnable action) {
        LinearLayout nav = findViewById(navId);
        if (nav == null) return;
        nav.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.88f).scaleY(0.88f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(3.5f))
                            .withEndAction(action).start();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    break;
            }
            return true;
        });
    }
}