package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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

    private static final String ALLOCATE_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/allocate.php";

    private SessionManager sessionManager;

    private String requestId     = "";
    private String requesterId   = "";
    private String resourceId    = "";
    private String requesterName = "";
    private int    quantityNeeded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocation);

        sessionManager = new SessionManager(this);

        Intent incoming = getIntent();
        requestId      = incoming.getStringExtra("request_id")     != null ? incoming.getStringExtra("request_id")     : "";
        requesterId    = incoming.getStringExtra("requester_id")   != null ? incoming.getStringExtra("requester_id")   : "";
        resourceId     = incoming.getStringExtra("resource_id")    != null ? incoming.getStringExtra("resource_id")    : "";
        requesterName  = incoming.getStringExtra("requester_name") != null ? incoming.getStringExtra("requester_name") : "Recipient";
        quantityNeeded = incoming.getIntExtra("quantity_needed", 0);

        // Card press animations
        addPressAnimation(findViewById(R.id.cardRecipient1));
        addPressAnimation(findViewById(R.id.cardRecipient2));
        addPressAnimation(findViewById(R.id.cardRecipient3));

        // Only show allocate buttons to staff
        setupAllocateButton(R.id.btnAllocate1, R.id.etAllocate1);
        setupAllocateButton(R.id.btnAllocate2, R.id.etAllocate2);
        setupAllocateButton(R.id.btnAllocate3, R.id.etAllocate3);

        // Bottom nav — staff goes back to staff donations, user goes to discover
        if (sessionManager.isStaff()) {
            setupNavItem(R.id.navDiscover, () ->
                    startActivity(new Intent(this, StaffDonationsActivity.class)));
            setupNavItem(R.id.navDonate, () ->
                    startActivity(new Intent(this, StaffDonationsActivity.class)));
        } else {
            setupNavItem(R.id.navDiscover, () ->
                    startActivity(new Intent(this, DiscoverActivity.class)));
            setupNavItem(R.id.navDonate, () ->
                    startActivity(new Intent(this, CommunityRequestsActivity.class)));
        }

        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void setupAllocateButton(int btnId, int etId) {
        MaterialButton btn = findViewById(btnId);
        EditText et = findViewById(etId);
        if (btn == null || et == null) return;

        // Hide allocate button if user is not staff
        if (!sessionManager.isStaff()) {
            btn.setVisibility(View.GONE);
            et.setVisibility(View.GONE);
            return;
        }

        btn.setOnClickListener(v -> {
            String input = et.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Enter an amount first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantityNeeded > 0 && amount > quantityNeeded) {
                Toast.makeText(this,
                        "Exceeds " + requesterName + "'s need of " + quantityNeeded,
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
                                // Updated toast — "Donation confirmed"
                                Toast.makeText(this,
                                        "Donation confirmed ✓",
                                        Toast.LENGTH_LONG).show();
                                et.setText("");
                                // Go back to staff donations after confirming
                                startActivity(new Intent(this,
                                        StaffDonationsActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this,
                                        json.optString("error", "Allocation failed"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Unexpected response",
                                    Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        btn.setEnabled(true);
                        Toast.makeText(this, "Cannot connect to server",
                                Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id",         sessionManager.getUserId());
                    params.put("token",           sessionManager.getToken());
                    params.put("requester_id",    requesterId);
                    params.put("resource_id",     resourceId);
                    params.put("quantity",         String.valueOf(finalAmount));
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