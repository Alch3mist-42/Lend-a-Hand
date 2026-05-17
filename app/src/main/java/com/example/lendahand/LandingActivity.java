package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        MaterialButton btnGetStarted = findViewById(R.id.btnGetStarted);
        MaterialButton btnSignIn = findViewById(R.id.btnGoToLoginOutlined);

        if (btnGetStarted != null)
            btnGetStarted.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class)));

        if (btnSignIn != null)
            btnSignIn.setOnClickListener(v ->
                    startActivity(new Intent(this, MainActivity.class)));

        animateIn(btnGetStarted, 300);
        animateIn(btnSignIn, 420);
    }

    private void animateIn(View v, long delay) {
        if (v == null) return;
        v.setAlpha(0f);
        v.setTranslationY(30f);
        v.animate().alpha(1f).translationY(0f)
                .setStartDelay(delay).setDuration(400)
                .setInterpolator(new OvershootInterpolator(1.2f)).start();
    }
}