package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AddItemsActivity extends AppCompatActivity {

    private TextView activeChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        // ── Stepper wiring ──
        wireSteppers(R.id.btnMinusFood1, R.id.btnPlusFood1, R.id.tvQtyFood1);
        wireSteppers(R.id.btnMinusFood2, R.id.btnPlusFood2, R.id.tvQtyFood2);
        wireSteppers(R.id.btnMinusFood3, R.id.btnPlusFood3, R.id.tvQtyFood3);
        wireSteppers(R.id.btnMinusHyg1,  R.id.btnPlusHyg1,  R.id.tvQtyHyg1);
        wireSteppers(R.id.btnMinusHyg2,  R.id.btnPlusHyg2,  R.id.tvQtyHyg2);
        wireSteppers(R.id.btnMinusHyg3,  R.id.btnPlusHyg3,  R.id.tvQtyHyg3);
        wireSteppers(R.id.btnMinusHyg4,  R.id.btnPlusHyg4,  R.id.tvQtyHyg4);
        wireSteppers(R.id.btnMinusCloth1, R.id.btnPlusCloth1, R.id.tvQtyCloth1);
        wireSteppers(R.id.btnMinusCloth2, R.id.btnPlusCloth2, R.id.tvQtyCloth2);
        wireSteppers(R.id.btnMinusCloth3, R.id.btnPlusCloth3, R.id.tvQtyCloth3);
        wireSteppers(R.id.btnMinusCloth4, R.id.btnPlusCloth4, R.id.tvQtyCloth4);
        wireSteppers(R.id.btnMinusHouse1, R.id.btnPlusHouse1, R.id.tvQtyHouse1);
        wireSteppers(R.id.btnMinusHouse2, R.id.btnPlusHouse2, R.id.tvQtyHouse2);
        wireSteppers(R.id.btnMinusHouse3, R.id.btnPlusHouse3, R.id.tvQtyHouse3);
        wireSteppers(R.id.btnMinusSchool1, R.id.btnPlusSchool1, R.id.tvQtySchool1);
        wireSteppers(R.id.btnMinusSchool2, R.id.btnPlusSchool2, R.id.tvQtySchool2);
        wireSteppers(R.id.btnMinusSchool3, R.id.btnPlusSchool3, R.id.tvQtySchool3);
        wireSteppers(R.id.btnMinusMed1,  R.id.btnPlusMed1,  R.id.tvQtyMed1);
        wireSteppers(R.id.btnMinusMed2,  R.id.btnPlusMed2,  R.id.tvQtyMed2);

        // ── Card press animations ──
        int[] cardIds = {
                R.id.cardFood1,   R.id.cardFood2,   R.id.cardFood3,
                R.id.cardHyg1,    R.id.cardHyg2,    R.id.cardHyg3,   R.id.cardHyg4,
                R.id.cardCloth1,  R.id.cardCloth2,  R.id.cardCloth3, R.id.cardCloth4,
                R.id.cardHouse1,  R.id.cardHouse2,  R.id.cardHouse3,
                R.id.cardSchool1, R.id.cardSchool2, R.id.cardSchool3,
                R.id.cardMed1,    R.id.cardMed2
        };
        for (int id : cardIds) {
            addCardPressAnimation(findViewById(id));
        }

        // ── Chip scroll-to-section ──
        ScrollView scrollView = findViewById(R.id.scrollContent);
        activeChip = findViewById(R.id.chipAll);

        setupChip(R.id.chipAll,       null,                  scrollView);
        setupChip(R.id.chipFood,      R.id.sectionFood,      scrollView);
        setupChip(R.id.chipHygiene,   R.id.sectionHygiene,   scrollView);
        setupChip(R.id.chipClothing,  R.id.sectionClothing,  scrollView);
        setupChip(R.id.chipHousehold, R.id.sectionHousehold, scrollView);
        setupChip(R.id.chipSchool,    R.id.sectionSchool,    scrollView);
        setupChip(R.id.chipMedical,   R.id.sectionMedical,   scrollView);

        // ── Bottom nav ──
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () -> {});
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // ── Save button: animate + navigate to Allocation ──
        addCardPressAnimation(findViewById(R.id.btnSubmitItems));
        findViewById(R.id.btnSubmitItems).setOnClickListener(v ->
                startActivity(new Intent(this, AllocationActivity.class)));
    }

    private void addCardPressAnimation(View view) {
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.96f).scaleY(0.96f)
                            .setDuration(100)
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(200)
                            .setInterpolator(new OvershootInterpolator(2.5f))
                            .start();
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
                    v.animate()
                            .scaleX(0.88f).scaleY(0.88f)
                            .setDuration(80)
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                    v.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(250)
                            .setInterpolator(new OvershootInterpolator(3.5f))
                            .withEndAction(action)
                            .start();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(150)
                            .start();
                    break;
            }
            return true;
        });
    }

    private void setupChip(int chipId, Integer sectionId, ScrollView scrollView) {
        TextView chip = findViewById(chipId);
        chip.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate()
                        .scaleX(0.90f).scaleY(0.90f)
                        .setDuration(80)
                        .start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(220)
                        .setInterpolator(new OvershootInterpolator(3.5f))
                        .start();

                activeChip.setBackgroundResource(R.drawable.chip_inactive);
                activeChip.setTextColor(0xccffffff);
                chip.setBackgroundResource(R.drawable.chip_active);
                chip.setTextColor(getColor(R.color.primary));
                activeChip = chip;

                if (sectionId == null) {
                    scrollView.smoothScrollTo(0, 0);
                } else {
                    TextView section = findViewById(sectionId);
                    scrollView.post(() ->
                            scrollView.smoothScrollTo(0, section.getTop() - 16));
                }
            }
            return true;
        });
    }

    private void wireSteppers(int minusId, int plusId, int qtyId) {
        Button minus = findViewById(minusId);
        Button plus  = findViewById(plusId);
        TextView qty = findViewById(qtyId);

        minus.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.75f).scaleY(0.75f).setDuration(70).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(220)
                        .setInterpolator(new OvershootInterpolator(4f))
                        .start();
                int current = Integer.parseInt(qty.getText().toString());
                if (current > 0) {
                    qty.setText(String.valueOf(current - 1));
                    bounceQty(qty, 0.75f);
                }
            }
            return true;
        });

        plus.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.75f).scaleY(0.75f).setDuration(70).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(220)
                        .setInterpolator(new OvershootInterpolator(4f))
                        .start();
                int current = Integer.parseInt(qty.getText().toString());
                qty.setText(String.valueOf(current + 1));
                bounceQty(qty, 1.35f);
            }
            return true;
        });
    }

    private void bounceQty(TextView qty, float targetScale) {
        qty.animate()
                .scaleX(targetScale).scaleY(targetScale)
                .setDuration(80)
                .withEndAction(() ->
                        qty.animate()
                                .scaleX(1f).scaleY(1f)
                                .setDuration(220)
                                .setInterpolator(new OvershootInterpolator(3.5f))
                                .start())
                .start();
    }
}