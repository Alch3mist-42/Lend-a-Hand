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

// This screen lets the user pick items to donate and send them to the server
public class AddItemsActivity extends AppCompatActivity {

    // The server URL we send donation data to
    private static final String DONATE_URL =
            "https://wmc.ms.wits.ac.za/students/sgroup2713/donate.php";

    // These are the item codes that match the database
    // Each one corresponds to a type of donation item
    private static final String[] RESOURCE_IDS = {
            "FN_NONPERISH",   // Non-perishable food
            "FN_CANNED",      // Canned food
            "FN_FORMULA",     // Baby formula
            "HYG_SOAP",       // Soap
            "HYG_SANITARY",   // Sanitary products
            "HH_BLANKETS",    // Blankets
            "CL_WINTER",      // Winter clothes
            "SCH_STATIONERY", // Stationery
            "SCH_BACKPACKS",  // Backpacks
            "MED_CHRONIC",    // Chronic medication
            "MED_INSULIN"     // Insulin coolers
    };

    // These are the IDs of the quantity text views on the screen
    // They match the order of RESOURCE_IDS above
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

    // Keeps track of which category chip is currently selected
    private TextView activeChip;

    // The scroll view so we can scroll to different sections
    private ScrollView scrollView;

    // Helps us get the logged-in user's ID and token
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        // Set up session manager to get user info
        sessionManager = new SessionManager(this);

        // Get the scroll view from the layout
        scrollView = findViewById(R.id.scrollContent);

        // Connect the + and - buttons to each item's quantity display
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

        // When user taps the Donate button, submit their selected items
        View btnSave = findViewById(R.id.btnSave);
        if (btnSave != null)
            btnSave.setOnClickListener(v -> submitDonations());

        // Set the "All" chip as active by default when screen opens
        activeChip = findViewById(R.id.chipAll);
        setChipActive(activeChip);

        // When user taps "All", scroll back to the top
        TextView chipAll = findViewById(R.id.chipAll);
        if (chipAll != null) {
            chipAll.setOnClickListener(v -> {
                setChipInactive(activeChip);
                activeChip = chipAll;
                setChipActive(chipAll);
                if (scrollView != null) scrollView.smoothScrollTo(0, 0);
            });
        }

        // Connect each category chip to its section on the screen
        wireChip(R.id.chipFood,     R.id.sectionFood);
        wireChip(R.id.chipHygiene,  R.id.sectionHygiene);
        wireChip(R.id.chipClothing, R.id.sectionClothing);
        wireChip(R.id.chipSchool,   R.id.sectionSchool);
        wireChip(R.id.chipMedical,  R.id.sectionMedical);

        // Add a press animation to each item card
        int[] cards = {
                R.id.cardNonPerishables, R.id.cardCannedFood,
                R.id.cardBabyFormula,    R.id.cardSoap,
                R.id.cardSanitary,       R.id.cardBlankets,
                R.id.cardWinterClothes,  R.id.cardStationery,
                R.id.cardBackpacks,      R.id.cardMedication,
                R.id.cardInsulin
        };
        for (int cid : cards) addReboundCard(cid);

