package com.example.lendahand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.ViewHolder> {

    private List<Recipient> recipientList;

    public RecipientAdapter(List<Recipient> recipientList) {
        this.recipientList = recipientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipient recipient = recipientList.get(position);
        holder.tvName.setText(recipient.getName());
        holder.tvBio.setText(recipient.getBio());
        holder.etAmount.setHint("Needs: " + recipient.getAmountNeeded());

        holder.btnAllocate.setOnClickListener(v -> {
            String amountStr = holder.etAmount.getText().toString();
            if (!amountStr.isEmpty()) {
                int amount = Integer.parseInt(amountStr);
                if (amount <= recipient.getAmountNeeded()) {
                    Toast.makeText(v.getContext(), "Allocated " + amount + " to " + recipient.getName(), Toast.LENGTH_SHORT).show();
                    // Logic: Later, Member 2 will add the PHP call here
                } else {
                    Toast.makeText(v.getContext(), "Amount exceeds need!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBio;
        EditText etAmount;
        Button btnAllocate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipientName);
            tvBio = itemView.findViewById(R.id.tvMotivation);
            etAmount = itemView.findViewById(R.id.etAllocateAmount);
            btnAllocate = itemView.findViewById(R.id.btnAllocate);
        }
    }
}