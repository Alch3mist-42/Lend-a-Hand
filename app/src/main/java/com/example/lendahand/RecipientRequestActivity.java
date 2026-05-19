package com.example.lendahand;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RecipientRequestActivity extends BaseActivity {

    private static final String REQUEST_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/request.php";

    private TextInputEditText etItem, etQuantity, etNote;
    private MaterialButton btnSubmit;
    private SessionManager sessionManager;
    private String item = "", quantity = "", note = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_request);

        sessionManager = new SessionManager(this);

        etItem     = findViewById(R.id.etItemNeeded);
        etQuantity = findViewById(R.id.etQuantity);
        etNote     = findViewById(R.id.etNote);
        btnSubmit  = findViewById(R.id.btnSubmitRequest);

        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnSubmit != null)
            btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void submitRequest() {
        item     = etItem     != null && etItem.getText()     != null ? etItem.getText().toString().trim()     : "";
        quantity = etQuantity != null && etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        note     = etNote     != null && etNote.getText()     != null ? etNote.getText().toString().trim()     : "";

        if (item.isEmpty()) {
            if (etItem != null) etItem.setError("Required");
            return;
        }
        if (quantity.isEmpty()) {
            if (etQuantity != null) etQuantity.setError("Required");
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Posting...");

        StringRequest request = new StringRequest(Request.Method.POST, REQUEST_URL,
                response -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Post My Request");
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("success")) {
                            Toast.makeText(this,
                                    "Request posted successfully! ✓",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this,
                                    json.optString("error", "Failed to post request"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Unexpected response",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Post My Request");
                    Toast.makeText(this,
                            "Cannot connect to server", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",          sessionManager.getUserId());
                params.put("token",            sessionManager.getToken());
                params.put("resource_id",      item);
                params.put("quantity",         quantity);
                params.put("delivery_location", note);
                params.put("delivery_date",    "");
                return params;
            }
        };

        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }
}