        // Set up the bottom navigation bar buttons
        setupNav(R.id.navDiscover, DiscoverActivity.class);
        setupNav(R.id.navDonate,   null); // already on donate screen
        setupNav(R.id.navActivity, LeaderboardActivity.class);
        setupNav(R.id.navProfile,  ProfileActivity.class);
    }

    // Gathers all items with quantity > 0 and sends each one to the server
    private void submitDonations() {

        // Build a map of items the user wants to donate
        final Map<String, Integer> itemsToSubmit = new HashMap<>();
        for (int i = 0; i < QTY_VIEW_IDS.length; i++) {
            TextView qtyView = findViewById(QTY_VIEW_IDS[i]);
            if (qtyView == null) continue;
            int qty = Integer.parseInt(qtyView.getText().toString());
            if (qty > 0) itemsToSubmit.put(RESOURCE_IDS[i], qty); // only add items with quantity
        }

        // Don't submit if the user hasn't selected anything
        if (itemsToSubmit.isEmpty()) {
            Toast.makeText(this, "Please select at least one item",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable the button while we wait for the server response
        View btnSave = findViewById(R.id.btnSave);
        if (btnSave != null) btnSave.setEnabled(false);

        // Track how many items succeeded or failed
        final int[] submitted = {0};
        final int[] failed    = {0};
        final int   total     = itemsToSubmit.size();

        // Send a separate request for each item
        for (Map.Entry<String, Integer> entry : itemsToSubmit.entrySet()) {
            final String resId = entry.getKey();   // item code e.g. "FN_CANNED"
            final int    qty   = entry.getValue(); // how many the user wants to donate

            // Create a POST request to the donate endpoint
            StringRequest request = new StringRequest(Request.Method.POST, DONATE_URL,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            // Check if server says success
                            if (json.has("success")) submitted[0]++;
                            else failed[0]++;
                        } catch (Exception e) {
                            failed[0]++; // couldn't parse response
                        }

                        // Check if all requests have finished
                        if (submitted[0] + failed[0] == total) {
                            if (btnSave != null) btnSave.setEnabled(true);
                            if (failed[0] == 0) {
                                // All items donated successfully
                                Toast.makeText(this,
                                        "Donation received ✓",
                                        Toast.LENGTH_LONG).show();
                                // Go to confirmation screen
                                startActivity(new Intent(this, DonationConfirmedActivity.class));
                                finish();
                            } else {
                                // Some items failed to submit
                                Toast.makeText(this,
                                        submitted[0] + " item(s) submitted, "
                                                + failed[0] + " failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    error -> {
                        // Network error - count as failed
                        failed[0]++;
                        if (submitted[0] + failed[0] == total) {
                            if (btnSave != null) btnSave.setEnabled(true);
                            Toast.makeText(this, "Cannot connect to server",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                // These are the values we send to the server
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

            // Add the request to the queue to be sent
            VolleySingleton.getInstance(this).getRequestQueue().add(request);
        }
    }

    // When user taps a category chip, scroll to that section
    private void wireChip(int chipId, int sectionId) {
        TextView chip = findViewById(chipId);
        if (chip == null) return;
        chip.setOnClickListener(v -> {
            setChipInactive(activeChip); // deactivate old chip
            activeChip = chip;
            setChipActive(chip);         // activate new chip
            scrollToSection(sectionId); // scroll to the right part of the page
        });
    }

    // Smoothly scrolls the page to the given section
    private void scrollToSection(int sectionId) {
        View section = findViewById(sectionId);
        if (section != null && scrollView != null) {
            section.postDelayed(() -> {
                int yOffset = section.getTop() - 100; // small offset so header doesn't cover it
                scrollView.smoothScrollTo(0, yOffset);
            }, 50);
        }
    }

    // Connects the minus and plus buttons to the quantity text view for one item
    private void wireSteppers(int minusId, int plusId, int qtyId) {
        Button minus = findViewById(minusId);
        Button plus  = findViewById(plusId);
        TextView qty = findViewById(qtyId);
        if (minus == null || plus == null || qty == null) return;

        // Decrease quantity by 1 but not below 0
        minus.setOnClickListener(v -> {
            int val = Integer.parseInt(qty.getText().toString());
            if (val > 0) qty.setText(String.valueOf(val - 1));
        });

        // Increase quantity by 1 but not above 7
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

    // Makes a chip look selected (white background, green text)
    private void setChipActive(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.chip_white_active);
        chip.setTextColor(getColor(R.color.primary));
    }

    // Makes a chip look unselected (transparent background, white text)
    private void setChipInactive(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.chip_ghost);
        chip.setTextColor(0xCCFFFFFF);
    }

    // Adds a small press animation when user touches a card
    private void addReboundCard(int cardId) {
        CardView card = findViewById(cardId);
        if (card == null) return;
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Shrink slightly when pressed
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Spring back to normal size
                    v.animate().scaleX(1f).scaleY(1f).setDuration(220)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                    break;
            }
            return false;
        });
    }

    // Sets up a bottom nav button to open a different screen when tapped
    private void setupNav(int navId, Class<?> target) {
        LinearLayout nav = findViewById(navId);
        if (nav == null) return;
        nav.setOnClickListener(v -> {
            if (target != null) startActivity(new Intent(this, target));
        });
    }
}