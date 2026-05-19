package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardActivity extends BaseActivity {

    private static final String LEADERBOARD_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/leaderboard.php";

    // Fallback static data
    private static final Object[][] MEMBERS = {
            {"Marcus Thorne",   "MT", "Legendary Donor",  "248", "#1", "248",
                    "A passionate advocate for community health.",   R.drawable.avatar_bg_gold},
            {"Elena Vance",     "EV", "Pillar of Support","187", "#2", "187",
                    "Elena believes education is the greatest gift.", R.drawable.avatar_bg_indigo},
            {"Dr. Julian Gray", "JG", "Pillar of Support","176", "#3", "176",
                    "As a doctor, Julian understands urgency.",       R.drawable.avatar_bg_green},
            {"Sarah Adeyemi",   "SA", "Rising Guardian",  "132", "#4", "132",
                    "Sarah gives back every month.",                  R.drawable.avatar_bg_indigo},
            {"Kagiso Molefe",   "KM", "Community Helper", "98",  "#5", "98",
                    "Kagiso focuses on food and household items.",    R.drawable.avatar_bg_green},
    };

    // Card UI ID mappings
    private static final int[] NAME_IDS     = {R.id.tvName1,        R.id.tvName2,        R.id.tvName3,        R.id.tvName4,        R.id.tvName5};
    private static final int[] INITIALS_IDS = {R.id.tvInitials1List,R.id.tvInitials2List,R.id.tvInitials3List,R.id.tvInitials4List,R.id.tvInitials5List};
    private static final int[] POINTS_IDS   = {R.id.tvPoints1,      R.id.tvPoints2,      R.id.tvPoints3,      R.id.tvPoints4,      R.id.tvPoints5};
    private static final int[] RANK_IDS     = {R.id.tvRank1,        R.id.tvRank2,        R.id.tvRank3,        R.id.tvRank4,        R.id.tvRank5};
    private static final int[] PODIUM_NAME_IDS   = {R.id.tvPodiumName1,   R.id.tvPodiumName2,   R.id.tvPodiumName3};
    private static final int[] PODIUM_POINTS_IDS = {R.id.tvPodiumPoints1, R.id.tvPodiumPoints2, R.id.tvPodiumPoints3};
    private static final int[] PODIUM_INITIALS_IDS = {R.id.tvInitials1,   R.id.tvInitials2,     R.id.tvInitials3};

    private final Object[][] dynamicMembers = new Object[5][8];
    private boolean serverDataLoaded = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        sessionManager = new SessionManager(this);

        // Wire card taps
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

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        StringRequest request = new StringRequest(Request.Method.POST, LEADERBOARD_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("error")) return;

                        JSONArray donors = json.getJSONArray("leaderboard");
                        int count = Math.min(donors.length(), 5);

                        for (int i = 0; i < count; i++) {
                            JSONObject d  = donors.getJSONObject(i);
                            String name   = d.optString("name", "Donor");
                            String points = d.optString("points", "0");

                            // Build initials
                            String[] parts  = name.trim().split(" ");
                            String initials = parts[0].substring(0, 1).toUpperCase();
                            if (parts.length > 1 && parts[parts.length-1].length() > 0)
                                initials += parts[parts.length-1].substring(0, 1).toUpperCase();

                            // Rank label from points
                            int pts = 0;
                            try { pts = Integer.parseInt(points); } catch (Exception ignored) {}
                            String rankLabel;
                            if      (pts >= 250) rankLabel = "Legendary Donor";
                            else if (pts >= 120) rankLabel = "Guardian";
                            else if (pts >= 60)  rankLabel = "Uplifter";
                            else if (pts >= 20)  rankLabel = "Helper";
                            else                 rankLabel = "Seedling";

                            int avatarBg = i == 0 ? R.drawable.avatar_bg_gold
                                    : i == 1 ? R.drawable.avatar_bg_indigo
                                    : R.drawable.avatar_bg_green;

                            dynamicMembers[i][0] = name;
                            dynamicMembers[i][1] = initials;
                            dynamicMembers[i][2] = rankLabel;
                            dynamicMembers[i][3] = points;
                            dynamicMembers[i][4] = "#" + (i + 1);
                            dynamicMembers[i][5] = points;
                            dynamicMembers[i][6] = "A dedicated ASTERA community donor.";
                            dynamicMembers[i][7] = avatarBg;

                            // Update card UI directly
                            updateCardUI(i, name, initials, points, rankLabel);
                        }
                        serverDataLoaded = true;

                    } catch (Exception e) {
                        // Keep static fallback
                    }
                },
                error -> { /* Keep static fallback */ }
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

    private void updateCardUI(int idx, String name, String initials,
                              String points, String rankLabel) {
        if (idx >= NAME_IDS.length) return;

        // Update list card
        TextView tvName = findViewById(NAME_IDS[idx]);
        if (tvName != null) tvName.setText(name);

        TextView tvInitials = findViewById(INITIALS_IDS[idx]);
        if (tvInitials != null) tvInitials.setText(initials);

        TextView tvPoints = findViewById(POINTS_IDS[idx]);
        if (tvPoints != null) tvPoints.setText(points + " pts");

        TextView tvRank = findViewById(RANK_IDS[idx]);
        if (tvRank != null) tvRank.setText(rankLabel);

        // Update podium (only top 3)
        if (idx < 3) {
            // Podium order is: 2nd, 1st, 3rd in XML
            // so idx 0=1st place, idx 1=2nd place, idx 2=3rd place
            TextView tvPodiumName = findViewById(PODIUM_NAME_IDS[idx]);
            if (tvPodiumName != null) {
                // Shorten name for podium display
                String[] parts = name.trim().split(" ");
                String shortName = parts[0] + (parts.length > 1
                        ? " " + parts[parts.length-1].substring(0,1) + "." : "");
                tvPodiumName.setText(shortName);
            }

            TextView tvPodiumPoints = findViewById(PODIUM_POINTS_IDS[idx]);
            if (tvPodiumPoints != null) tvPodiumPoints.setText(points + " pts");

            TextView tvPodiumInitials = findViewById(PODIUM_INITIALS_IDS[idx]);
            if (tvPodiumInitials != null) tvPodiumInitials.setText(initials);
        }
    }

    private void openMemberProfile(int idx) {
        Object[] m = serverDataLoaded ? dynamicMembers[idx] : MEMBERS[idx];
        if (m == null || m[0] == null) m = MEMBERS[idx];
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