package com.example.lendahand;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class StaffDonationsActivity extends BaseActivity {

    private static final String PENDING_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/donor_items_pending.php";
    private static final String ACCEPT_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/accept_donation.php";

    private SessionManager sessionManager;
    private LinearLayout donationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_donations);

        sessionManager = new SessionManager(this);
        donationsContainer = findViewById(R.id.donationsContainer);

        View btnGoToRequests = findViewById(R.id.btnGoToRequests);
        if (btnGoToRequests != null)
            btnGoToRequests.setOnClickListener(v ->
                    startActivity(new Intent(this, CommunityRequestsActivity.class)));

        // Staff bottom nav: DISCOVER / DONATIONS(active) / ALLOCATE / PROFILE
        setupNavItem(R.id.navDiscover,  () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonations, () -> {});
        setupNavItem(R.id.navRequests,  () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));
        setupNavItem(R.id.navProfile,   () ->
                startActivity(new Intent(this, ProfileActivity.class)));

        loadPendingDonations();
    }

    private void loadPendingDonations() {
        StringRequest request = new StringRequest(Request.Method.POST, PENDING_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("error")) {
                            Toast.makeText(this,
                                    json.optString("error"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONArray donations = json.getJSONArray("donations");
                        TextView tvCount = findViewById(R.id.tvPendingCount);
                        if (tvCount != null)
                            tvCount.setText(donations.length() + " pending donation(s)");
                        if (donationsContainer != null)
                            donationsContainer.removeAllViews();
                        if (donations.length() == 0) {
                            addEmptyState();
                            return;
                        }
                        for (int i = 0; i < donations.length(); i++) {
                            JSONObject d = donations.getJSONObject(i);
                            addDonationCard(
                                    d.optString("donor_id"),
                                    d.optString("donor_name"),
                                    d.optString("resource_name"),
                                    d.optString("category"),
                                    d.optString("quantity_donated"),
                                    d.optString("resource_id")
                            );
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not load donations",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Cannot connect to server",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getUserId());
                params.put("token",   sessionManager.getToken());
                return params;
            }
        };
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void addDonationCard(String donorId, String donorName,
                                 String resourceName, String category,
                                 String qty, String resourceId) {
        String[] parts = donorName.trim().split(" ");
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1 && parts[parts.length - 1].length() > 0)
            initials += parts[parts.length - 1].substring(0, 1).toUpperCase();

        CardView card = new CardView(this);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = 32;
        card.setLayoutParams(cardParams);
        card.setRadius(48f);
        card.setCardElevation(6f);
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(48, 48, 48, 48);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView avatar = new TextView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(96, 96);
        avatarParams.rightMargin = 32;
        avatar.setLayoutParams(avatarParams);
        avatar.setBackgroundResource(R.drawable.avatar_bg_green);
        avatar.setGravity(android.view.Gravity.CENTER);
        avatar.setText(initials);
        avatar.setTextColor(Color.WHITE);
        avatar.setTextSize(14f);
        avatar.setTypeface(null, android.graphics.Typeface.BOLD);
        topRow.addView(avatar);

        LinearLayout nameCol = new LinearLayout(this);
        nameCol.setOrientation(LinearLayout.VERTICAL);

        TextView tvName = new TextView(this);
        tvName.setText(donorName);
        tvName.setTextSize(16f);
        tvName.setTextColor(Color.parseColor("#191c1e"));
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        nameCol.addView(tvName);

        TextView tvCat = new TextView(this);
        tvCat.setText(category);
        tvCat.setTextSize(12f);
        tvCat.setTextColor(Color.parseColor("#004f45"));
        nameCol.addView(tvCat);

        topRow.addView(nameCol);
        inner.addView(topRow);

        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 28));
        inner.addView(spacer);

        TextView tvItem = new TextView(this);
        tvItem.setText("Item:  " + resourceName);
        tvItem.setTextSize(14f);
        tvItem.setTextColor(Color.parseColor("#3e4946"));
        inner.addView(tvItem);

        TextView tvQty = new TextView(this);
        tvQty.setText(qty + " items");
        tvQty.setTextSize(22f);
        tvQty.setTextColor(Color.parseColor("#191c1e"));
        tvQty.setTypeface(null, android.graphics.Typeface.BOLD);
        tvQty.setPadding(0, 8, 0, 28);
        inner.addView(tvQty);

        MaterialButton btnAccept = new MaterialButton(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 120);
        btnAccept.setLayoutParams(btnParams);
        btnAccept.setText("Accept Donation");
        btnAccept.setTextColor(Color.WHITE);
        btnAccept.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#004f45")));
        btnAccept.setCornerRadius(60);

        final String finalDonorId    = donorId;
        final String finalResourceId = resourceId;
        final String finalDonorName  = donorName;

        btnAccept.setOnClickListener(v ->
                acceptDonation(finalDonorId, finalResourceId,
                        finalDonorName, card, btnAccept));

        inner.addView(btnAccept);
        card.addView(inner);

        if (donationsContainer != null)
            donationsContainer.addView(card);
    }

    private void acceptDonation(String donorId, String resourceId,
                                String donorName, CardView card,
                                MaterialButton btnAccept) {
        btnAccept.setEnabled(false);
        btnAccept.setText("Accepting...");

        StringRequest request = new StringRequest(Request.Method.POST, ACCEPT_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("success")) {
                            Toast.makeText(this,
                                    "✓ Donation from " + donorName + " accepted",
                                    Toast.LENGTH_LONG).show();
                            card.animate().alpha(0f).setDuration(400)
                                    .withEndAction(() -> {
                                        if (donationsContainer != null)
                                            donationsContainer.removeView(card);
                                        // Go to allocate page after accepting
                                        startActivity(new Intent(this,
                                                CommunityRequestsActivity.class));
                                    }).start();
                        } else {
                            btnAccept.setEnabled(true);
                            btnAccept.setText("Accept Donation");
                            Toast.makeText(this,
                                    json.optString("error", "Failed to accept"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        btnAccept.setEnabled(true);
                        btnAccept.setText("Accept Donation");
                    }
                },
                error -> {
                    btnAccept.setEnabled(true);
                    btnAccept.setText("Accept Donation");
                    Toast.makeText(this, "Cannot connect to server",
                            Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",     sessionManager.getUserId());
                params.put("token",       sessionManager.getToken());
                params.put("donor_id",    donorId);
                params.put("resource_id", resourceId);
                return params;
            }
        };
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void addEmptyState() {
        TextView empty = new TextView(this);
        empty.setText("No pending donations at this time.");
        empty.setTextSize(15f);
        empty.setTextColor(Color.parseColor("#6e7976"));
        empty.setPadding(0, 48, 0, 0);
        if (donationsContainer != null)
            donationsContainer.addView(empty);
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