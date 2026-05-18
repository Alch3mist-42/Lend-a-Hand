package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileActivity extends BaseActivity {

    private static final String BASE_URL = "https://wmc.ms.wits.ac.za/students/sgroup2713/";
    private static final String PROFILE_URL = BASE_URL + "profile.php";

    private static final int CREDITS_HELPER   = 20;
    private static final int CREDITS_UPLIFTER = 60;
    private static final int CREDITS_GUARDIAN = 120;
    private static final int CREDITS_LEGEND   = 250;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        // See Journey
        TextView btnSeeJourney = findViewById(R.id.btnSeeJourney);
        if (btnSeeJourney != null)
            btnSeeJourney.setOnClickListener(v ->
                    startActivity(new Intent(this, JourneyMapActivity.class)));

        // Impact log cards — rebound only
        addReboundAnimation(findViewById(R.id.cardLog1));
        addReboundAnimation(findViewById(R.id.cardLog2));
        addReboundAnimation(findViewById(R.id.cardLog3));

        // Expand+ → Community Requests
        com.google.android.material.button.MaterialButton btnExpand =
                findViewById(R.id.btnExpand);
        if (btnExpand != null)
            btnExpand.setOnClickListener(v ->
                    startActivity(new Intent(this, CommunityRequestsActivity.class)));

        // Bottom nav
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () -> {});

        // Load real profile data
        loadProfile();
    }

    private void loadProfile() {
        StringRequest request = new StringRequest(Request.Method.GET, PROFILE_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject user = json.getJSONObject("user");

                        String name   = user.optString("name", "")
                                + " " + user.optString("surname", "");
                        int    points = user.optInt("points", 0);
                        int    rank   = user.optInt("rank_position", 0);

                        // Update name
                        TextView tvName = findViewById(R.id.tvProfileName);
                        if (tvName != null) tvName.setText(name.trim());

                        // Update credits
                        TextView tvCredits = findViewById(R.id.tvCredits);
                        if (tvCredits != null) tvCredits.setText(String.valueOf(points));

                        // Update rank label
                        updateRankDisplay(points);

                        // Update impact log from donations
                        JSONArray donations = json.getJSONArray("donations");
                        updateImpactLog(donations);

                    } catch (Exception e) {
                        // Keep demo data on error
                    }
                },
                error -> { /* Keep demo data */ }
        );
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void updateRankDisplay(int points) {
        String currentRank, nextExpansion;
        if (points >= CREDITS_LEGEND) {
            currentRank = "Legend"; nextExpansion = "Max rank reached!";
        } else if (points >= CREDITS_GUARDIAN) {
            currentRank = "Guardian";
            nextExpansion = (CREDITS_LEGEND - points) + " to Legend";
        } else if (points >= CREDITS_UPLIFTER) {
            currentRank = "Uplifter";
            nextExpansion = (CREDITS_GUARDIAN - points) + " to Guardian";
        } else if (points >= CREDITS_HELPER) {
            currentRank = "Helper";
            nextExpansion = (CREDITS_UPLIFTER - points) + " to Uplifter";
        } else {
            currentRank = "Seedling";
            nextExpansion = (CREDITS_HELPER - points) + " to Helper";
        }

        TextView tvRank = findViewById(R.id.tvRankLabel);
        if (tvRank != null) tvRank.setText(currentRank);

        TextView tvNext = findViewById(R.id.tvNextRank);
        if (tvNext != null) tvNext.setText(nextExpansion);
    }

    private void updateImpactLog(JSONArray donations) {
        int[] logIds = {R.id.cardLog1, R.id.cardLog2, R.id.cardLog3};
        for (int i = 0; i < Math.min(donations.length(), logIds.length); i++) {
            try {
                JSONObject d = donations.getJSONObject(i);
                String resourceName = d.optString("resource_name", "Donation");
                String qty          = d.optString("quantity_donated", "0");
                String status       = d.optString("status", "");

                CardView card = findViewById(logIds[i]);
                if (card == null) continue;

                // Find title TextView inside card
                TextView tvTitle = card.findViewWithTag("log_title_" + i);
                if (tvTitle != null)
                    tvTitle.setText(resourceName + " — " + qty + " items");

            } catch (Exception e) {
                // Keep demo data for this card
            }
        }
    }

    private void addReboundAnimation(CardView card) {
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(220)
                            .setInterpolator(new OvershootInterpolator(2.5f)).start();
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