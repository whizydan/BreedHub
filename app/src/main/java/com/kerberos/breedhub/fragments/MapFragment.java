package com.kerberos.breedhub.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.kerberos.breedhub.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "long";
    private static final String ARG_PARAM2 = "lat";
    private static final String ARG_PARAM3 = "loc";
    private static final String ARG_PARAM4 = "img";
    private String longitude;
    private String latitude;
    private String location;
    private String imageUrl;
    TextInputLayout search;
    MapView mapView;
    ShapeableImageView image;
    MaterialTextView title;
    private GoogleMap mMap;
    int imagePath;

    public MapFragment() {}

    public static MapFragment newInstance(String longitude, String latitude, String locationName, int imagePath) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, longitude);
        args.putString(ARG_PARAM2, latitude);
        args.putString(ARG_PARAM3, locationName);
        fragment.imagePath = imagePath;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            longitude = getArguments().getString(ARG_PARAM1);
            latitude = getArguments().getString(ARG_PARAM2);
            location = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search = view.findViewById(R.id.search);
        mapView = view.findViewById(R.id.map);
        title = view.findViewById(R.id.text);
        image = view.findViewById(R.id.image);
        MaterialCardView section = view.findViewById(R.id.section);

        TextInputEditText searchEditText = (TextInputEditText) search.getEditText();

        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String query = s.toString();
                    if (!query.isEmpty()) {
                        searchLocation(query);
                    }
                }
            });
        }

        if (location == null) {
            // New fragment from default constructor, show map and allow user to search
            section.setVisibility(View.GONE);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        } else {
            // Instance of fragment, show acquired data
            image.setImageDrawable(requireActivity().getDrawable(imagePath));

            title.setText(location);

            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }
    }

    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (longitude != null && latitude != null) {
            double lon = Double.parseDouble(longitude);
            double lat = Double.parseDouble(latitude);
            LatLng location = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(location).title("Marker in " + this.location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
