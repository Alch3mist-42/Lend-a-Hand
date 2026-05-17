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
    private ScrollView scrollView;
    private LinearLayout itemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        scrollView = findViewById(R.id.scrollContent);
        itemsContainer = findViewById(R.id.itemsContainer);

        // ── Steppers ──
        wireSteppers(R.id.btnMinus_nonPerish,   R.id.btnPlus_nonPerish,   R.id.tvQty_nonPerish);
        wireSteppers(R.id.btnMinus_canned,       R.id.btnPlus_canned,       R.id.tvQty_canned);
        wireSteppers(R.id.btnMinus_babyFormula,  R.id.btnPlus_babyFormula,  R.id.tvQty_babyFormula);
        wireSteppers(R.id.btnMinus_soap,         R.id.btnPlus_soap,         R.id.tvQty_soap);
        wireSteppers(R.id.btnMinus_sanitary,     R.id.btnPlus_sanitary,     R.id.tvQty_sanitary);
        wireSteppers(R.id.btnMinus_blankets,     R.id.btnPlus_blankets,     R.id.tvQty_blankets);
        wireSteppers(R.id.btnMinus_winter,       R.id.btnPlus_winter,       R.id.tvQty_winter);
        wireSteppers(R.id.btnMinus_stationery,   R.id.btnPlus_stationery,   R.id.tvQty_stationery);
        wireSteppers(R.id.btnMinus_backpacks,    R.id.btnPlus_backpacks,    R.id.tvQty_backpacks);
        wireSteppers(R.id.btnMinus_medication,   R.id.btnPlus_medication,   R.id.tvQty_medication);
        wireSteppers(R.id.btnMinus_insulin,      R.id.btnPlus_insulin,      R.id.tvQty_insulin);

        // ── Save button ──
        View btnSave = findViewById(R.id.btnSave);
        if (btnSave != null) btnSave.setOnClickListener(v ->
                startActivity(new Intent(this, AllocationActivity.class)));

        // ── Chip filtering with scroll to section ──
        activeChip = findViewById(R.id.chipAll);
        setChipActive(activeChip);

        // Food chip
        TextView chipFood = findViewById(R.id.chipFood);
        if (chipFood != null) {
            chipFood.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipFood;
                setChipActive(chipFood);
                scrollToSection(R.id.sectionFood);
            });
        }

        // Hygiene chip
        TextView chipHygiene = findViewById(R.id.chipHygiene);
        if (chipHygiene != null) {
            chipHygiene.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipHygiene;
                setChipActive(chipHygiene);
                scrollToSection(R.id.sectionHygiene);
            });
        }

        // Clothing chip
        TextView chipClothing = findViewById(R.id.chipClothing);
        if (chipClothing != null) {
            chipClothing.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipClothing;
                setChipActive(chipClothing);
                scrollToSection(R.id.sectionClothing);
            });
        }

        // School chip
        TextView chipSchool = findViewById(R.id.chipSchool);
        if (chipSchool != null) {
            chipSchool.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipSchool;
                setChipActive(chipSchool);
                scrollToSection(R.id.sectionSchool);
            });
        }

        // Medical chip
        TextView chipMedical = findViewById(R.id.chipMedical);
        if (chipMedical != null) {
            chipMedical.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipMedical;
                setChipActive(chipMedical);
                scrollToSection(R.id.sectionMedical);
            });
        }

        // All chip - scroll to top
        TextView chipAll = findViewById(R.id.chipAll);
        if (chipAll != null) {
            chipAll.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipAll;
                setChipActive(chipAll);
                scrollView.smoothScrollTo(0, 0);
            });
        }

        // ── Card animations ──
        int[] cards = {
                R.id.cardNonPerishables, R.id.cardCannedFood, R.id.cardBabyFormula,
                R.id.cardSoap, R.id.cardSanitary, R.id.cardBlankets,
                R.id.cardWinterClothes, R.id.cardStationery, R.id.cardBackpacks,
                R.id.cardMedication, R.id.cardInsulin
        };
        for (int cid : cards) addReboundCard(cid);

        // ── Bottom nav ──
        setupNav(R.id.navDiscover, DiscoverActivity.class);
        setupNav(R.id.navDonate,   null);
        setupNav(R.id.navActivity, LeaderboardActivity.class);
        setupNav(R.id.navProfile,  ProfileActivity.class);
    }

    private void scrollToSection(int sectionId) {
        View section = findViewById(sectionId);
        if (section != null && scrollView != null) {
            // Add a small delay to ensure layout is ready
            section.postDelayed(() -> {
                int yOffset = section.getTop() - 100; // 100dp padding from top
                scrollView.smoothScrollTo(0, yOffset);
            }, 50);
        }
    }

    private void wireSteppers(int minusId, int plusId, int qtyId) {
        Button minus = findViewById(minusId);
        Button plus  = findViewById(plusId);
        TextView qty = findViewById(qtyId);
        if (minus == null || plus == null || qty == null) return;
        minus.setOnClickListener(v -> {
            int val = Integer.parseInt(qty.getText().toString());
            if (val > 0) qty.setText(String.valueOf(val - 1));
        });
        plus.setOnClickListener(v -> {
            int val = Integer.parseInt(qty.getText().toString());
            qty.setText(String.valueOf(val + 1));
        });
    }

    private void setChipActive(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.chip_white_active);
        chip.setTextColor(getColor(R.color.primary));
    }

    private void setChipInactive(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.chip_ghost);
        chip.setTextColor(0xCCFFFFFF);
    }

    private void addReboundCard(int cardId) {
        CardView card = findViewById(cardId);
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f)
                            .setDuration(220)
                            .setInterpolator(new OvershootInterpolator(3f))
                            .start();
                    break;
            }
            return false;
        });
    }

    private void setupNav(int navId, Class<?> target) {
        LinearLayout nav = findViewById(navId);
        if (nav == null) return;
        nav.setOnClickListener(v -> {
            if (target != null) startActivity(new Intent(this, target));
        });
    }
}