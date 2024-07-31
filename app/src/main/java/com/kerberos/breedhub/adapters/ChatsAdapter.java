package com.kerberos.breedhub.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.ChatListModel;
import com.kerberos.breedhub.models.ChatsModel;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private List<ChatsModel> mData;
    private List<ChatsModel> filteredList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    public ChatsAdapter(Context context, List<ChatsModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatsModel userModel = filteredList.get(position);

        if(userModel.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.cardRight.setVisibility(View.VISIBLE);
            holder.messageRight.setText(userModel.getMessage());
            holder.timeRight.setText(userModel.getTime());
        }else{
            holder.cardLeft.setVisibility(View.VISIBLE);
            holder.messageLeft.setText(userModel.getMessage());
            holder.timeLeft.setText(userModel.getTime());
        }
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView messageLeft, messageRight, timeRight, timeLeft;
        MaterialCardView cardRight, cardLeft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRight = itemView.findViewById(R.id.cardRight);
            cardLeft = itemView.findViewById(R.id.cardLeft);
            messageLeft = itemView.findViewById(R.id.messageLeft);
            messageRight = itemView.findViewById(R.id.messageRight);
            timeLeft = itemView.findViewById(R.id.timeLeft);
            timeRight = itemView.findViewById(R.id.timeRight);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // Interface for click listener callback
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Method to set click listener for items in the RecyclerView
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Method to filter the data based on a search query
    public void filter(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(mData); // If query is empty, show all data
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (ChatsModel item : mData) {
                if (item.getMessage().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
}
