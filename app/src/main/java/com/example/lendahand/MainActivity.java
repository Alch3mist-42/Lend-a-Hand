package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
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

public class MainActivity extends AppCompatActivity {

    private static final String LOGIN_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/login.php";

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);

        if (btnLogin != null) {
            btnLogin.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                                .setInterpolator(new OvershootInterpolator(2.5f))
                                .withEndAction(this::attemptLogin)
                                .start();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                        break;
                }
                return true;
            });
        }

        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);
        if (tvGoToRegister != null)
            tvGoToRegister.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        username = etUsername != null && etUsername.getText() != null
                ? etUsername.getText().toString().trim() : "";
        password = etPassword != null && etPassword.getText() != null
                ? etPassword.getText().toString().trim() : "";

        if (username.isEmpty()) { etUsername.setError("Required"); return; }
        if (password.isEmpty()) { etPassword.setError("Required"); return; }

        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Sign In");
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("success")) {
                            SessionManager session = new SessionManager(this);
                            session.saveSession(
                                    json.optString("user_id", username),
                                    username,
                                    json.optString("role", "user"),
                                    json.optString("token", "")
                            );
                            String role = json.optString("role", "user");
                            Intent intent;
                            if (role.equals("stuff")) {
                                intent = new Intent(this, StaffDonationsActivity.class);
                            } else {
                                intent = new Intent(this, DiscoverActivity.class);
                                intent.putExtra("username", username);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this,
                                    json.optString("error", "Login failed"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Unexpected response",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Sign In");
                    Toast.makeText(this,
                            "Cannot connect to server.",
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",  username);
                params.put("login",    username);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }
}