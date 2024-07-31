package com.kerberos.breedhub.views;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.PetModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPet extends BottomSheetDialogFragment {
    ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    DatabaseReference reference;
    String downloadUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_pet,container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton save = view.findViewById(R.id.save);
        TextInputLayout name = view.findViewById(R.id.name);
        TextInputLayout breed = view.findViewById(R.id.breed);
        TextInputLayout color = view.findViewById(R.id.color);
        TextInputLayout age = view.findViewById(R.id.age);
        TextInputLayout weight = view.findViewById(R.id.weight);
        TextInputLayout description = view.findViewById(R.id.description);
        RadioGroup gender = view.findViewById(R.id.gender);
        RadioButton male = view.findViewById(R.id.male);
        RadioButton female= view.findViewById(R.id.female);
        imageView = view.findViewById(R.id.image);
        final boolean[] isMale = {false};
        reference = FirebaseDatabase.getInstance().getReference("pets");

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.male){
                    isMale[0] = true;
                }
            }
        });

//        save.setOnClickListener(v -> {
//            PetModel petModel = new PetModel();
//            petModel.setPhoto(downloadUrl);
//            petModel.setAge(age.getEditText().getText().toString());
//            petModel.setBreed(breed.getEditText().getText().toString());
//            petModel.setId(String.valueOf(System.currentTimeMillis()));
//            petModel.setColor(color.getEditText().getText().toString());
//            petModel.setDescription(description.getEditText().getText().toString());
//            petModel.setName(name.getEditText().getText().toString());
//            petModel.setWeight(weight.getEditText().getText().toString());
//            petModel.setMale(isMale[0]);
//            petModel.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//            reference.child(petModel.getId()).setValue(petModel)
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            new NotificationDialog(e.getMessage()).show(getChildFragmentManager(),"Addpet");
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            dismiss();
//                        }
//                    });
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
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
                                    downloadUrl = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new NotificationDialog(e.getMessage()).show(getChildFragmentManager(),"ProfileFragment");
                        }
                    });
        }
    }

}
