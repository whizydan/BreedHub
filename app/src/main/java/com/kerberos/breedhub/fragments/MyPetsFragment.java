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

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.PetInformationActivity;
import com.kerberos.breedhub.adapters.PetsAdapter;
import com.kerberos.breedhub.models.PetModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPetsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPetsFragment extends Fragment implements PetsAdapter.ItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<PetModel> petModels = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPetsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPetsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPetsFragment newInstance(String param1, String param2) {
        MyPetsFragment fragment = new MyPetsFragment();
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
        return inflater.inflate(R.layout.fragment_my_pets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton add = view.findViewById(R.id.add);
        RecyclerView myPets = view.findViewById(R.id.myPets);
        final PetsAdapter[] adapter = new PetsAdapter[1];

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("cats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petModels.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    PetModel petModel = dataSnapshot.getValue(PetModel.class);

                    petModels.add(petModel);
                }

                adapter[0] = new PetsAdapter(requireActivity(), petModels);
                myPets.setAdapter(adapter[0]);
                adapter[0].setClickListener(MyPetsFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), PetInformationActivity.class));
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        PetModel petModel = petModels.get(position);

        PetProfileFragment petProfileFragment = PetProfileFragment.newInstance(petModel.getId(), "");
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder, petProfileFragment).commit();
    }
}