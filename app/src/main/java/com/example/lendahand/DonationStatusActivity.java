package com.example.lendahand;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class DonationStatusActivity extends BaseActivity {

    private static final String STATUS_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/donation_status.php";

    private SessionManager sessionManager;
    private LinearLayout statusContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_status);

        sessionManager = new SessionManager(this);
        statusContainer = findViewById(R.id.statusContainer);

        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));
        setupNavItem(R.id.navStatus, () -> {});
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));

        loadDonationStatus();
    }

    private void loadDonationStatus() {
        StringRequest request = new StringRequest(Request.Method.POST, STATUS_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("error")) {
                            Toast.makeText(this,
                                    json.optString("error"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray donations = json.getJSONArray("donations");

                        if (statusContainer != null)
                            statusContainer.removeAllViews();

                        if (donations.length() == 0) {
                            TextView empty = new TextView(this);
                            empty.setText("You haven't made any donations yet.");
                            empty.setTextSize(15f);
                            empty.setTextColor(Color.parseColor("#6e7976"));
                            empty.setPadding(0, 48, 0, 0);
                            if (statusContainer != null)
                                statusContainer.addView(empty);
                            return;
                        }

                        for (int i = 0; i < donations.length(); i++) {
                            addStatusCard(donations.getJSONObject(i));
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Could not load status",
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

    private void addStatusCard(JSONObject item) {
        try {
            String resourceName = item.optString("resource_name", "Unknown");
            String category     = item.optString("category", "");
            String qty          = item.optString("quantity_donated", "0");
            String status       = item.optString("status", "submitted");
            String allocated    = item.optString("quantity_allocated", "");

            // Map status to step number
            int currentStep;
            switch (status) {
                case "accepted":    currentStep = 2; break;
                case "to be collected":
                case "allocated":   currentStep = 3; break;
                case "delivered":   currentStep = 4; break;
                default:            currentStep = 1; break; // submitted
            }

            CardView card = new CardView(this);
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = 32;
            card.setLayoutParams(cardParams);
            card.setRadius(48f);
            card.setCardElevation(4f);
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setPadding(48, 48, 48, 48);

            // Title
            TextView tvTitle = new TextView(this);
            tvTitle.setText(resourceName + " — " + qty + " items");
            tvTitle.setTextSize(17f);
            tvTitle.setTextColor(Color.parseColor("#191c1e"));
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTitle.setPadding(0, 0, 0, 8);
            inner.addView(tvTitle);

            TextView tvCat = new TextView(this);
            tvCat.setText(category);
            tvCat.setTextSize(12f);
            tvCat.setTextColor(Color.parseColor("#004f45"));
            tvCat.setPadding(0, 0, 0, 32);
            inner.addView(tvCat);

            // Status steps
            String[] steps = {"Submitted", "Accepted by Staff",
                    "Allocated to Recipient", "Delivered"};

            for (int i = 0; i < steps.length; i++) {
                LinearLayout stepRow = new LinearLayout(this);
                stepRow.setOrientation(LinearLayout.HORIZONTAL);
                stepRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.bottomMargin = 20;
                stepRow.setLayoutParams(rowParams);

                // Step indicator circle
                TextView stepCircle = new TextView(this);
                LinearLayout.LayoutParams circleParams =
                        new LinearLayout.LayoutParams(64, 64);
                circleParams.rightMargin = 28;
                stepCircle.setLayoutParams(circleParams);
                stepCircle.setGravity(android.view.Gravity.CENTER);
                stepCircle.setText(i < currentStep ? "✓" : String.valueOf(i + 1));
                stepCircle.setTextSize(13f);
                stepCircle.setTypeface(null, android.graphics.Typeface.BOLD);

                if (i < currentStep) {
                    // Completed
                    stepCircle.setBackgroundResource(R.drawable.avatar_bg_green);
                    stepCircle.setTextColor(Color.WHITE);
                } else if (i == currentStep - 1 + 1) {
                    // Current
                    stepCircle.setBackgroundResource(R.drawable.circle_outline_green);
                    stepCircle.setTextColor(Color.parseColor("#004f45"));
                } else {
                    // Future
                    stepCircle.setBackgroundResource(R.drawable.circle_ring_light);
                    stepCircle.setTextColor(Color.parseColor("#aab0b8"));
                }

                stepRow.addView(stepCircle);

                // Step label
                TextView stepLabel = new TextView(this);
                stepLabel.setText(steps[i]);
                stepLabel.setTextSize(14f);
                stepLabel.setTextColor(i < currentStep
                        ? Color.parseColor("#004f45")
                        : Color.parseColor("#aab0b8"));
                if (i == currentStep - 1)
                    stepLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                stepRow.addView(stepLabel);

                inner.addView(stepRow);
            }

            card.addView(inner);
            if (statusContainer != null)
                statusContainer.addView(card);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNavItem(int navId, Runnable action) {
        LinearLayout nav = findViewById(navId);
        if (nav == null) return;
        nav.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.88f).scaleY(0.88f).setDuration(80).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(3.5f))
                            .withEndAction(action).start();
                    break;
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    break;
            }
            return true;
        });
    }
}