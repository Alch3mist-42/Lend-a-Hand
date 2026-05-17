package com.example.lendahand;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RecipientRequestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_request);

        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null)
            btnBack.setOnClickListener(v -> finish());

        MaterialButton btnSubmit = findViewById(R.id.btnSubmitRequest);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                TextInputEditText etItem = findViewById(R.id.etItemNeeded);
                TextInputEditText etQty  = findViewById(R.id.etQuantity);
                TextInputEditText etNote = findViewById(R.id.etNote);

                String item = etItem != null && etItem.getText() != null ?
                        etItem.getText().toString().trim() : "";
                String qty  = etQty  != null && etQty.getText()  != null ?
                        etQty.getText().toString().trim()  : "";

                if (item.isEmpty()) {
                    if (etItem != null) etItem.setError("Please specify what you need");
                    return;
                }
                if (qty.isEmpty()) {
                    if (etQty != null) etQty.setError("Please enter a quantity");
                    return;
                }

                // Demo: show success and go back
                Toast.makeText(this,
                        "Request posted! Donors will be notified.",
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}