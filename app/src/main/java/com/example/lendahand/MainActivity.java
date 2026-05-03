package com.example.lendahand;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipientAdapter adapter;
    private List<Recipient> recipientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Logic: Switch to the matching screen layout
        setContentView(R.layout.activity_allocation);

        recyclerView = findViewById(R.id.rvRecipients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Logic: Create some dummy data to test the UI
        recipientList = new ArrayList<>();
        recipientList.add(new Recipient(1, "John Doe", "I need school supplies for my kids.", 10));
        recipientList.add(new Recipient(2, "Jane Smith", "Looking for warm blankets for winter.", 5));
        recipientList.add(new Recipient(3, "Local Shelter", "We need canned food for 20 people.", 50));

        adapter = new RecipientAdapter(recipientList);
        recyclerView.setAdapter(adapter);
    }
}