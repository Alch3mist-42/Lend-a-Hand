package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class AllocationActivity extends BaseActivity {

    private static final String BASE_URL = "https://wmc.ms.wits.ac.za/students/sgroup2713/";
    private static final String ALLOCATE_URL  = BASE_URL + "allocate.php";

    private SessionManager sessionManager;
    private String requestId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocation);

        sessionManager = new SessionManager(this);
        requestId = getIntent().getStringExtra("request_id") != null
                ? getIntent().getStringExtra("request_id") : "";

        // Card press animations
        addPressAnimation(findViewById(R.id.cardRecipient1));
        addPressAnimation(findViewById(R.id.cardRecipient2));
        addPressAnimation(findViewById(R.id.cardRecipient3));

        // Allocate buttons
        setupAllocateButton(R.id.btnAllocate1, R.id.etAllocate1, "John Doe",     5);
        setupAllocateButton(R.id.btnAllocate2, R.id.etAllocate2, "Mary Jenkins", 20);
        setupAllocateButton(R.id.btnAllocate3, R.id.etAllocate3, "Local Shelter",50);

        // Bottom nav
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void setupAllocateButton(int btnId, int etId, String name, int maxNeeded) {
        MaterialButton btn = findViewById(btnId);
        EditText et = findViewById(etId);
        if (btn == null || et == null) return;

        btn.setOnClickListener(v -> {
            String input = et.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Enter an amount first", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try { amount = Integer.parseInt(input); }
            catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount > maxNeeded) {
                Toast.makeText(this, "Exceeds " + name + "'s need of " + maxNeeded,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            btn.setEnabled(false);

            final int finalAmount = amount;
            StringRequest request = new StringRequest(Request.Method.POST, ALLOCATE_URL,
                    response -> {
                        btn.setEnabled(true);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.has("success")) {
                                Toast.makeText(this,
                                        "✓ Allocated " + finalAmount + " to " + name,
                                        Toast.LENGTH_SHORT).show();
                                et.setText("");
                            } else {
                                Toast.makeText(this,
                                        json.optString("error", "Allocation failed"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        btn.setEnabled(true);
                        Toast.makeText(this, "Cannot connect to server", Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("donor_id",     sessionManager.getUserId());
                    params.put("requester_id", ""); // populated from real request data
                    params.put("resource_id",  ""); // populated from real request data
                    params.put("quantity",     String.valueOf(finalAmount));
                    params.put("collection_date", "");
                    params.put("delivery_date",   "");
                    return params;
                }
            };

            VolleySingleton.getInstance(this).getRequestQueue().add(request);
        });
    }

    private void addPressAnimation(CardView card) {
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start(); break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                            .setInterpolator(new OvershootInterpolator(2.5f)).start(); break;
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
                    v.animate().scaleX(0.88f).scaleY(0.88f).setDuration(80).start(); break;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(3.5f))
                            .withEndAction(action).start(); break;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start(); break;
            }
            return true;
        });
    }
}