package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;

public class LeaderboardActivity extends BaseActivity {

    // Member data: name, initials, rank label, points, rank#, donations, bio, avatar drawable
    private static final Object[][] MEMBERS = {
            {"Marcus Thorne",  "MT", "Legendary Donor", "12.5k", "#1", "248",
                    "A passionate advocate for community health. Marcus has donated consistently for over two years, focusing on medical supplies and school resources.", R.drawable.avatar_bg_gold},
            {"Elena Vance",    "EV", "Pillar of Support","8.2k", "#2", "187",
                    "Elena believes education is the greatest gift. She donates school supplies every term and organises community drives in her neighbourhood.", R.drawable.avatar_bg_indigo},
            {"Dr. Julian Gray","JG", "Pillar of Support","7.9k", "#3", "176",
                    "As a doctor, Julian understands urgency. He donates medical supplies monthly and mentors young professionals about community responsibility.", R.drawable.avatar_bg_green},
            {"Sarah Adeyemi",  "SA", "Rising Guardian",  "5.1k", "#4", "132",
                    "Sarah grew up in a community that relied on donations. Now she gives back every month and inspires her friends to join the movement.", R.drawable.avatar_bg_indigo},
            {"Kagiso Molefe",  "KM", "Community Helper", "3.4k", "#5", "98",
                    "Kagiso started donating after volunteering at a local shelter. He focuses on food and household items for families in transition.", R.drawable.avatar_bg_green},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Wire ranked cards to member profiles
        int[] cardIds = {R.id.cardRank1, R.id.cardRank2, R.id.cardRank3,
                R.id.cardRank4, R.id.cardRank5};
        for (int i = 0; i < cardIds.length; i++) {
            final int idx = i;
            CardView card = findViewById(cardIds[i]);
            if (card == null) continue;
            card.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                                .setInterpolator(new OvershootInterpolator(2.5f))
                                .withEndAction(() -> openMemberProfile(idx)).start();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                        break;
                }
                return true;
            });
        }

        // Bottom nav
        setupNavItem(R.id.navDiscover, () ->
                startActivity(new Intent(this, DiscoverActivity.class)));
        setupNavItem(R.id.navDonate, () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));
        setupNavItem(R.id.navActivity, () -> {});
        setupNavItem(R.id.navProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void openMemberProfile(int idx) {
        Object[] m = MEMBERS[idx];
        Intent intent = new Intent(this, MemberProfileActivity.class);
        intent.putExtra("member_name",      (String) m[0]);
        intent.putExtra("member_initials",  (String) m[1]);
        intent.putExtra("member_rank",      (String) m[2]);
        intent.putExtra("member_points",    (String) m[3]);
        intent.putExtra("member_rank_num",  (String) m[4]);
        intent.putExtra("member_donations", (String) m[5]);
        intent.putExtra("member_bio",       (String) m[6]);
        intent.putExtra("member_avatar_bg", (int)    m[7]);
        startActivity(intent);
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