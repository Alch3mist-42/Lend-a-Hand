package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;

public class AllocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocation);

        // ── Card press animations ──
        addPressAnimation(findViewById(R.id.cardRecipient1));
        addPressAnimation(findViewById(R.id.cardRecipient2));
        addPressAnimation(findViewById(R.id.cardRecipient3));

        // ── Allocate buttons with validation ──
        setupAllocateButton(R.id.btnAllocate1, R.id.etAllocate1, "John Doe", 5);
        setupAllocateButton(R.id.btnAllocate2, R.id.etAllocate2, "Mary Jenkins", 20);
        setupAllocateButton(R.id.btnAllocate3, R.id.etAllocate3, "Local Shelter", 50);

        // ── Bottom nav ──
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, AddItemsActivity.class)));
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void setupAllocateButton(int btnId, int etId, String name, int maxNeeded) {
        MaterialButton btn = findViewById(btnId);
        EditText et = findViewById(etId);

        btn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(2.5f))
                        .start();

                String input = et.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(this, "Enter an amount first", Toast.LENGTH_SHORT).show();
                    return true;
                }
                int amount = Integer.parseInt(input);
                if (amount <= 0) {
                    Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                } else if (amount > maxNeeded) {
                    Toast.makeText(this,
                            "Exceeds " + name + "'s need of " + maxNeeded,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Member 2 will replace this Toast with a Volley POST to donate_item.php
                    Toast.makeText(this,
                            "✓ Allocated " + amount + " to " + name,
                            Toast.LENGTH_SHORT).show();
                    et.setText("");
                }
            }
            return true;
        });
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
                    v.animate().scaleX(0.88f).scaleY(0.88f).setDuration(80).start();
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
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    break;
            }
            return true;
        });
    }
}