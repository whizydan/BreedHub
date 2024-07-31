package com.kerberos.breedhub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.CatInfoActivity;
import com.kerberos.breedhub.adapters.PetsAdapter;
import com.kerberos.breedhub.models.PetModel;
import com.kerberos.breedhub.views.AddPet;
import com.kerberos.breedhub.views.NotificationDialog;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetFragment extends Fragment implements  PetsAdapter.ItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<PetModel> pets = new ArrayList<>();

    public PetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetFragment newInstance(String param1, String param2) {
        PetFragment fragment = new PetFragment();
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
        return inflater.inflate(R.layout.fragment_pet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExtendedFloatingActionButton addPet = view.findViewById(R.id.newPet);
        RecyclerView petList = view.findViewById(R.id.petList);

        FirebaseDatabase.getInstance().getReference("cats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pets.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    PetModel petModel = dataSnapshot.getValue(PetModel.class);
                    if(!petModel.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        pets.add(petModel);
                    }
                }
                petList.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false));
                petList.setAdapter(new PetsAdapter(requireActivity(),pets));
                PetsAdapter adapter = (PetsAdapter) petList.getAdapter();
                adapter.setClickListener(PetFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new NotificationDialog(error.getDetails()).show(getChildFragmentManager(),"PetFragment");
            }
        });

        addPet.setOnClickListener(v -> {
            AddPet addPetFragment = new AddPet();
            addPetFragment.show(getChildFragmentManager(),"PetFragment");
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(requireActivity(), CatInfoActivity.class);
        intent.putExtra("id", pets.get(position).getId());
        startActivity(intent);
    }
}