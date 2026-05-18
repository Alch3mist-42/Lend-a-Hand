package com.example.lendahand;

import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MemberProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);

        // Back button
        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Populate from intent extras
        String name      = getIntent().getStringExtra("member_name");
        String initials  = getIntent().getStringExtra("member_initials");
        String rank      = getIntent().getStringExtra("member_rank");
        String points    = getIntent().getStringExtra("member_points");
        String rankNum   = getIntent().getStringExtra("member_rank_num");
        String donations = getIntent().getStringExtra("member_donations");
        String bio       = getIntent().getStringExtra("member_bio");
        int    avatarBg  = getIntent().getIntExtra("member_avatar_bg",
                R.drawable.avatar_bg_gold);

        setText(R.id.tvMemberName,      name,      "Member");
        setText(R.id.tvMemberInitials,  initials,  "MT");
        setText(R.id.tvMemberRank,      rank,      "Legendary Donor");
        setText(R.id.tvMemberPoints,    points,    "—");
        setText(R.id.tvMemberRankNum,   rankNum,   "#1");
        setText(R.id.tvMemberDonations, donations, "—");
        setText(R.id.tvMemberBio,       bio,
                "A passionate community member dedicated to making a lasting difference.");
        setText(R.id.tvMemberTitle,
                name != null ? name + "'s Profile" : "Guardian Profile", "Guardian Profile");

        // Set avatar background
        TextView tvInitials = findViewById(R.id.tvMemberInitials);
        if (tvInitials != null) tvInitials.setBackgroundResource(avatarBg);

        // Entrance animations
        animateEntrance();
    }

    private void setText(int id, String value, String fallback) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(value != null ? value : fallback);
    }

    private void animateEntrance() {
        int[] ids = {
                R.id.tvMemberName, R.id.tvMemberRank,
                R.id.tvMemberPoints, R.id.tvMemberRankNum, R.id.tvMemberDonations
        };
        for (int i = 0; i < ids.length; i++) {
            View v = findViewById(ids[i]);
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationY(20f);
            v.animate()
                    .alpha(1f).translationY(0f)
                    .setStartDelay(100L * i)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .start();
        }
    }
}