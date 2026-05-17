package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class JourneyMapActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_map);

        // Back button
        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Impact card taps → rebound only (no redirect per spec)
        addReboundCard(R.id.cardImpact1);
        addReboundCard(R.id.cardImpact2);
        addReboundCard(R.id.cardImpact3);

        // Entrance animations — nodes fade + slide in
        animateEntrance();

        // Scroll parallax effect on trunk
        ScrollView scroll = findViewById(R.id.journeyScroll);
        if (scroll != null) {
            scroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
                int scrollY = scroll.getScrollY();
                // Subtle parallax on the top banner
                android.view.View banner = scroll.getChildAt(0);
                if (banner != null)
                    banner.setTranslationY(scrollY * 0.15f);
            });
        }
    }

    private void animateEntrance() {
        int[] nodeIds = {R.id.cardImpact1, R.id.cardImpact2, R.id.cardImpact3};
        for (int i = 0; i < nodeIds.length; i++) {
            android.view.View v = findViewById(nodeIds[i]);
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationX(i % 2 == 0 ? -40f : 40f);
            v.animate()
                    .alpha(1f).translationX(0f)
                    .setStartDelay(200L + 150L * i)
                    .setDuration(400)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .start();
        }
    }

    private void addReboundCard(int cardId) {
        CardView card = findViewById(cardId);
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                    break;
            }
            return true;
        });
    }
}