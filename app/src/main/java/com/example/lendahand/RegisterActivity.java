package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://192.168.1.100/lendahand";

    private TextInputEditText etFullName, etUsername, etEmail,
            etPassword, etConfirmPassword, etBio, etContact;
    private TextInputLayout layoutBio, layoutContact;
    private TextView labelBio;
    private MaterialButton btnDonor, btnRecipient, btnRegister;
    private CheckBox checkTerms;
    private String selectedUserType = "donor";

    // Strength bar views
    private View bar1, bar2, bar3, bar4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName        = findViewById(R.id.etFullName);
        etUsername        = findViewById(R.id.etUsername);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etBio             = findViewById(R.id.etBio);
        etContact         = findViewById(R.id.etContact);
        layoutBio         = findViewById(R.id.layoutBio);
        layoutContact     = findViewById(R.id.layoutContact);
        labelBio          = findViewById(R.id.labelBio);
        btnDonor          = findViewById(R.id.btnDonor);
        btnRecipient      = findViewById(R.id.btnRecipient);
        btnRegister       = findViewById(R.id.btnRegister);
        checkTerms        = findViewById(R.id.checkTerms);
        bar1 = findViewById(R.id.strengthBar1);
        bar2 = findViewById(R.id.strengthBar2);
        bar3 = findViewById(R.id.strengthBar3);
        bar4 = findViewById(R.id.strengthBar4);

        // Auto-focus first field
        if (etFullName != null) etFullName.requestFocus();

        // Password strength watcher
        if (etPassword != null) {
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePasswordStrength(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // User type toggle
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

        btnRegister.setOnClickListener(v -> attemptRegister());

        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        if (tvGoToLogin != null)
            tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void updatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 6)  strength++;
        if (password.length() >= 10) strength++;
        if (password.matches(".*[A-Z].*") || password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+].*")) strength++;

        int green  = 0xFF004f45;
        int yellow = 0xFFc9a900;
        int red    = 0xFFba1a1a;
        int grey   = 0xFFe0e3e5;

        bar1.setBackgroundColor(strength >= 1 ? red    : grey);
        bar2.setBackgroundColor(strength >= 2 ? yellow : grey);
        bar3.setBackgroundColor(strength >= 3 ? yellow : grey);
        bar4.setBackgroundColor(strength >= 4 ? green  : grey);
    }

    private void attemptRegister() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email    = etEmail.getText()    != null ? etEmail.getText().toString().trim()    : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirm  = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
        String bio      = etBio.getText()      != null ? etBio.getText().toString().trim()      : "";

        // Validation
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email is required");
            etEmail.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }
        if (selectedUserType.equals("recipient") && bio.isEmpty()) {
            etBio.setError("Bio is required for recipients");
            etBio.requestFocus();
            return;
        }
        if (checkTerms != null && !checkTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms of Service to continue",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // TEMPORARY DEMO BYPASS
        Toast.makeText(this, "Account created! Welcome " + fullName, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this, DiscoverActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("user_type", selectedUserType);
        startActivity(intent);
        finish();

        // VOLLEY VERSION (uncomment when backend is ready)
        /*
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");
        String contact = etContact.getText() != null ? etContact.getText().toString().trim() : "";
        JSONObject body = new JSONObject();
        try {
            body.put("full_name", fullName);
            body.put("username", username);
            body.put("email", email);
            body.put("password", password);
            body.put("user_type", selectedUserType);
            body.put("bio", bio);
            body.put("contact_info", contact);
        } catch (JSONException e) { e.printStackTrace(); }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
            SERVER_URL + "/register.php", body,
            response -> {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                try {
                    if (response.getString("status").equals("success")) {
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                Toast.makeText(this, "Cannot connect to server.", Toast.LENGTH_SHORT).show();
            });
        requestQueue.add(request);
        */
    }
}