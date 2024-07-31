package com.kerberos.breedhub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.EditInformationActivity;
import com.kerberos.breedhub.adapters.PetImagesAdapter;
import com.kerberos.breedhub.models.PetModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PetProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PetProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PetProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetProfileFragment newInstance(String param1, String param2) {
        PetProfileFragment fragment = new PetProfileFragment();
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
        return inflater.inflate(R.layout.fragment_pet_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ShapeableImageView profileImage = view.findViewById(R.id.profileImage);
        MaterialTextView name = view.findViewById(R.id.name);
        MaterialTextView age = view.findViewById(R.id.age);
        RecyclerView images = view.findViewById(R.id.images);
        MaterialButton edit = view.findViewById(R.id.editCat);
        MaterialButton delete = view.findViewById(R.id.deleteCat);

        String catId = mParam1;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditInformationActivity.class);
            intent.putExtra("id", catId);
            startActivityForResult( intent, 100);
        });

        delete.setOnClickListener(v -> {
            reference.child("cats").child(catId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder, new MyPetsFragment()).commit();
                }
            });
        });

        reference.child("cats").child(catId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PetModel petModel = snapshot.getValue(PetModel.class);

                age.setText(petModel.getAge());
                name.setText(petModel.getName());
                try{
                    Glide.with(requireActivity())
                            .load(petModel.getPhoto().get(0))
                            .placeholder(R.drawable.cats3)
                            .error(R.drawable.logo)
                            .into(profileImage);
                }catch (Exception ignored){}

                PetImagesAdapter adapter = new PetImagesAdapter(requireActivity(), petModel.getPhoto());
                images.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}