package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends BaseActivity {

    private static final int CREDITS_HELPER   = 20;
    private static final int CREDITS_UPLIFTER = 60;
    private static final int CREDITS_GUARDIAN = 120;
    private static final int CREDITS_LEGEND   = 250;

    private int userCredits = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getIntent().hasExtra("credits"))
            userCredits = getIntent().getIntExtra("credits", 42);

        // Name and bio
        TextView tvName = findViewById(R.id.tvProfileName);
        if (tvName != null) tvName.setText("Oratile Fisasi");

        TextView tvBio = findViewById(R.id.tvProfileBio);
        if (tvBio != null) tvBio.setText(
                "Passionate about community upliftment and education. " +
                        "Believes small acts of giving create ripples of lasting change.");

        updateRankDisplay();

        // Expand + button → Community Requests
        com.google.android.material.button.MaterialButton btnExpand =
                findViewById(R.id.btnExpand);
        if (btnExpand != null) {
            btnExpand.setText("Expand +");
            btnExpand.setOnClickListener(v ->
                    startActivity(new Intent(this, CommunityRequestsActivity.class)));
        }

        // See Journey → Journey Map
        View btnSeeJourney = findViewById(R.id.btnSeeJourney);
        if (btnSeeJourney != null) {
            btnSeeJourney.setOnClickListener(v ->
                    startActivity(new Intent(this, JourneyMapActivity.class)));
            // Bounce arrow animation
            btnSeeJourney.postDelayed(() ->
                    btnSeeJourney.animate().translationX(8f).setDuration(400)
                            .setInterpolator(new OvershootInterpolator(2f))
                            .withEndAction(() ->
                                    btnSeeJourney.animate().translationX(0f).setDuration(300)
                                            .setInterpolator(new OvershootInterpolator(3f)).start()
                            ).start(), 600);
        }

        // Impact log cards → do NOT redirect (rebound only)
        addReboundAnimation(findViewById(R.id.cardLog1));
        addReboundAnimation(findViewById(R.id.cardLog2));
        addReboundAnimation(findViewById(R.id.cardLog3));

        // Bottom nav — Donate goes to Community Requests
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () -> {});
    }

    private void updateRankDisplay() {
        String currentRank, nextExpansion;
        if (userCredits >= CREDITS_LEGEND) {
            currentRank = "Legend"; nextExpansion = "Max rank reached!";
        } else if (userCredits >= CREDITS_GUARDIAN) {
            currentRank = "Guardian"; nextExpansion = (CREDITS_LEGEND - userCredits) + " to Legend";
        } else if (userCredits >= CREDITS_UPLIFTER) {
            currentRank = "Uplifter"; nextExpansion = (CREDITS_GUARDIAN - userCredits) + " to Guardian";
        } else if (userCredits >= CREDITS_HELPER) {
            currentRank = "Helper"; nextExpansion = (CREDITS_UPLIFTER - userCredits) + " to Uplifter";
        } else {
            currentRank = "Seedling"; nextExpansion = (CREDITS_HELPER - userCredits) + " to Helper";
        }
        TextView tvCredits   = findViewById(R.id.tvCredits);
        TextView tvNextRank  = findViewById(R.id.tvNextRank);
        TextView tvRankLabel = findViewById(R.id.tvRankLabel);
        if (tvCredits   != null) tvCredits.setText(userCredits + " Credits");
        if (tvNextRank  != null) tvNextRank.setText(nextExpansion);
        if (tvRankLabel != null) tvRankLabel.setText(currentRank);
    }

    private void addReboundAnimation(View view) {
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
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