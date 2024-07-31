package com.kerberos.breedhub.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.AboutActivity;
import com.kerberos.breedhub.activities.HelpActivity;
import com.kerberos.breedhub.activities.LoginActivity;
import com.kerberos.breedhub.activities.MyPetsActivity;
import com.kerberos.breedhub.activities.NotificationActivity;
import com.kerberos.breedhub.activities.RequestsActivity;
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.models.UserModel;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.views.EditProfileDetails;
import com.kerberos.breedhub.views.LoaderDialog;
import com.kerberos.breedhub.views.NotificationDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    DatabaseReference reference;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        LoaderDialog loaderDialog = new LoaderDialog(requireActivity());
        loaderDialog.show();
        if (imageUri != null) {
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
            StorageReference fileReference = storageReference.child(fileName + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(requireActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                                    // You can get the download URL here: uri.toString()
                                    reference.child("photo").setValue(uri.toString());
                                    loaderDialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loaderDialog.dismiss();
                            new NotificationDialog(e.getMessage()).show(getChildFragmentManager(),"ProfileFragment");
                        }
                    });
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout about = view.findViewById(R.id.about);
        LinearLayout share = view.findViewById(R.id.share);
        LinearLayout pets = view.findViewById(R.id.pets);
        LinearLayout help = view.findViewById(R.id.help);
        LinearLayout logout = view.findViewById(R.id.logout);
        LinearLayout breedingRequests = view.findViewById(R.id.requests);
        ShapeableImageView profileImage = view.findViewById(R.id.profileImage);
        MaterialTextView profileName = view.findViewById(R.id.name);
        MaterialTextView profileEmail = view.findViewById(R.id.email);
        ImageView editProfile = view.findViewById(R.id.edit);
        TinyDB tinyDB = new TinyDB(requireActivity());
        boolean isAdmin = tinyDB.getBoolean("isVet");
        ImageView notifications = view.findViewById(R.id.notifications);

        if(isAdmin){
            pets.setVisibility(View.GONE);
            breedingRequests.setVisibility(View.GONE);
        }

        notifications.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), NotificationActivity.class));
        });
        logout.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        breedingRequests.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), RequestsActivity.class));
        });

        profileName.setText(tinyDB.getString("fname"));

        editProfile.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder, new ProfileDetailFragment()).commit();
        });

        pets.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder, new MyPetsFragment()).commit();
        });
        share.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Download BreedHub App from the store today!");
            startActivity(Intent.createChooser(intent, "Share via"));
        });
        help.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), HelpActivity.class));
        });
        profileName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        profileEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        /*uploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });*/

        reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userModel = snapshot.getValue(UserData.class);
                profileName.setText(userModel.getFirstName());
                try{
                    Glide.with(requireActivity())
                            .load(userModel.getPhoto())
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(profileImage);
                }catch (Exception ignored){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new NotificationDialog(error.getDetails()).show(getChildFragmentManager(),"ProfileFragment");
            }
        });
        about.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), AboutActivity.class));
        });
    }
}