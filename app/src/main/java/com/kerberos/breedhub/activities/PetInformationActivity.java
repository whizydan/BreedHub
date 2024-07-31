package com.kerberos.breedhub.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.adapters.PetImagesAdapter;
import com.kerberos.breedhub.models.PetModel;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.toolkit.Utilities;
import com.kerberos.breedhub.views.LoaderDialog;
import com.kerberos.breedhub.views.NotificationDialog;
import com.maxkeppeler.sheets.calendar.CalendarSheet;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class PetInformationActivity extends AppCompatActivity implements PetImagesAdapter.ItemClickListener {
    private static final int PICK_IMAGES_REQUEST = 100;
    boolean isMale = true;
    ArrayList<String> petImages = new ArrayList<>();
    ArrayList<Uri> imageUris = new ArrayList<>();
    String addButtonUrl = "https://png.pngtree.com/png-vector/20190419/ourmid/pngtree-vector-add-icon-png-image_956621.jpg";
    RecyclerView images;
    String breed = "";
    String health = "";
    private Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner = findViewById(R.id.spinner);
        MaterialButton selectBreed = findViewById(R.id.next);
        MaterialButton skip = findViewById(R.id.skip);
        MaterialButton male = findViewById(R.id.male);
        MaterialButton female = findViewById(R.id.female);
        ImageView back = findViewById(R.id.back);
        TextInputEditText name = findViewById(R.id.name);
        TextInputEditText dob = findViewById(R.id.birthday);
        MaterialSwitch readyToBreed = findViewById(R.id.ready);
        TextInputEditText description = findViewById(R.id.description);
        images = findViewById(R.id.images);
        TextInputLayout date = findViewById(R.id.date);
        MaterialButton healthInput = findViewById(R.id.health);

        ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        pdfUri = result.getData().getData();
                        uploadFileToFirebase();
                    }
                }
        );

        healthInput.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            filePickerLauncher.launch(intent);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.breeds, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cats");

        back.setOnClickListener(v->{
            super.onBackPressed();
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                breed = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                breed = adapterView.getItemAtPosition(0).toString();
            }
        });

        date.setEndIconOnClickListener(v -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            CalendarSheet calendarSheet = new CalendarSheet();
            calendarSheet.setWindowContext(this);
            Calendar calendar = Calendar.getInstance();
            Calendar dateEnd = calendar;
            dateEnd.add(Calendar.DAY_OF_YEAR,50);
            calendarSheet.setSelectedDateRange(calendar, dateEnd);
            calendarSheet.onPositive("Confirm", R.drawable.done, new Function2<Calendar, Calendar, Unit>() {
                @Override
                public Unit invoke(Calendar calendar, Calendar calendar2) {
                    Date date1 = calendar.getTime();
                    dob.setText(dateFormat.format(date1));
                    return null;
                }
            });
            Utilities.getDate("Select date", this, calendarSheet);
        });

        petImages.add(addButtonUrl);
        updateList();

        selectBreed.setOnClickListener(v -> {
            if(TextUtils.isEmpty(name.getText())){
                name.setError("Enter name");
            } else if (TextUtils.isEmpty(dob.getText())) {
                dob.setError("Enter birthday");
            } else if (petImages.isEmpty()) {
                new NotificationDialog("Please select at least one  image").show(getSupportFragmentManager(),"PetInformationActivity");
            } else {
                LoaderDialog loaderDialog = new LoaderDialog(this);
                loaderDialog.show();
                Intent intent = new Intent(this, SuccessActivity.class);
                petImages.remove(addButtonUrl);
                TinyDB tinyDB = new TinyDB(this);

                PetModel petModel = new PetModel();
                petModel.setPhoto(petImages);
                petModel.setBreed(breed);
                petModel.setMale(isMale);
                petModel.setId(String.valueOf(System.currentTimeMillis()));
                petModel.setWeight("");
                petModel.setAge(dob.getText().toString());
                petModel.setName(name.getText().toString());
                petModel.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                petModel.setDescription(description.getText().toString());
                petModel.setColor("");
                petModel.setBodyType("");
                petModel.setCoat("");
                petModel.setOrigin("");
                petModel.setPattern("");
                petModel.setHealth(health);
                petModel.setReadyToBreed(readyToBreed.isChecked());
                petModel.setCountry(tinyDB.getString("country"));

                ref.child(petModel.getId()).setValue(petModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loaderDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loaderDialog.dismiss();
                        NotificationDialog notificationDialog = new NotificationDialog("Error occurred: "+ e.getMessage());
                        notificationDialog.show(getSupportFragmentManager(),"PetInformationActivity");
                    }
                });


            }
        });
        skip.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        male.setBackgroundColor(getResources().getColor(R.color.positive));
        male.setTextColor(Color.WHITE);

        male.setOnClickListener(v -> {
            isMale = true;
            v.setBackgroundColor(getResources().getColor(R.color.positive));
            female.setBackgroundColor(Color.parseColor("#00000000"));
            male.setTextColor(Color.WHITE);
            female.setTextColor(getResources().getColor(R.color.positive));
        });
        female.setOnClickListener(v -> {
            isMale = false;
            v.setBackgroundColor(getResources().getColor(R.color.positive));
            male.setBackgroundColor(Color.parseColor("#00000000"));
            female.setTextColor(Color.WHITE);
            male.setTextColor(getResources().getColor(R.color.positive));
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        String petImage = petImages.get(position);

        if(petImage.contains(addButtonUrl)){
            //start the image capture
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGES_REQUEST);
        }else{
            //do nothing
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
            }
            uploadImagesToFirebase();
        }
    }
    public void updateList(){
        PetImagesAdapter adapter = new PetImagesAdapter(this, petImages);
        images.setAdapter(adapter);
        adapter.setClickListener(this);
    }
    private void uploadImagesToFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        for (Uri imageUri : imageUris) {
            StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    petImages.add(downloadUri.toString());
                                    //clear the uris so we don't upload same images again
                                    imageUris.clear();
                                    updateList();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    private void uploadFileToFirebase() {
        if (pdfUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference pdfRef = storageReference.child("pdfs/" + DocumentsContract.getDocumentId(pdfUri));

            UploadTask uploadTask = pdfRef.putFile(pdfUri);
            uploadTask.addOnSuccessListener(taskSnapshot ->
                            pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                health = uri.toString();
                                Toast.makeText(PetInformationActivity.this, "File Uploaded Successfully. URL: " + downloadUrl, Toast.LENGTH_LONG).show();
                                // You can now use the download URL for further operations
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(PetInformationActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}