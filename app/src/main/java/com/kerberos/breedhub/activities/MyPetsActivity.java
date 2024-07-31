package com.kerberos.breedhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.adapters.PetsAdapter;
import com.kerberos.breedhub.models.PetModel;

import java.util.ArrayList;

public class MyPetsActivity extends AppCompatActivity implements PetsAdapter.ItemClickListener {
    ArrayList<PetModel> cats = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_pets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView myPets = findViewById(R.id.myPets);

        FirebaseDatabase.getInstance().getReference("cats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            PetModel petModel = dataSnapshot.getValue(PetModel.class);

                            if(petModel.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                cats.add(petModel);
                            }
                        }

                        PetsAdapter adapter = new PetsAdapter(MyPetsActivity.this, cats);
                        myPets.setAdapter(adapter);
                        adapter.setClickListener(MyPetsActivity.this::onItemClick);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),error.getDetails(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        PetModel cat = cats.get(position);
        Intent intent = new Intent(this, CatInfoActivity.class);
        intent.putExtra("id",cat.getId());
        startActivity(intent);
    }
}