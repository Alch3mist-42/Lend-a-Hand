package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    // ── Swap this IP for Member 1's actual server IP when ready ──
    private static final String SERVER_URL = "http://192.168.1.100/lendahand";

    private TextInputEditText etUsername, etPassword, etBio, etContact;
    private TextInputLayout layoutBio, layoutContact;
    private TextView labelBio;
    private MaterialButton btnDonor, btnRecipient, btnRegister;
    private String selectedUserType = "donor"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername    = findViewById(R.id.etUsername);
        etPassword    = findViewById(R.id.etPassword);
        etBio         = findViewById(R.id.etBio);
        etContact     = findViewById(R.id.etContact);
        layoutBio     = findViewById(R.id.layoutBio);
        layoutContact = findViewById(R.id.layoutContact);
        labelBio      = findViewById(R.id.labelBio);
        btnDonor      = findViewById(R.id.btnDonor);
        btnRecipient  = findViewById(R.id.btnRecipient);
        btnRegister   = findViewById(R.id.btnRegister);

        // ── User type toggle ──
        btnDonor.setOnClickListener(v -> {
            selectedUserType = "donor";
            layoutBio.setVisibility(View.GONE);
            labelBio.setVisibility(View.GONE);
            layoutContact.setVisibility(View.GONE);
        });

        btnRecipient.setOnClickListener(v -> {
            selectedUserType = "recipient";
            layoutBio.setVisibility(View.VISIBLE);
            labelBio.setVisibility(View.VISIBLE);
            layoutContact.setVisibility(View.VISIBLE);
        });

        // ── Register button ──
        btnRegister.setOnClickListener(v -> attemptRegister());

        // ── Back to login ──
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String bio      = etBio.getText() != null ? etBio.getText().toString().trim() : "";

        // ── Input validation ──
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (selectedUserType.equals("recipient") && bio.isEmpty()) {
            etBio.setError("Bio is required for recipients");
            etBio.requestFocus();
            return;
        }

        // ── TEMPORARY DEMO BYPASS ──
        // When Member 1's server is ready, replace this block with the
        // Volley POST request below (currently commented out)
        Toast.makeText(this, "Account created! Welcome " + username, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this, DiscoverActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("user_type", selectedUserType);
        startActivity(intent);
        finish();

        // ── VOLLEY VERSION (uncomment when backend is ready) ──
        /*
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        String contact = etContact.getText() != null ? etContact.getText().toString().trim() : "";

        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
            body.put("user_type", selectedUserType);
            body.put("bio", bio);
            body.put("contact_info", contact);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            SERVER_URL + "/register.php",
            body,
            response -> {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                try {
                    String status = response.getString("status");
                    if (status.equals("success")) {
                        Toast.makeText(this,
                            "Account created! Please sign in.",
                            Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this,
                            response.getString("message"),
                            Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                Toast.makeText(this,
                    "Cannot connect to server. Check your connection.",
                    Toast.LENGTH_SHORT).show();
            }
        );
        requestQueue.add(request);
        */
    }
}