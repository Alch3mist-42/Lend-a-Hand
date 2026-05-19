package com.example.lendahand;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PendingDonationsActivity extends BaseActivity {

    private static final String PENDING_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/pending_donations.php";

    private SessionManager sessionManager;

    private TextView tvDonationCount;
    private TextView tvEmptyState;
    private LinearLayout llDonationList;

    // ─── Colour helpers ─────────────────────────────────────────────────────

    private int primaryColor() { return getResources().getColor(R.color.primary, getTheme()); }
    private int surfaceColor() { return getResources().getColor(R.color.surface, getTheme()); }
    private int errorColor()   { return getResources().getColor(R.color.error,   getTheme()); }
    private int onSurface()    { return getResources().getColor(R.color.on_surface, getTheme()); }
    private int onSurfaceVar() { return getResources().getColor(R.color.on_surface_variant, getTheme()); }
    private int mutedGrey()    { return Color.parseColor("#aab0b8"); }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    // ─── onCreate ───────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Root constraint layout
        androidx.constraintlayout.widget.ConstraintLayout root =
                new androidx.constraintlayout.widget.ConstraintLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.setBackgroundColor(surfaceColor());

        // ── Top bar ──────────────────────────────────────────────────────
        LinearLayout topBar = new LinearLayout(this);
        topBar.setId(View.generateViewId());
        topBar.setOrientation(LinearLayout.VERTICAL);
        topBar.setBackgroundColor(primaryColor());
        topBar.setPadding(dp(24), dp(52), dp(24), dp(20));

        TextView tvLabel = new TextView(this);
        tvLabel.setText("ASTERA STAFF");
        tvLabel.setTextColor(Color.parseColor("#aaffffff"));
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams labelP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelP.bottomMargin = dp(8);
        topBar.addView(tvLabel, labelP);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Pending Donations");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tvTitle.setTypeface(null, Typeface.BOLD);
        topBar.addView(tvTitle);

        TextView tvSub = new TextView(this);
        tvSub.setText("Review and accept incoming donations");
        tvSub.setTextColor(Color.parseColor("#80ffffff"));
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        LinearLayout.LayoutParams subP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subP.topMargin = dp(4);
        topBar.addView(tvSub, subP);

        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams topBarLP =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topBarLP.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        root.addView(topBar, topBarLP);

        // ── Count pill ───────────────────────────────────────────────────
        tvDonationCount = new TextView(this);
        tvDonationCount.setId(View.generateViewId());
        tvDonationCount.setText("Loading...");
        tvDonationCount.setTextColor(primaryColor());
        tvDonationCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvDonationCount.setTypeface(null, Typeface.BOLD);
        tvDonationCount.setPadding(dp(14), dp(6), dp(14), dp(6));
        try {
            tvDonationCount.setBackground(
                    getResources().getDrawable(R.drawable.verified_badge_bg, getTheme()));
        } catch (Exception ignored) {
            tvDonationCount.setBackgroundColor(Color.parseColor("#e8f5e9"));
        }

        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams countLP =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        countLP.topToBottom = topBar.getId();
        countLP.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        countLP.setMarginStart(dp(20));
        countLP.topMargin = dp(16);
        root.addView(tvDonationCount, countLP);

        // ── Allocate button ──────────────────────────────────────────────
        Button btnAllocate = new Button(this);
        btnAllocate.setId(View.generateViewId());
        btnAllocate.setText("Allocate ›");
        btnAllocate.setTextColor(Color.WHITE);
        btnAllocate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnAllocate.setTypeface(null, Typeface.BOLD);
        btnAllocate.setBackgroundColor(primaryColor());
        btnAllocate.setOnClickListener(v ->
                Toast.makeText(this, "Allocate feature coming soon", Toast.LENGTH_SHORT).show());

        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams allocLP =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, dp(40));
        allocLP.topToBottom = topBar.getId();
        allocLP.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        allocLP.setMarginEnd(dp(20));
        allocLP.topMargin = dp(10);
        root.addView(btnAllocate, allocLP);

        // ── Bottom nav ───────────────────────────────────────────────────
        LinearLayout bottomNav = buildBottomNav();
        bottomNav.setId(View.generateViewId());
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams navLP =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(80));
        navLP.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        root.addView(bottomNav, navLP);

        // ── Scroll area ──────────────────────────────────────────────────
        ScrollView scroll = new ScrollView(this);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setPadding(dp(16), dp(12), dp(16), dp(8));

        LinearLayout scrollInner = new LinearLayout(this);
        scrollInner.setOrientation(LinearLayout.VERTICAL);
        scrollInner.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tvEmptyState = new TextView(this);
        tvEmptyState.setText("No pending donations at this time.");
        tvEmptyState.setTextColor(onSurfaceVar());
        tvEmptyState.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvEmptyState.setGravity(Gravity.CENTER);
        tvEmptyState.setVisibility(View.GONE);
        LinearLayout.LayoutParams emptyLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emptyLP.topMargin = dp(32);
        scrollInner.addView(tvEmptyState, emptyLP);

        llDonationList = new LinearLayout(this);
        llDonationList.setOrientation(LinearLayout.VERTICAL);
        llDonationList.setVisibility(View.GONE);
        scrollInner.addView(llDonationList, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scroll.addView(scrollInner);

        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams scrollLP =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0);
        scrollLP.topToBottom = tvDonationCount.getId();
        scrollLP.bottomToTop = bottomNav.getId();
        root.addView(scroll, scrollLP);

        setContentView(root);
    }

    // ─── onResume = real-time reload ─────────────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();
        loadPendingDonations();
    }

    // ─── Network ─────────────────────────────────────────────────────────────

    private void loadPendingDonations() {
        StringRequest request = new StringRequest(Request.Method.POST, PENDING_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("error")) {
                            Toast.makeText(this,
                                    json.optString("error", "Could not load donations"),
                                    Toast.LENGTH_SHORT).show();
                            showEmpty();
                            return;
                        }
                        renderDonations(json.getJSONArray("donations"));
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not load donations", Toast.LENGTH_SHORT).show();
                        showEmpty();
                    }
                },
                error -> {
                    Toast.makeText(this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
                    showEmpty();
                }
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

    private void respondToDonation(String donationId, String action, View card) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2713/respond_donation.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if ("success".equals(json.optString("status"))) {
                            card.animate()
                                    .alpha(0f)
                                    .translationX(action.equals("accept") ? dp(80) : -dp(80))
                                    .setDuration(250)
                                    .withEndAction(() -> {
                                        llDonationList.removeView(card);
                                        int remaining = llDonationList.getChildCount();
                                        tvDonationCount.setText(remaining + " pending donation"
                                                + (remaining == 1 ? "" : "s"));
                                        if (remaining == 0) showEmpty();
                                    }).start();
                            Toast.makeText(this,
                                    action.equals("accept") ? "Donation accepted ✓" : "Donation declined",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, json.optString("error", "Action failed"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Cannot connect to server",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",     sessionManager.getUserId());
                params.put("token",       sessionManager.getToken());
                params.put("donation_id", donationId);
                params.put("action",      action);
                return params;
            }
        };
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    // ─── Rendering ───────────────────────────────────────────────────────────

    private void renderDonations(JSONArray donations) {
        int count = donations.length();
        tvDonationCount.setText(count + " pending donation" + (count == 1 ? "" : "s"));
        llDonationList.removeAllViews();
        if (count == 0) { showEmpty(); return; }
        tvEmptyState.setVisibility(View.GONE);
        llDonationList.setVisibility(View.VISIBLE);
        for (int i = 0; i < count; i++) {
            try { addDonationCard(donations.getJSONObject(i)); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void addDonationCard(JSONObject item) {
        String donorName  = item.optString("donor_name",    "Unknown donor");
        String resource   = item.optString("resource_name", "Unknown item");
        String donationId = item.optString("donation_id",   "");
        int    qty        = item.optInt("quantity",         0);

        // Card wrapper
        LinearLayout.LayoutParams wrapLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapLP.bottomMargin = dp(16);

        CardView card = new CardView(this);
        card.setRadius(dp(20));
        card.setCardElevation(dp(4));
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(20), dp(20), dp(20), dp(20));

        // Donor row
        LinearLayout donorRow = new LinearLayout(this);
        donorRow.setOrientation(LinearLayout.HORIZONTAL);
        donorRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams donorRowLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        donorRowLP.bottomMargin = dp(12);

        // Avatar
        FrameLayout avatar = new FrameLayout(this);
        TextView tvInit = new TextView(this);
        tvInit.setText(donorName.isEmpty() ? "D"
                : String.valueOf(donorName.charAt(0)).toUpperCase());
        tvInit.setTextColor(Color.WHITE);
        tvInit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvInit.setTypeface(null, Typeface.BOLD);
        tvInit.setGravity(Gravity.CENTER);
        tvInit.setBackgroundColor(primaryColor());
        FrameLayout.LayoutParams initLP = new FrameLayout.LayoutParams(dp(44), dp(44));
        initLP.gravity = Gravity.CENTER;
        avatar.addView(tvInit, initLP);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(48)));

        // Name + resource
        LinearLayout nameCol = new LinearLayout(this);
        nameCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams nameColLP = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        nameColLP.setMarginStart(dp(12));

        TextView tvDonor = new TextView(this);
        tvDonor.setText(donorName);
        tvDonor.setTextColor(onSurface());
        tvDonor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvDonor.setTypeface(null, Typeface.BOLD);

        TextView tvRes = new TextView(this);
        tvRes.setText(resource);
        tvRes.setTextColor(onSurfaceVar());
        tvRes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        LinearLayout.LayoutParams resLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        resLP.topMargin = dp(2);
        nameCol.addView(tvDonor);
        nameCol.addView(tvRes, resLP);

        // Qty badge
        TextView tvQty = new TextView(this);
        tvQty.setText("Qty: " + qty);
        tvQty.setTextColor(primaryColor());
        tvQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvQty.setTypeface(null, Typeface.BOLD);
        tvQty.setPadding(dp(10), dp(4), dp(10), dp(4));
        try {
            tvQty.setBackground(getResources().getDrawable(R.drawable.verified_badge_bg, getTheme()));
        } catch (Exception ignored) {
            tvQty.setBackgroundColor(Color.parseColor("#e8f5e9"));
        }

        donorRow.addView(avatar);
        donorRow.addView(nameCol, nameColLP);
        donorRow.addView(tvQty);
        inner.addView(donorRow, donorRowLP);

        // Accept / Decline buttons
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.END);

        Button btnDecline = new Button(this);
        btnDecline.setText("Decline");
        btnDecline.setTextColor(errorColor());
        btnDecline.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnDecline.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams decLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(40));
        decLP.setMarginEnd(dp(10));
        btnDecline.setOnClickListener(v -> respondToDonation(donationId, "decline", card));

        Button btnAccept = new Button(this);
        btnAccept.setText("Accept ✓");
        btnAccept.setTextColor(Color.WHITE);
        btnAccept.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnAccept.setTypeface(null, Typeface.BOLD);
        btnAccept.setBackgroundColor(primaryColor());
        btnAccept.setOnClickListener(v -> respondToDonation(donationId, "accept", card));

        btnRow.addView(btnDecline, decLP);
        btnRow.addView(btnAccept, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(40)));

        inner.addView(btnRow);
        card.addView(inner);
        llDonationList.addView(card, wrapLP);
    }

    private void showEmpty() {
        tvDonationCount.setText("0 pending donations");
        tvEmptyState.setVisibility(View.VISIBLE);
        llDonationList.setVisibility(View.GONE);
    }

    // ─── Bottom nav (programmatic) ────────────────────────────────────────────

    private LinearLayout buildBottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setWeightSum(4);
        nav.setPadding(dp(8), dp(8), dp(8), dp(8));
        nav.setElevation(dp(12));
        try {
            nav.setBackground(getResources().getDrawable(R.drawable.bottom_nav_bg, getTheme()));
        } catch (Exception e) {
            nav.setBackgroundColor(Color.WHITE);
        }
        nav.addView(navItem("DISCOVER",  false, () -> startActivity(new Intent(this, DiscoverActivity.class))));
        nav.addView(navItem("DONATIONS", true,  () -> { }));
        nav.addView(navItem("REQUESTS",  false, () -> startActivity(new Intent(this, CommunityRequestsActivity.class))));
        nav.addView(navItem("PROFILE",   false, () -> startActivity(new Intent(this, ProfileActivity.class))));
        return nav;
    }

    private LinearLayout navItem(String label, boolean active, Runnable action) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(6), dp(6), dp(6), dp(6));
        item.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        if (active) {
            try {
                item.setBackground(getResources().getDrawable(R.drawable.nav_active_bg, getTheme()));
            } catch (Exception ignored) {}
        }
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setLetterSpacing(0.06f);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(active ? primaryColor() : mutedGrey());
        item.addView(tv);
        item.setOnTouchListener((v, event) -> {
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
        return item;
    }
}