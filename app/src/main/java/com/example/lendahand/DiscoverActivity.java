package com.example.lendahand;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DiscoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // ── Notification bell ──
        // Find the bell TextView in the top bar and wire it
        // It's the 3rd child of topBar LinearLayout
        LinearLayout topBar = findViewById(R.id.topBar);
        if (topBar != null && topBar.getChildCount() >= 3) {
            View bellView = topBar.getChildAt(2);
            bellView.setOnClickListener(v -> showNotificationPanel());
        }

        // ── CTA buttons ──
        findViewById(R.id.btnBecomeGiver).setOnClickListener(v ->
                startActivity(new Intent(this, AddItemsActivity.class)));

        findViewById(R.id.btnFindNeed).setOnClickListener(v ->
                startActivity(new Intent(this, AllocationActivity.class)));

        // ── Tier card animations ──
        addPressAnimation(findViewById(R.id.cardTier1));
        addPressAnimation(findViewById(R.id.cardTier2));
        addPressAnimation(findViewById(R.id.cardTier3));

        // ── Tier card clicks (navigate to donation screen) ──
        if (findViewById(R.id.cardTier1) != null)
            findViewById(R.id.cardTier1).setOnClickListener(v ->
                    startActivity(new Intent(this, AddItemsActivity.class)));

        if (findViewById(R.id.cardTier2) != null)
            findViewById(R.id.cardTier2).setOnClickListener(v ->
                    startActivity(new Intent(this, AddItemsActivity.class)));

        if (findViewById(R.id.cardTier3) != null)
            findViewById(R.id.cardTier3).setOnClickListener(v ->
                    startActivity(new Intent(this, AddItemsActivity.class)));

        // ── View leaderboard link ──
        findViewById(R.id.btnViewLeaderboard).setOnClickListener(v ->
                startActivity(new Intent(this, LeaderboardActivity.class)));

        // ── Bottom nav ──
        setupNavItem(R.id.navDiscover, () -> {});
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, AddItemsActivity.class)));
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void showNotificationPanel() {
        // Build notification items as a styled dialog
        // In production these would come from the server
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.NotificationDialog);

        // Custom message view
        String notifications =
                "🏆  TIER UPGRADE\n" +
                        "You've reached Helper rank!\n" +
                        "18 more credits to Uplifter.\n\n" +
                        "✅  DONATION CONFIRMED\n" +
                        "Your donation to John Doe\n" +
                        "has been received.\n\n" +
                        "📢  COMMUNITY REQUEST\n" +
                        "Local Shelter needs 50\n" +
                        "blankets urgently.\n\n" +
                        "💳  CREDITS AWARDED\n" +
                        "+12 credits for your\n" +
                        "City Park donation.";

        builder.setTitle("Notifications")
                .setMessage(notifications)
                .setPositiveButton("Close", null)
                .show();
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