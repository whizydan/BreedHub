package com.kerberos.breedhub.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
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
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.toolkit.TinyDB;

public class EditProfileActivity extends AppCompatActivity {
    private String downloadUrl;
    private Uri imageUri ;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageRef;
    ShapeableImageView imageView;
    UserData userData ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        MaterialButton completeProfile = findViewById(R.id.complete);
        EditText phone  = findViewById(R.id.phone);
        EditText lname = findViewById(R.id.lname);
        EditText fname = findViewById(R.id.fname);
        imageView = findViewById(R.id.profilePicture);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData = snapshot.getValue(UserData.class);
                fname.setText(userData.getFirstName());
                lname.setText(userData.getLastName());
                phone.setText(userData.getPhone());

                try{
                    Glide.with(EditProfileActivity.this)
                            .load(userData.getPhoto())
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(imageView);
                }catch (Exception ignored){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        completeProfile.setOnClickListener(v -> {
            if(TextUtils.isEmpty(fname.getText())){
                fname.setError("Enter first name");
            }else if(TextUtils.isEmpty(lname.getText())){
                lname.setError("Enter last name");
            } else if (TextUtils.isEmpty(phone.getText())) {
                phone.setError("Enter phone number");
            }else{
                userData.setFirstName(fname.getText().toString());
                userData.setLastName(lname.getText().toString());
                userData.setPhone(phone.getText().toString());
                if(downloadUrl != null || !downloadUrl.isEmpty()){
                    userData.setPhoto(downloadUrl);
                }

                TinyDB tinyDB = new TinyDB(this);
                tinyDB.putString("lname", lname.getText().toString());
                tinyDB.putString("fname", fname.getText().toString());
                tinyDB.putString("phone", phone.getText().toString());

                reference.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("status", "0");
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadFile();
        }
    }
    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();
                                    // Save the download URL as needed
                                    Toast.makeText(EditProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }
}
