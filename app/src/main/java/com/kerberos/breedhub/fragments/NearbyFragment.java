package com.kerberos.breedhub.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.CentreInfoActivity;
import com.kerberos.breedhub.adapters.CentresAdapter;
import com.kerberos.breedhub.models.Locations;
import com.kerberos.breedhub.toolkit.Utilities;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyFragment extends Fragment implements CentresAdapter.ItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Locations> locations;

    public NearbyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearbyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyFragment newInstance(String param1, String param2) {
        NearbyFragment fragment = new NearbyFragment();
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
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView centresList = view.findViewById(R.id.centresList);
        locations = Utilities.getLocations();

        CentresAdapter adapter = new CentresAdapter(requireActivity(), locations);
        centresList.setAdapter(adapter);

        TextInputLayout search = view.findViewById(R.id.textInputLayout);
        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        adapter.setClickListener(this);

        if(mParam1 != null){
            adapter.filter(mParam1);
            search.getEditText().setText(mParam1);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Locations locations1 = locations.get(position);
        MapFragment nearbyFragment = MapFragment.newInstance(locations1.getLongitude(), locations1.getLatitude(), locations1.getName(),locations1.getImage());

        //requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder,nearbyFragment).commit();

        Intent intent = new Intent(requireActivity(), CentreInfoActivity.class);
        intent.putExtra("name", locations1.getName());
        intent.putExtra("long", locations1.getLongitude());
        intent.putExtra("lat", locations1.getLatitude());
        intent.putExtra("img", locations1.getImage());
        intent.putExtra("desc", locations1.getDescription());
        intent.putExtra("phone", locations1.getPhone());
        intent.putExtra("sp", locations1.getSpecialty().toString());
        startActivity(intent);

    }
}