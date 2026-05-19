package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

// This is the base class that all our activities extend
// It adds shared behaviour like screen transitions and window settings
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the app content fit inside the system bars (status bar, navigation bar)
        // This stops the layout from going under the top or bottom bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
    }

    // Every time we open a new screen, add a smooth fade-in transition
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // Every time we close a screen, add a smooth fade-out transition
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}