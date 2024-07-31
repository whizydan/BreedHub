package com.kerberos.breedhub.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.ChatActivity;
import com.kerberos.breedhub.adapters.UsersAdapter;
import com.kerberos.breedhub.models.UserModel;

import java.util.ArrayList;

public class UsersListFragment extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.users_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView usersList = view.findViewById(R.id.usersList);
        ArrayList<UserModel> users = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            users.add(userModel);
                        }
                        UsersAdapter adapter = new UsersAdapter(requireActivity(),users);
                        usersList.setAdapter(adapter);
                        usersList.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false));


                        adapter.setClickListener(new UsersAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(requireActivity(), ChatActivity.class);
                                intent.putExtra("id",users.get(position).getId());
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new NotificationDialog(error.getDetails()).show(getChildFragmentManager(),"UserList");
                    }
                })
                ;
    }
}
