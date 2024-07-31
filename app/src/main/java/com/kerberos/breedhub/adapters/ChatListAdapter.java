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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.ChatListModel;
import com.kerberos.breedhub.models.ChatsModel;
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<ChatListModel> mData;
    private List<ChatListModel> filteredList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    public ChatListAdapter(Context context, List<ChatListModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.user_chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatListModel chatListItem = filteredList.get(position);

        String partnerId = chatListItem.getId().replace(FirebaseAuth.getInstance().getCurrentUser().getUid(),"");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(partnerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = snapshot.getValue(UserData.class);
                chatListItem.setName((userData.getFirstName() != null && !userData.getFirstName().isEmpty()) ? userData.getFirstName() : userData.getLastName());
                chatListItem.setImage(userData.getPhoto());

                try{
                    Glide.with(mContext)
                            .load(chatListItem.getImage())
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(holder.image);
                }catch (Exception ignored){}

                holder.name.setText(chatListItem.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("chats").child(chatListItem.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatsModel chats = new ChatsModel();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    chats = dataSnapshot.getValue(ChatsModel.class);
                }
                holder.lastMessage.setText(chats.getMessage());
                holder.time.setText(chats.getTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView name, time, lastMessage;
        ShapeableImageView image ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            lastMessage = itemView.findViewById(R.id.lastMessage);
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
            for (ChatListModel item : mData) {
                if (item.getName().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
}
