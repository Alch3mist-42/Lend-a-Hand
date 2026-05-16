package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

    // Credit thresholds for each rank
    private static final int CREDITS_SEEDLING  = 0;
    private static final int CREDITS_HELPER    = 20;
    private static final int CREDITS_UPLIFTER  = 60;
    private static final int CREDITS_GUARDIAN  = 120;
    private static final int CREDITS_LEGEND    = 250;

    // Demo: in production this comes from the server
    private int userCredits = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Update credits display from intent if passed
        if (getIntent().hasExtra("credits")) {
            userCredits = getIntent().getIntExtra("credits", 42);
        }

        updateRankDisplay();

        // Impact log card animations
        addPressAnimation(findViewById(R.id.cardLog1));
        addPressAnimation(findViewById(R.id.cardLog2));
        addPressAnimation(findViewById(R.id.cardLog3));

        // Expand Your Horizon button
        findViewById(R.id.btnExpand).setOnClickListener(v ->
                startActivity(new Intent(this, AddItemsActivity.class)));

        // Bottom nav
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, AddItemsActivity.class)));
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () -> {});
    }

    private void updateRankDisplay() {
        // Determine current rank and next milestone
        String currentRank;
        String nextExpansion;

        if (userCredits >= CREDITS_LEGEND) {
            currentRank    = "Legend";
            nextExpansion  = "Max rank reached!";
        } else if (userCredits >= CREDITS_GUARDIAN) {
            currentRank    = "Guardian";
            nextExpansion  = (CREDITS_LEGEND - userCredits) + " to Legend";
        } else if (userCredits >= CREDITS_UPLIFTER) {
            currentRank    = "Uplifter";
            nextExpansion  = (CREDITS_GUARDIAN - userCredits) + " to Guardian";
        } else if (userCredits >= CREDITS_HELPER) {
            currentRank    = "Helper";
            nextExpansion  = (CREDITS_UPLIFTER - userCredits) + " to Uplifter";
        } else {
            currentRank    = "Seedling";
            nextExpansion  = (CREDITS_HELPER - userCredits) + " to Helper";
        }

        // Update UI
        android.widget.TextView tvCredits  = findViewById(R.id.tvCredits);
        android.widget.TextView tvNextRank = findViewById(R.id.tvNextRank);
        android.widget.TextView tvRankLabel = findViewById(R.id.tvRankLabel);

        if (tvCredits  != null) tvCredits.setText(userCredits + " Credits");
        if (tvNextRank != null) tvNextRank.setText(nextExpansion);
        if (tvRankLabel != null) tvRankLabel.setText(currentRank);
    }

    private void addPressAnimation(CardView card) {
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                            .setInterpolator(new OvershootInterpolator(2.5f)).start();
                    break;
            }
            return false;
        });
    }

    private void setupNavItem(int navId, Runnable action) {
        LinearLayout nav = findViewById(navId);
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