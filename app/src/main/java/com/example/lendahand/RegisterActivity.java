package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "REGISTER_DEBUG";
    private static final String SIGNUP_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/signup.php";

    private TextInputEditText etFullName, etUsername, etEmail,
            etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private CheckBox cbTerms;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager    = new SessionManager(this);
        etFullName        = findViewById(R.id.etFullName);
        etUsername        = findViewById(R.id.etUsername);
        etEmail           = findViewById(R.id.etEmail);
        etPhone           = findViewById(R.id.etPhone);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);
        cbTerms           = findViewById(R.id.cbTerms);

        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnRegister != null)
            btnRegister.setOnClickListener(v -> attemptRegister());

        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        if (tvGoToLogin != null)
            tvGoToLogin.setOnClickListener(v ->
                    startActivity(new Intent(this, MainActivity.class)));
    }

    private void attemptRegister() {
        String fullName = getValue(etFullName);
        String username = getValue(etUsername);
        String email    = getValue(etEmail);
        String phone    = getValue(etPhone);
        String password = getValue(etPassword);
        String confirm  = getValue(etConfirmPassword);

        // ── Client-side validation ───────────────────────────────────────
        if (fullName.isEmpty()) { etFullName.setError("Required"); return; }
        if (username.isEmpty()) { etUsername.setError("Required"); return; }
        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters"); return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required"); return;
        }
        if (phone.isEmpty()) { etPhone.setError("Required"); return; }
        if (password.length() < 6) { etPassword.setError("Min 6 characters"); return; }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Passwords don't match"); return;
        }
        if (cbTerms != null && !cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to Terms & Conditions",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String[] parts = fullName.trim().split(" ", 2);
        String name    = parts[0];
        String surname = parts.length > 1 ? parts[1] : "";

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        Log.d(TAG, "Sending registration for username: " + username);

        StringRequest request = new StringRequest(Request.Method.POST, SIGNUP_URL,
                response -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Create Account");

                    // Log raw server response so we can debug
                    Log.d(TAG, "Raw server response: " + response);

                    // Strip any unexpected characters before the JSON
                    String cleaned = response.trim();
                    int jsonStart = cleaned.indexOf("{");
                    if (jsonStart > 0) {
                        Log.w(TAG, "Non-JSON prefix stripped: " + cleaned.substring(0, jsonStart));
                        cleaned = cleaned.substring(jsonStart);
                    }

                    if (cleaned.isEmpty()) {
                        Log.e(TAG, "Empty response from server");
                        Toast.makeText(this,
                                "Server returned empty response. Check signup.php",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(cleaned);
                        Log.d(TAG, "Parsed JSON: " + json.toString());

                        // ── Success ──────────────────────────────────────
                        if (json.has("success") ||
                                "success".equalsIgnoreCase(json.optString("status"))) {

                            Log.d(TAG, "Registration successful for: " + username);
                            sessionManager.saveSession(
                                    json.optString("user_id", username),
                                    fullName,
                                    json.optString("role", "user"),
                                    json.optString("token", "")
                            );
                            startActivity(new Intent(this, DiscoverActivity.class));
                            finish();

                        } else {
                            // ── Server-side error ────────────────────────
                            String error = json.optString("error",
                                    json.optString("message",
                                            json.optString("msg", "Registration failed")));

                            Log.e(TAG, "Server error: " + error);

                            String el = error.toLowerCase();
                            if (el.contains("username") || el.contains("user_id")
                                    || el.contains("taken")   || el.contains("exists")
                                    || el.contains("duplicate")) {
                                etUsername.setError("Username already taken. Choose another.");
                                etUsername.requestFocus();

                            } else if (el.contains("email")) {
                                etEmail.setError("Email already registered.");
                                etEmail.requestFocus();

                            } else {
                                // Show the raw error so we can see exactly what the
                                // server is saying while debugging
                                Toast.makeText(this,
                                        "Error: " + error, Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "JSON parse failed. Raw: " + response, e);
                        Toast.makeText(this,
                                "Unexpected server response:\n" + response,
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Create Account");
                    String msg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Log.e(TAG, "Volley error: " + msg);
                    Toast.makeText(this,
                            "Cannot connect to server. Check your connection.",
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",  username);
                params.put("name",     name);
                params.put("surname",  surname);
                params.put("email",    email);
                params.put("phone",    phone);
                params.put("password", password);
                Log.d(TAG, "POST params: " + params.toString());
                return params;
            }
        };

        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private String getValue(TextInputEditText field) {
        return field != null && field.getText() != null
                ? field.getText().toString().trim() : "";
    }
}