package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class LeaderboardActivity extends BaseActivity {

    private static final String BASE_URL = "https://wmc.ms.wits.ac.za/students/sgroup2713/";
    private static final String LEADERBOARD_URL = BASE_URL + "leaderboard.php";

    // Fallback static member data
    private static final Object[][] MEMBERS = {
            {"Marcus Thorne",  "MT", "Legendary Donor", "12.5k", "#1", "248",
                    "A passionate advocate for community health.", R.drawable.avatar_bg_gold},
            {"Elena Vance",    "EV", "Pillar of Support","8.2k", "#2", "187",
                    "Elena believes education is the greatest gift.", R.drawable.avatar_bg_indigo},
            {"Dr. Julian Gray","JG", "Pillar of Support","7.9k", "#3", "176",
                    "As a doctor, Julian understands urgency.", R.drawable.avatar_bg_green},
            {"Sarah Adeyemi",  "SA", "Rising Guardian",  "5.1k", "#4", "132",
                    "Sarah gives back every month.", R.drawable.avatar_bg_indigo},
            {"Kagiso Molefe",  "KM", "Community Helper", "3.4k", "#5", "98",
                    "Kagiso focuses on food and household items.", R.drawable.avatar_bg_green},
    };

    // Dynamic member data loaded from server
    private final Object[][] dynamicMembers = new Object[5][8];
    private boolean serverDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Wire ranked cards
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

        // Load real leaderboard data
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        StringRequest request = new StringRequest(Request.Method.GET, LEADERBOARD_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray donors = json.getJSONArray("leaderboard");
                        for (int i = 0; i < Math.min(donors.length(), 5); i++) {
                            JSONObject d = donors.getJSONObject(i);
                            String name  = d.optString("name", "Donor");
                            String points = d.optString("points", "0");
                            String initials = name.length() >= 2
                                    ? name.substring(0,1).toUpperCase()
                                    + name.split(" ")[name.split(" ").length-1].substring(0,1).toUpperCase()
                                    : name.substring(0,1).toUpperCase();
                            dynamicMembers[i][0] = name;
                            dynamicMembers[i][1] = initials;
                            dynamicMembers[i][2] = "Rank #" + (i + 1);
                            dynamicMembers[i][3] = points;
                            dynamicMembers[i][4] = "#" + (i + 1);
                            dynamicMembers[i][5] = points;
                            dynamicMembers[i][6] = "A dedicated ASTERA community donor.";
                            dynamicMembers[i][7] = R.drawable.avatar_bg_green;


                        }
                        serverDataLoaded = true;
                    } catch (Exception e) {
                        // Keep static fallback data
                    }
                },
                error -> { /* Keep static fallback */ }
        );
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }



    private void openMemberProfile(int idx) {
        Object[] m = serverDataLoaded ? dynamicMembers[idx] : MEMBERS[idx];
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