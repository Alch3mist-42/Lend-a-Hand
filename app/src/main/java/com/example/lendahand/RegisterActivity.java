package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName        = findViewById(R.id.etFullName);
        etUsername        = findViewById(R.id.etUsername);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);
        cbTerms           = findViewById(R.id.cbTerms);

        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnRegister != null) btnRegister.setOnClickListener(v -> attemptRegister());

        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        if (tvGoToLogin != null)
            tvGoToLogin.setOnClickListener(v ->
                    startActivity(new Intent(this, MainActivity.class)));
    }

    private void attemptRegister() {
        String fullName = etFullName != null && etFullName.getText() != null
                ? etFullName.getText().toString().trim() : "";
        String username = etUsername != null && etUsername.getText() != null
                ? etUsername.getText().toString().trim() : "";
        String email    = etEmail != null && etEmail.getText() != null
                ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null && etPassword.getText() != null
                ? etPassword.getText().toString().trim() : "";
        String confirm  = etConfirmPassword != null && etConfirmPassword.getText() != null
                ? etConfirmPassword.getText().toString().trim() : "";

        if (fullName.isEmpty()) { if (etFullName != null) etFullName.setError("Required"); return; }
        if (username.isEmpty()) { if (etUsername != null) etUsername.setError("Required"); return; }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (etEmail != null) etEmail.setError("Valid email required"); return;
        }
        if (password.length() < 6) { if (etPassword != null) etPassword.setError("Min 6 characters"); return; }
        if (!password.equals(confirm)) { if (etConfirmPassword != null) etConfirmPassword.setError("Passwords don't match"); return; }
        if (cbTerms != null && !cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms & Conditions", Toast.LENGTH_SHORT).show(); return;
        }

        Toast.makeText(this, "Welcome, " + fullName + "! 🌿", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DiscoverActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}