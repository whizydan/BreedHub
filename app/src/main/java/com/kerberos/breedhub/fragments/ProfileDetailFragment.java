package com.kerberos.breedhub.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.ChangePasswordActivity;
import com.kerberos.breedhub.activities.EditProfileActivity;
import com.kerberos.breedhub.models.UserData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileDetailFragment newInstance(String param1, String param2) {
        ProfileDetailFragment fragment = new ProfileDetailFragment();
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
        return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialTextView name = view.findViewById(R.id.name);
        ShapeableImageView profileImage = view.findViewById(R.id.profileImage);
        MaterialTextView date = view.findViewById(R.id.date);
        MaterialTextView phone = view.findViewById(R.id.phone);
        LinearLayout edit = view.findViewById(R.id.edit);
        LinearLayout editPass = view.findViewById(R.id.editPass);
        MaterialTextView email = view.findViewById(R.id.email);

        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserData userData = snapshot.getValue(UserData.class);
                                date.setText(userData.getCreatedOn());
                                phone.setText(userData.getPhone());
                                name.setText(userData.getFirstName());
                                email.setText(userData.getEmail());

                                try{
                                    Glide.with(requireActivity())
                                            .load(userData.getPhoto())
                                            .placeholder(R.drawable.logo)
                                            .error(R.drawable.logo)
                                            .into(profileImage);
                                }catch (Exception ignored){}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            startActivityForResult(intent,400);
        });
        editPass.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ChangePasswordActivity.class);
            startActivityForResult(intent,300);
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400 && resultCode == RESULT_OK) {
            if (data != null) {
                String resultData = data.getStringExtra("status");
                if(resultData.equals("0")){
                    Toast.makeText(requireActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
                }
            }
        }else if (requestCode == 300 && resultCode == RESULT_OK) {
            if (data != null) {
                String resultData = data.getStringExtra("status");
                if(resultData.equals("0")){
                    Toast.makeText(requireActivity(), "Password changed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}