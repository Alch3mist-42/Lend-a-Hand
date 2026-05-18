package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DiscoverActivity extends BaseActivity {

    private static final String BASE_URL = "https://wmc.ms.wits.ac.za/students/sgroup2713/";
    private static final String LOGOUT_URL  = BASE_URL + "logout.php";

    private DrawerLayout drawerLayout;
    private ImageView btnBurger;
    private boolean drawerOpen = false;
    private long backPressedTime = 0;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        sessionManager = new SessionManager(this);

        // Welcome toast
        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty())
            Toast.makeText(this, "Welcome back, " + username + "! 🌿",
                    Toast.LENGTH_LONG).show();

        drawerLayout = findViewById(R.id.drawerLayout);
        btnBurger    = findViewById(R.id.btnBurger);

        // Burger toggle
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerOpen = true;
                if (btnBurger != null) {
                    btnBurger.setImageResource(R.drawable.ic_menu_close_green);
                    btnBurger.animate().rotation(0f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(2f)).start();
                }
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                drawerOpen = false;
                if (btnBurger != null) {
                    btnBurger.setImageResource(R.drawable.ic_menu_plus_green);
                    btnBurger.animate().rotation(0f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(2f)).start();
                }
            }
        });

        if (btnBurger != null)
            btnBurger.setOnClickListener(v -> {
                if (drawerOpen) drawerLayout.closeDrawers();
                else drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
            });

        // Drawer items
        wireDrawerItem(R.id.menuDiscover, null);
        wireDrawerItem(R.id.menuRequests, CommunityRequestsActivity.class);
        wireDrawerItem(R.id.menuLeaderboard, LeaderboardActivity.class);
        wireDrawerItem(R.id.menuProfile, ProfileActivity.class);
        wireDrawerItem(R.id.menuImpactLog, ProfileActivity.class);
        wireDrawerItem(R.id.menuSettings, null);

        // Logout — calls logout.php then clears session
        View menuLogout = findViewById(R.id.menuLogout);
        if (menuLogout != null)
            menuLogout.setOnClickListener(v -> {
                drawerLayout.closeDrawers();
                logout();
            });

        // Bell
        View btnBell = findViewById(R.id.btnBell);
        if (btnBell != null)
            btnBell.setOnClickListener(v -> showNotificationBottomSheet());

        // CTA buttons
        View btnGiver = findViewById(R.id.btnBecomeGiver);
        if (btnGiver != null)
            btnGiver.setOnClickListener(v ->
                    startActivity(new Intent(this, CommunityRequestsActivity.class)));

        View btnNeed = findViewById(R.id.btnFindNeed);
        if (btnNeed != null)
            btnNeed.setOnClickListener(v ->
                    startActivity(new Intent(this, CommunityRequestsActivity.class)));

        // Tier cards
        addPressAndNavigate(R.id.cardTier1, PriorityExplainedActivity.class);
        addPressAndNavigate(R.id.cardTier2, PriorityExplainedActivity.class);
        addPressAndNavigate(R.id.cardTier3, PriorityExplainedActivity.class);

        // Hero card rebound
        addReboundAnimation(findViewById(R.id.cardHero));

        // Guardians → Leaderboard
        View btnLeaderboard = findViewById(R.id.btnViewLeaderboard);
        if (btnLeaderboard != null)
            btnLeaderboard.setOnClickListener(v ->
                    startActivity(new Intent(this, LeaderboardActivity.class)));

        // Bottom nav
        setupNavItem(R.id.navDiscover, () -> {});
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));
        setupNavItem(R.id.navActivity, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));

        bounceArrows();
    }

    // ── Double back to exit ──
    @Override
    public void onBackPressed() {
        if (drawerOpen) { drawerLayout.closeDrawers(); return; }
        long currentTime = System.currentTimeMillis();
        if (currentTime - backPressedTime < 2000) {
            finishAffinity();
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        StringRequest request = new StringRequest(Request.Method.POST, LOGOUT_URL,
                response -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                },
                error -> {
                    // Clear session anyway and redirect
                    sessionManager.clearSession();
                    Intent intent = new Intent(this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
        );
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void bounceArrows() {
        View root = getWindow().getDecorView().getRootView();
        animateArrowInView(root);
    }

    private void animateArrowInView(View v) {
        if (v instanceof TextView) {
            String text = ((TextView) v).getText().toString();
            if (text.contains("→") || text.contains("↓")) {
                v.post(() -> v.animate().translationX(8f).setDuration(400)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .withEndAction(() -> v.animate().translationX(0f).setDuration(300)
                                .setInterpolator(new OvershootInterpolator(3f)).start()).start());
            }
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++)
                animateArrowInView(vg.getChildAt(i));
        }
    }

    private void wireDrawerItem(int id, Class<?> target) {
        View item = findViewById(id);
        if (item == null) return;
        item.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            if (target != null) startActivity(new Intent(this, target));
        });
        addReboundAnimation(item);
    }

    private void showNotificationBottomSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this,
                com.google.android.material.R.style.Theme_Material3_Light_BottomSheetDialog);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(56, 48, 56, 80);
        container.setBackgroundResource(android.R.color.white);

        TextView title = new TextView(this);
        title.setText("Notifications");
        title.setTextSize(22f);
        title.setTextColor(0xFF191c1e);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 32);
        container.addView(title);

        addNotifItem(container, "🏆", "Tier Upgrade",
                "You've reached Helper rank! 18 more credits to Uplifter.", "#c9a900");
        addNotifItem(container, "✅", "Donation Confirmed",
                "Your donation has been received.", "#004f45");
        addNotifItem(container, "📢", "Community Request",
                "Local Shelter needs 50 blankets urgently.", "#4c56af");
        addNotifItem(container, "💳", "Credits Awarded",
                "+12 credits for your City Park donation.", "#004f45");

        sheet.setContentView(container);
        sheet.show();
    }

    private void addNotifItem(LinearLayout parent, String emoji,
                              String heading, String body, String hex) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.drawable.verified_badge_bg);
        row.setPadding(28, 24, 28, 24);

        TextView icon = new TextView(this);
        icon.setText(emoji);
        icon.setTextSize(20f);
        icon.setPadding(0, 0, 20, 0);
        row.addView(icon);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView h = new TextView(this);
        h.setText(heading);
        h.setTextSize(14f);
        h.setTextColor(android.graphics.Color.parseColor(hex));
        h.setTypeface(null, android.graphics.Typeface.BOLD);
        col.addView(h);

        TextView b = new TextView(this);
        b.setText(body);
        b.setTextSize(13f);
        b.setTextColor(0xFF3e4946);
        b.setLineSpacing(4f, 1f);
        b.setPadding(0, 4, 0, 0);
        col.addView(b);

        row.addView(col);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = 14;
        row.setLayoutParams(p);
        parent.addView(row);
    }

    private void addReboundAnimation(View view) {
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80).start(); break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(300)
                            .setInterpolator(new OvershootInterpolator(3f)).start(); break;
            }
            return false;
        });
    }

    private void addPressAndNavigate(int cardId, Class<?> target) {
        CardView card = findViewById(cardId);
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start(); break;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                            .setInterpolator(new OvershootInterpolator(2.5f)).start();
                    startActivity(new Intent(this, target)); break;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start(); break;
            }
            return true;
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