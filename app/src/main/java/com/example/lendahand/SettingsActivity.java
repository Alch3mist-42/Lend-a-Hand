package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private static final String LOGOUT_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/logout.php";

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null)
            btnBack.setOnClickListener(v -> finish());

        // Account section
        addRippleNav(R.id.settingManageProfile, () ->
                startActivity(new Intent(this, ProfileActivity.class)));

        addRippleNav(R.id.settingDonationStatus, () ->
                startActivity(new Intent(this, DonationStatusActivity.class)));

        addRippleNav(R.id.settingPostRequest, () ->
                startActivity(new Intent(this, RecipientRequestActivity.class)));

        // Community section
        addRippleNav(R.id.settingCommunityRequests, () ->
                startActivity(new Intent(this, CommunityRequestsActivity.class)));

        addRippleNav(R.id.settingLeaderboard, () ->
                startActivity(new Intent(this, LeaderboardActivity.class)));

        addRippleNav(R.id.settingPriorityInfo, () ->
                startActivity(new Intent(this, PriorityExplainedActivity.class)));

        // Logout
        addRippleNav(R.id.settingLogout, this::logout);
    }

    private void logout() {
        StringRequest request = new StringRequest(Request.Method.POST, LOGOUT_URL,
                response -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                },
                error -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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

    private void addRippleNav(int viewId, Runnable action) {
        View view = findViewById(viewId);
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                            .setInterpolator(new OvershootInterpolator(2.5f))
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