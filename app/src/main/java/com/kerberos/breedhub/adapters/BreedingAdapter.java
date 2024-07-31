package com.kerberos.breedhub.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.BreedingModel;
import com.kerberos.breedhub.models.PetModel;
import com.kerberos.breedhub.models.RequestModel;
import com.kerberos.breedhub.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class BreedingAdapter extends RecyclerView.Adapter<BreedingAdapter.ViewHolder> {
    private List<RequestModel> mData;
    private List<RequestModel> filteredList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    public BreedingAdapter(Context context, List<RequestModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.breeding_list_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModel breedingModel = filteredList.get(position);

        holder.user1Name.setText(breedingModel.getUser1().getFirstName() + "'s " + breedingModel.getCat1().getName());
        holder.user2Name.setText(breedingModel.getUser2().getFirstName() + "'s " + breedingModel.getCat2().getName());
        holder.status.setText(breedingModel.getStatus());

        try{
            Glide.with(mContext)
                    .load(breedingModel.getCat1().getPhoto().get(0))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.user1Pet);
        }catch (Exception ignored){}
        try{
            Glide.with(mContext)
                    .load(breedingModel.getCat2().getPhoto().get(0))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.user2Pet);
        }catch (Exception ignored){}

        try{
            Glide.with(mContext)
                    .load(breedingModel.getUser1().getPhoto())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.user1Profile);
        }catch (Exception ignored){}
        try{
            Glide.with(mContext)
                    .load(breedingModel.getUser2().getPhoto())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.user2Profile);
        }catch (Exception ignored){}
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView user1Name, user2Name, status;
        ImageView user1Pet, user2Pet, user1Profile, user2Profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.textView4);
            user1Pet = itemView.findViewById(R.id.user1);
            user2Pet = itemView.findViewById(R.id.user2);
            user1Name = itemView.findViewById(R.id.materialTextView2);
            user2Name = itemView.findViewById(R.id.materialTextView);
            user1Profile = itemView.findViewById(R.id.user1Profile);
            user2Profile = itemView.findViewById(R.id.user2profile);
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
            for (RequestModel item : mData) {
                if (item.getRejectionReason().equalsIgnoreCase(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
}
