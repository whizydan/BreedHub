package com.kerberos.breedhub.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.PetModel;

import java.util.ArrayList;
import java.util.List;

public class MyPetsAdapter extends RecyclerView.Adapter<MyPetsAdapter.ViewHolder> {
    private List<PetModel> mData;
    private List<PetModel> filteredList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    public MyPetsAdapter(Context context, List<PetModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_pet_list_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetModel petModel = filteredList.get(position);
        if(petModel.getName().equals("Add")){
            holder.petImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.add));
        }else{
            try{
                Glide.with(mContext)
                        .load(petModel.getPhoto().get(petModel.getPhoto().size() - 1))
                        .placeholder(R.drawable.cats3)
                        .error(R.drawable.logo)
                        .into(holder.petImage);
            }catch (Exception ignored){}
        }

        holder.petName.setText(petModel.getName());
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView petName;
        ShapeableImageView petImage ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.petImage);
            petName = itemView.findViewById(R.id.petName);
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
            for (PetModel item : mData) {
                if (item.getName().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
}
