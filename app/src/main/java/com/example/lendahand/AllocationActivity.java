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

// This screen is only for staff - they use it to allocate donated items to recipients
public class AllocationActivity extends BaseActivity {

    // URL of the server endpoint that handles allocation
    private static final String ALLOCATE_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/allocate.php";

    // Used to check if the user is staff and to get their token
    private SessionManager sessionManager;

    // Data about the request we are allocating to
    // These values come from the CommunityRequestsActivity
    private String requestId     = "";
    private String requesterId   = "";
    private String resourceId    = "";
    private String requesterName = "";
    private int    quantityNeeded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocation);

        // Set up session manager to check role and get auth token
        sessionManager = new SessionManager(this);

        // Get the request details that were passed from the previous screen
        Intent incoming = getIntent();
        requestId      = incoming.getStringExtra("request_id")     != null ? incoming.getStringExtra("request_id")     : "";
        requesterId    = incoming.getStringExtra("requester_id")   != null ? incoming.getStringExtra("requester_id")   : "";
        resourceId     = incoming.getStringExtra("resource_id")    != null ? incoming.getStringExtra("resource_id")    : "";
        requesterName  = incoming.getStringExtra("requester_name") != null ? incoming.getStringExtra("requester_name") : "Recipient";
        quantityNeeded = incoming.getIntExtra("quantity_needed", 0);

        // Add press animation to the recipient cards
        addPressAnimation(findViewById(R.id.cardRecipient1));
        addPressAnimation(findViewById(R.id.cardRecipient2));
        addPressAnimation(findViewById(R.id.cardRecipient3));

        // Set up the allocate button for each recipient card
        setupAllocateButton(R.id.btnAllocate1, R.id.etAllocate1);
        setupAllocateButton(R.id.btnAllocate2, R.id.etAllocate2);
        setupAllocateButton(R.id.btnAllocate3, R.id.etAllocate3);

        // Bottom nav depends on who is logged in
        if (sessionManager.isStaff()) {
            // Staff go back to their donations page
            setupNavItem(R.id.navDiscover, () ->
                    startActivity(new Intent(this, StaffDonationsActivity.class)));
            setupNavItem(R.id.navDonate, () ->
                    startActivity(new Intent(this, StaffDonationsActivity.class)));
        } else {
            // Regular users go to discover or donate
            setupNavItem(R.id.navDiscover, () ->
                    startActivity(new Intent(this, DiscoverActivity.class)));
            setupNavItem(R.id.navDonate, () ->
                    startActivity(new Intent(this, CommunityRequestsActivity.class)));
        }

        // These nav items are the same for everyone
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    // Sets up an allocate button - only staff can see and use it
    private void setupAllocateButton(int btnId, int etId) {
        MaterialButton btn = findViewById(btnId);
        EditText et = findViewById(etId);
        if (btn == null || et == null) return;

        // Hide the button and input field if the user is not staff
        if (!sessionManager.isStaff()) {
            btn.setVisibility(View.GONE);
            et.setVisibility(View.GONE);
            return;
        }

        // Staff taps the button to confirm allocation
        btn.setOnClickListener(v -> {
            String input = et.getText().toString().trim();

            // Make sure they typed something
            if (input.isEmpty()) {
                Toast.makeText(this, "Enter an amount first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                // Try to convert the input to a number
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Amount must be positive
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Can't give more than what the recipient needs
            if (quantityNeeded > 0 && amount > quantityNeeded) {
                Toast.makeText(this,
                        "Exceeds " + requesterName + "'s need of " + quantityNeeded,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button while waiting for server
            btn.setEnabled(false);
            final int finalAmount = amount;

            // Send the allocation to the server
            StringRequest request = new StringRequest(Request.Method.POST, ALLOCATE_URL,
                    response -> {
                        btn.setEnabled(true); // re-enable button
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.has("success")) {
                                // Allocation worked - show confirmation message
                                Toast.makeText(this,
                                        "Donation confirmed ✓",
                                        Toast.LENGTH_LONG).show();
                                et.setText(""); // clear the input
                                // Go back to the staff donations list
                                startActivity(new Intent(this,
                                        StaffDonationsActivity.class));
                                finish();
                            } else {
                                // Server returned an error message
                                Toast.makeText(this,
                                        json.optString("error", "Allocation failed"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            // Could not read the server response
                            Toast.makeText(this, "Unexpected response",
                                    Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        // Network error
                        btn.setEnabled(true);
                        Toast.makeText(this, "Cannot connect to server",
                                Toast.LENGTH_LONG).show();
                    }
            ) {
                // Data we send to the server for the allocation
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id",         sessionManager.getUserId());
                    params.put("token",           sessionManager.getToken());
                    params.put("requester_id",    requesterId);   // who gets the items
                    params.put("resource_id",     resourceId);    // what item
                    params.put("quantity",        String.valueOf(finalAmount)); // how many
                    params.put("collection_date", "");
                    params.put("delivery_date",   "");
                    return params;
                }
            };

            VolleySingleton.getInstance(this).getRequestQueue().add(request);
        });
    }

    // Adds a small press animation when a card is touched
    private void addPressAnimation(CardView card) {
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Slightly shrink the card when pressed
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Spring back to normal size
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                            .setInterpolator(new OvershootInterpolator(2.5f)).start();
                    break;
            }
            return false;
        });
    }

    // Sets up a bottom nav button with a press animation and navigation action
    private void setupNavItem(int navId, Runnable action) {
        LinearLayout nav = findViewById(navId);
        if (nav == null) return;
        nav.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.88f).scaleY(0.88f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                    // Run the action after the animation finishes
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