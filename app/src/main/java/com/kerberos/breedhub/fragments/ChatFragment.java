package com.kerberos.breedhub.fragments;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.ChatActivity;
import com.kerberos.breedhub.adapters.ChatListAdapter;
import com.kerberos.breedhub.adapters.ChatsAdapter;
import com.kerberos.breedhub.models.ChatListModel;
import com.kerberos.breedhub.models.ChatsModel;
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.models.UserModel;
import com.kerberos.breedhub.views.NotificationDialog;
import com.kerberos.breedhub.views.UsersListFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements ChatListAdapter.ItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<ChatListModel> chats = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView chatList = view.findViewById(R.id.chatList);
        ExtendedFloatingActionButton newChat = view.findViewById(R.id.newChat);
        final ChatListAdapter[] adapter = new ChatListAdapter[1];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                chats.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.getKey().contains(myUid)){

                        chats.add(new ChatListModel(dataSnapshot.getKey(), "","","",""));
                    }

                }
                adapter[0] = new ChatListAdapter(requireActivity(), chats);
                chatList.setAdapter(adapter[0]);
                adapter[0].setClickListener(ChatFragment.this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        newChat.setOnClickListener(v -> {
            new UsersListFragment().show(getChildFragmentManager(),"ChatFragment");
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        ChatListModel chatListModel = chats.get(position);

        Intent intent = new Intent(requireActivity(), ChatActivity.class);
        intent.putExtra("id",chatListModel.getId());
        startActivity(intent);
    }
}