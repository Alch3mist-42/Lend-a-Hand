package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;

public class CommunityRequestsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_requests);

        // Post a request → Recipient form
        View btnPost = findViewById(R.id.btnPostRequest);
        if (btnPost != null)
            btnPost.setOnClickListener(v ->
                    startActivity(new Intent(this, RecipientRequestActivity.class)));

        // Donate buttons → Add Items (user picks items AFTER choosing recipient)
        setDonateClick(R.id.btnDonateRequest1);
        setDonateClick(R.id.btnDonateRequest2);
        setDonateClick(R.id.btnDonateRequest3);

        // Card animations — rebound + hover
        addReboundCard(R.id.cardRequest1);
        addReboundCard(R.id.cardRequest2);
        addReboundCard(R.id.cardRequest3);

        // Bottom nav — Donate is current screen (active)
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () -> {});
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void setDonateClick(int btnId) {
        View btn = findViewById(btnId);
        if (btn != null)
            btn.setOnClickListener(v ->
                    startActivity(new Intent(this, AddItemsActivity.class)));
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