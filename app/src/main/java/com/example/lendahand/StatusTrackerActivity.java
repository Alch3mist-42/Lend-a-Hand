package com.example.lendahand;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class StatusTrackerActivity extends BaseActivity {

    private static final String BASE_URL = "https://wmc.ms.wits.ac.za/students/sgroup2713/";
    private static final String STATUS_URL = BASE_URL + "status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_tracker);

        TextView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        loadStatus();
    }

    private void loadStatus() {
        StringRequest request = new StringRequest(Request.Method.GET, STATUS_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        // Donations
                        JSONArray donations = json.optJSONArray("donations");
                        if (donations != null && donations.length() > 0) {
                            JSONObject d   = donations.getJSONObject(0);
                            String status  = d.optString("status", "sent");
                            String date    = d.optString("collection_date", "TBC");
                            Toast.makeText(this,
                                    "Donation status: " + status + " | " + date,
                                    Toast.LENGTH_LONG).show();
                        }

                        // Requests
                        JSONArray requests = json.optJSONArray("requests");
                        if (requests != null && requests.length() > 0) {
                            JSONObject r  = requests.getJSONObject(0);
                            String status = r.optString("status", "pending");
                            Toast.makeText(this,
                                    "Request status: " + status,
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        // Keep demo data
                    }
                },
                error -> { /* Keep demo data */ }
        );
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }
}