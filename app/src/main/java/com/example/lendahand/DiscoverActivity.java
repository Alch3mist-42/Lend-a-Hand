package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.TextView;

public class DiscoverActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private ImageView btnBurger;
    private boolean drawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        drawerLayout = findViewById(R.id.drawerLayout);
        btnBurger = findViewById(R.id.btnBurger);

        // Burger plus/X toggle
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerOpen = true;
                if (btnBurger != null) {
                    btnBurger.animate().rotation(45f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(2f)).start();
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
                if (drawerOpen)
                    drawerLayout.closeDrawers();
                else
                    drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
            });

        // Wire drawer items
        wireDrawerItem(R.id.menuDiscover, null);
        wireDrawerItem(R.id.menuRequests, CommunityRequestsActivity.class);
        wireDrawerItem(R.id.menuLeaderboard, LeaderboardActivity.class);
        wireDrawerItem(R.id.menuProfile, ProfileActivity.class);
        wireDrawerItem(R.id.menuImpactLog, ProfileActivity.class);
        wireDrawerItem(R.id.menuSettings, null);

        View menuLogout = findViewById(R.id.menuLogout);
        if (menuLogout != null)
            menuLogout.setOnClickListener(v -> {
                drawerLayout.closeDrawers();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
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

        // Tier cards → Priority Explained screen
        addPressAndNavigate(R.id.cardTier1, PriorityExplainedActivity.class);
        addPressAndNavigate(R.id.cardTier2, PriorityExplainedActivity.class);
        addPressAndNavigate(R.id.cardTier3, PriorityExplainedActivity.class);

        // Guardians Circle → Leaderboard
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

        // Bounce arrows on page load
        bounceArrows();
    }

    private void bounceArrows() {
        // Find all arrow TextViews and animate them
        View root = getWindow().getDecorView().getRootView();
        animateArrowInView(root);
    }

    private void animateArrowInView(View v) {
        if (v instanceof TextView) {
            String text = ((TextView) v).getText().toString();
            if (text.contains("→") || text.contains("↓")) {
                v.post(() -> {
                    v.animate().translationX(8f).setDuration(400)
                            .setInterpolator(new OvershootInterpolator(2f))
                            .withEndAction(() ->
                                    v.animate().translationX(0f).setDuration(300)
                                            .setInterpolator(new OvershootInterpolator(3f)).start()
                            ).start();
                });
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
            if (target != null)
                startActivity(new Intent(this, target));
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
                    v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(300)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                    break;
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
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                            .setInterpolator(new OvershootInterpolator(2.5f)).start();
                    startActivity(new Intent(this, target));
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    break;
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
// Note: addReboundAnimation for hero card already handled in existing DiscoverActivity
// The cardHero id needs wiring in onCreate — patch below is added to existing file