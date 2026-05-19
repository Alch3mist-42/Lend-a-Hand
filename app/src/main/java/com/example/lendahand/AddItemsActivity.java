package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class AddItemsActivity extends AppCompatActivity {

    private static final String DONATE_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/donate.php";

    private static final String[] RESOURCE_IDS = {
            "FN_NONPERISH",
            "FN_CANNED",
            "FN_FORMULA",
            "HYG_SOAP",
            "HYG_SANITARY",
            "HH_BLANKETS",
            "CL_WINTER",
            "SCH_STATIONERY",
            "SCH_BACKPACKS",
            "MED_CHRONIC",
            "MED_INSULIN"
    };

    private static final int[] QTY_VIEW_IDS = {
            R.id.tvQty_nonPerish,
            R.id.tvQty_canned,
            R.id.tvQty_babyFormula,
            R.id.tvQty_soap,
            R.id.tvQty_sanitary,
            R.id.tvQty_blankets,
            R.id.tvQty_winter,
            R.id.tvQty_stationery,
            R.id.tvQty_backpacks,
            R.id.tvQty_medication,
            R.id.tvQty_insulin
    };

    private TextView activeChip;
    private ScrollView scrollView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        sessionManager = new SessionManager(this);
        scrollView = findViewById(R.id.scrollContent);

        // Wire steppers
        wireSteppers(R.id.btnMinus_nonPerish,   R.id.btnPlus_nonPerish,   R.id.tvQty_nonPerish);
        wireSteppers(R.id.btnMinus_canned,      R.id.btnPlus_canned,      R.id.tvQty_canned);
        wireSteppers(R.id.btnMinus_babyFormula, R.id.btnPlus_babyFormula, R.id.tvQty_babyFormula);
        wireSteppers(R.id.btnMinus_soap,        R.id.btnPlus_soap,        R.id.tvQty_soap);
        wireSteppers(R.id.btnMinus_sanitary,    R.id.btnPlus_sanitary,    R.id.tvQty_sanitary);
        wireSteppers(R.id.btnMinus_blankets,    R.id.btnPlus_blankets,    R.id.tvQty_blankets);
        wireSteppers(R.id.btnMinus_winter,      R.id.btnPlus_winter,      R.id.tvQty_winter);
        wireSteppers(R.id.btnMinus_stationery,  R.id.btnPlus_stationery,  R.id.tvQty_stationery);
        wireSteppers(R.id.btnMinus_backpacks,   R.id.btnPlus_backpacks,   R.id.tvQty_backpacks);
        wireSteppers(R.id.btnMinus_medication,  R.id.btnPlus_medication,  R.id.tvQty_medication);
        wireSteppers(R.id.btnMinus_insulin,     R.id.btnPlus_insulin,     R.id.tvQty_insulin);

        // Donate button (was Save)
        View btnSave = findViewById(R.id.btnSave);
        if (btnSave != null)
            btnSave.setOnClickListener(v -> submitDonations());

        // Chips
        activeChip = findViewById(R.id.chipAll);
        setChipActive(activeChip);

        TextView chipAll = findViewById(R.id.chipAll);
        if (chipAll != null) {
            chipAll.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipAll;
                setChipActive(chipAll);
                if (scrollView != null) scrollView.smoothScrollTo(0, 0);
            });
        }

        wireChip(R.id.chipFood,     R.id.sectionFood);
        wireChip(R.id.chipHygiene,  R.id.sectionHygiene);
        wireChip(R.id.chipClothing, R.id.sectionClothing);
        wireChip(R.id.chipSchool,   R.id.sectionSchool);
        wireChip(R.id.chipMedical,  R.id.sectionMedical);

        // Card animations
        int[] cards = {
                R.id.cardNonPerishables, R.id.cardCannedFood,
                R.id.cardBabyFormula,    R.id.cardSoap,
                R.id.cardSanitary,       R.id.cardBlankets,
                R.id.cardWinterClothes,  R.id.cardStationery,
                R.id.cardBackpacks,      R.id.cardMedication,
                R.id.cardInsulin
        };
        for (int cid : cards) addReboundCard(cid);

        // Bottom nav
        setupNav(R.id.navDiscover, DiscoverActivity.class);
        setupNav(R.id.navDonate,   null);
        setupNav(R.id.navActivity, LeaderboardActivity.class);
        setupNav(R.id.navProfile,  ProfileActivity.class);
    }

    private void submitDonations() {
        final Map<String, Integer> itemsToSubmit = new HashMap<>();
        for (int i = 0; i < QTY_VIEW_IDS.length; i++) {
            TextView qtyView = findViewById(QTY_VIEW_IDS[i]);
            if (qtyView == null) continue;
            int qty = Integer.parseInt(qtyView.getText().toString());
            if (qty > 0) itemsToSubmit.put(RESOURCE_IDS[i], qty);
        }

        if (itemsToSubmit.isEmpty()) {
            Toast.makeText(this, "Please select at least one item",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        View btnSave = findViewById(R.id.btnSave);
        if (btnSave != null) btnSave.setEnabled(false);

        final int[] submitted = {0};
        final int[] failed    = {0};
        final int   total     = itemsToSubmit.size();

        for (Map.Entry<String, Integer> entry : itemsToSubmit.entrySet()) {
            final String resId = entry.getKey();
            final int    qty   = entry.getValue();

            StringRequest request = new StringRequest(Request.Method.POST, DONATE_URL,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.has("success")) submitted[0]++;
                            else failed[0]++;
                        } catch (Exception e) {
                            failed[0]++;
                        }

                        if (submitted[0] + failed[0] == total) {
                            if (btnSave != null) btnSave.setEnabled(true);
                            if (failed[0] == 0) {
                                // Updated toast message
                                Toast.makeText(this,
                                        "Donation received ✓",
                                        Toast.LENGTH_LONG).show();
                                // Navigate to status tracker instead of allocation
                                startActivity(new Intent(this, DonationConfirmedActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this,
                                        submitted[0] + " item(s) submitted, "
                                                + failed[0] + " failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    error -> {
                        failed[0]++;
                        if (submitted[0] + failed[0] == total) {
                            if (btnSave != null) btnSave.setEnabled(true);
                            Toast.makeText(this, "Cannot connect to server",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id",         sessionManager.getUserId());
                    params.put("token",           sessionManager.getToken());
                    params.put("resource_id",     resId);
                    params.put("quantity",        String.valueOf(qty));
                    params.put("photo_url",       "");
                    params.put("location",        "");
                    params.put("notes",           "");
                    params.put("collection_date", "");
                    return params;
                }
            };

            VolleySingleton.getInstance(this).getRequestQueue().add(request);
        }
    }

    private void wireChip(int chipId, int sectionId) {
        TextView chip = findViewById(chipId);
        if (chip == null) return;
        chip.setOnClickListener(v -> {
            setChipInactive(activeChip);
            activeChip = chip;
            setChipActive(chip);
            scrollToSection(sectionId);
        });
    }

    private void scrollToSection(int sectionId) {
        View section = findViewById(sectionId);
        if (section != null && scrollView != null) {
            section.postDelayed(() -> {
                int yOffset = section.getTop() - 100;
                scrollView.smoothScrollTo(0, yOffset);
            }, 50);
        }
    }

    private void wireSteppers(int minusId, int plusId, int qtyId) {
        Button minus = findViewById(minusId);
        Button plus  = findViewById(plusId);
        TextView qty = findViewById(qtyId);
        if (minus == null || plus == null || qty == null) return;

        minus.setOnClickListener(v -> {
            int val = Integer.parseInt(qty.getText().toString());
            if (val > 0) qty.setText(String.valueOf(val - 1));
        });

        plus.setOnClickListener(v -> {
            int val = Integer.parseInt(qty.getText().toString());
            if (val < 7) {
                qty.setText(String.valueOf(val + 1));
            } else {
                Toast.makeText(this, "Maximum 7 items per donation",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setChipActive(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.chip_white_active);
        chip.setTextColor(getColor(R.color.primary));
    }

    private void setChipInactive(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.chip_ghost);
        chip.setTextColor(0xCCFFFFFF);
    }

    private void addReboundCard(int cardId) {
        CardView card = findViewById(cardId);
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(220)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                    break;
            }
            return false;
        });
    }

    private void setupNav(int navId, Class<?> target) {
        LinearLayout nav = findViewById(navId);
        if (nav == null) return;
        nav.setOnClickListener(v -> {
            if (target != null) startActivity(new Intent(this, target));
        });
    }
}