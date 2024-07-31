package com.kerberos.breedhub.activities;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.adapters.BreedingAdapter;
import com.kerberos.breedhub.models.RequestModel;
import com.kerberos.breedhub.views.NotificationDialog;

import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity implements BreedingAdapter.ItemClickListener{
    ArrayList<RequestModel> requests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final BreedingAdapter[] adapter = new BreedingAdapter[1];
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference.child("req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    RequestModel request = dataSnapshot.getValue(RequestModel.class);

                    if(request.getUser1().getUid().equals(userId) || request.getUser2().getUid().equals(userId)){
                        requests.add(request);
                    }
                }

                adapter[0] = new BreedingAdapter(RequestsActivity.this, requests);
                recyclerView.setAdapter(adapter[0]);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new NotificationDialog(error.getDetails()).show(getSupportFragmentManager(),"RequestActivity");
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        RequestModel request = requests.get(position);

        Intent intent = new Intent(this, BreedingDetailsActivity.class);
        intent.putExtra("id", request.getId());
        startActivity(intent);
    }
}