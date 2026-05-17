package com.example.lendahand;

import android.os.Bundle;
import android.widget.TextView;

public class StatusTrackerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_tracker);

        // Back button
        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null)
            btnBack.setOnClickListener(v -> finish());

        // Populate from intent extras if passed
        if (getIntent().hasExtra("recipient_name")) {
            TextView subtitle = findViewById(R.id.tvTrackerSubtitle);
            if (subtitle != null) {
                String name = getIntent().getStringExtra("recipient_name");
                String category = getIntent().getStringExtra("category");
                subtitle.setText("To: " + name + " • " + category);
            }
        }
    }
}