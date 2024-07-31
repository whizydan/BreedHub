package com.kerberos.breedhub.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.adapters.CarouselAdapter;
import com.kerberos.breedhub.models.PetModel;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.views.CircleItemDecoration;
import com.kerberos.breedhub.views.LoaderDialog;
import com.kerberos.breedhub.views.NotificationDialog;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CatInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cat_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView like = findViewById(R.id.like);

        ActionMenuView actionMenuView = findViewById(R.id.actionMenu);
        MenuInflater menuInflater = new MenuInflater(this);
        Menu menu = actionMenuView.getMenu();
        menuInflater.inflate(R.menu.menu_action, menu);
        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        MaterialTextView name = findViewById(R.id.name);
        MaterialTextView country = findViewById(R.id.country);
        MaterialTextView description = findViewById(R.id.description);
        MaterialTextView seeMore = findViewById(R.id.seeMore);
        MaterialTextView origin = findViewById(R.id.origin);
        MaterialTextView bodyType = findViewById(R.id.bodyType);
        MaterialTextView pattern = findViewById(R.id.pattern);
        MaterialTextView coat = findViewById(R.id.coat);
        MaterialButton messageBreeder = findViewById(R.id.messageBreeder);
        MaterialButton viewHealthRecord = findViewById(R.id.health);
        String catId = getIntent().getStringExtra("id");
        final String[] breederId = {""};
        final String[] myCatId = {""};
        final boolean[] favorite = {false};
        final PetModel[] cat = {new PetModel()};

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("fav").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    PetModel petModel = dataSnapshot.getValue(PetModel.class);

                    if(petModel.getId().equalsIgnoreCase(catId)){
                        like.setImageDrawable(getDrawable(R.drawable.favorite_filled));
                        favorite[0] = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewHealthRecord.setOnClickListener(v -> {
            LoaderDialog dialog = new LoaderDialog(this);
            dialog.show();

            StorageReference reference1 = FirebaseStorage.getInstance().getReference();
            if (cat[0] != null) {
                String pdfUrl = cat[0].getHealth();
                StorageReference pdfRef = reference1.child(pdfUrl);

                // Create a temporary file to download the PDF to
                try {
                    File localFile = File.createTempFile("health_record", ".pdf");
                    pdfRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        dialog.dismiss();
                        // Open the PDF using an Intent
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(localFile), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        Intent chooser = Intent.createChooser(intent, "Open PDF");
                        try {
                            startActivity(chooser);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(this, "No application found to open PDF", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Failed to download PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    dialog.dismiss();
                    Toast.makeText(this, "Error creating temp file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        like.setOnClickListener(v -> {
            if(favorite[0]){
                //remove from favorites
                reference.child("fav").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(catId).removeValue();
                like.setImageDrawable(getDrawable(R.drawable.favorite_empty));
            }else{
                //add to favorites
                reference.child("fav").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(catId)
                        .setValue(cat[0]);
                like.setImageDrawable(getDrawable(R.drawable.favorite_filled));
            }
        });
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference.child("cats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                   PetModel pet = dataSnapshot.getValue(PetModel.class);
                   if(pet.getId().equalsIgnoreCase(catId)){
                       cat[0] = dataSnapshot.getValue(PetModel.class);
                       CarouselAdapter adapter = new CarouselAdapter(CatInfoActivity.this, pet.getPhoto());
                       recyclerView.setAdapter(adapter);

                       int circleColor = Color.GREEN; // Replace with desired circle color
                       int circleRadius = 20; // Replace with desired circle radius
                       CircleItemDecoration itemDecoration = new CircleItemDecoration(circleColor, circleRadius);
                       //recyclerView.addItemDecoration(itemDecoration);
                       //coat.setText(cat.getCoat());
                       //pattern.setText(cat.getPattern());
                       //bodyType.setText(cat.getBodyType());
                       //origin.setText(cat.getOrigin());
                       name.setText(pet.getName());
                       country.setText(pet.getCountry());
                       description.setText(pet.getDescription());
                       breederId[0] = pet.getUserId();
                   }

                   if(pet.getUserId() != null && pet.getUserId().equals(userId)){
                       myCatId[0] = pet.getId();
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new NotificationDialog("Error occurred: " + error.getDetails()).show(getSupportFragmentManager(), "CatInfo");
            }
        });

        seeMore.setOnClickListener(v -> {
            int lines = seeMore.getMaxLines();
            if(lines == 2){
                seeMore.setText("See less");
                seeMore.setMaxLines(200);
            }else{
                seeMore.setText("See more");
                seeMore.setMaxLines(2);
            }
        });

        messageBreeder.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactActivity.class);
            intent.putExtra("id",breederId[0]);
            intent.putExtra("cat1",myCatId[0]);
            intent.putExtra("cat2",cat[0].getId());
            startActivity(intent);
        });

        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.edit){

                }else if(item.getItemId() == R.id.delete){
                    reference.child("cats").child(catId).removeValue()
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    new NotificationDialog("Error occurred: " + e.getMessage()).show(getSupportFragmentManager(), "CatInfo");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Deleted successfully", Toast.LENGTH_LONG).show();
                                    CatInfoActivity.super.onBackPressed();
                                }
                            });
                }
                return false;
            }
        });
    }
}