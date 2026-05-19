package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
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

        if (fullName.isEmpty()) { etFullName.setError("Required"); return; }
        if (username.isEmpty()) { etUsername.setError("Required"); return; }
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

        StringRequest request = new StringRequest(Request.Method.POST, SIGNUP_URL,
                response -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Create Account");
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("success")) {

                            // Save full session with token from server
                            sessionManager.saveSession(
                                    json.optString("user_id", username),
                                    fullName,
                                    json.optString("role", "user"),
                                    json.optString("token", "")
                            );

                            // Navigate without username extra to avoid
                            // the "Welcome back" toast in DiscoverActivity
                            startActivity(new Intent(this, DiscoverActivity.class));
                            finish();

                        } else {
                            Toast.makeText(this,
                                    json.optString("error", "Registration failed"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Unexpected response",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Create Account");
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