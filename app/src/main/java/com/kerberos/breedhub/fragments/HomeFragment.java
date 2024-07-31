package com.kerberos.breedhub.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.CatInfoActivity;
import com.kerberos.breedhub.activities.PetInformationActivity;
import com.kerberos.breedhub.adapters.MyPetsAdapter;
import com.kerberos.breedhub.adapters.ServicesAdapter;
import com.kerberos.breedhub.models.PetModel;
import com.kerberos.breedhub.models.ServicesModel;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.toolkit.Utilities;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements MyPetsAdapter.ItemClickListener {
    private ServicesAdapter.ItemClickListener itemClickListener = new ServicesAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            onServiceItemClick(view, position);
        }
    };

    ArrayList<PetModel> cats = new ArrayList<>();
    ArrayList<ServicesModel> servicesModels = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TinyDB tinyDB = new TinyDB(requireActivity());

        MaterialTextView userName = view.findViewById(R.id.userName);
        RecyclerView myPets = view.findViewById(R.id.myPets);
        RecyclerView services = view.findViewById(R.id.services);

        userName.setText(tinyDB.getString("fname"));
        servicesModels = Utilities.getServices(getParentFragmentManager());
        ServicesAdapter adapter = new ServicesAdapter(requireActivity(), servicesModels);
        services.setAdapter(adapter);
        adapter.setClickListener(itemClickListener);

        PetModel addButton = new PetModel();
        addButton.setName("Add");
        addButton.setAdd(R.drawable.add_pet);

        myPets.setAdapter(new MyPetsAdapter(requireActivity(), cats));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cats.clear();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    PetModel petModel = dataSnapshot.getValue(PetModel.class);
                    if(petModel.getUserId().equals(uid)){
                        cats.add(petModel);
                    }
                }
                cats.add(addButton);
                myPets.setAdapter(new MyPetsAdapter(requireActivity(), cats));
                MyPetsAdapter adapter1 = (MyPetsAdapter) myPets.getAdapter();
                adapter1.setClickListener(HomeFragment.this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cats.add(addButton);
                myPets.setAdapter(new MyPetsAdapter(requireActivity(), cats));
            }
        });



    }

    @Override
    public void onItemClick(View view, int position) {
        PetModel myPet = cats.get(position);

        if(myPet.getName().equals("Add")){
            startActivity(new Intent(requireActivity(), PetInformationActivity.class));
        }else{
            Intent intent = new Intent(requireActivity(), CatInfoActivity.class);
            intent.putExtra("id",myPet.getId());
            startActivity(intent);
        }
    }

    private void onServiceItemClick(View view, int position){
        ServicesModel servicesModel = servicesModels.get(position);
        NearbyFragment nearbyFragment = NearbyFragment.newInstance(servicesModel.getName(),"");
        
        if(servicesModel.getName().equals("Connections")){
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder,new PetFragment()).commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder,nearbyFragment).commit();
        }
    }
}