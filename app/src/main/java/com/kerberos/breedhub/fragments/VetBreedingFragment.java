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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.BreedingDetailsActivity;
import com.kerberos.breedhub.adapters.BreedingAdapter;
import com.kerberos.breedhub.models.RequestModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VetBreedingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VetBreedingFragment extends Fragment implements BreedingAdapter.ItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<RequestModel> requests = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VetBreedingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VetBreedingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VetBreedingFragment newInstance(String param1, String param2) {
        VetBreedingFragment fragment = new VetBreedingFragment();
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
        return inflater.inflate(R.layout.fragment_vet_breeding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.breedingList);
        final BreedingAdapter[] adapter = new BreedingAdapter[1];

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL, false));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    requests.add(dataSnapshot.getValue(RequestModel.class));
                }

                adapter[0] = new BreedingAdapter(requireActivity(), requests);
                recyclerView.setAdapter(adapter[0]);
                adapter[0].setClickListener(VetBreedingFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(requireActivity(), BreedingDetailsActivity.class);
        intent.putExtra("id", requests.get(position).getId());
        startActivity(intent);
    }
}