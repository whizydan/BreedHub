package com.kerberos.breedhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.ChatsModel;
import com.kerberos.breedhub.models.PetModel;
import com.kerberos.breedhub.models.RequestModel;
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.toolkit.Utilities;
import com.kerberos.breedhub.views.NotificationDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactActivity extends AppCompatActivity {
    UserData user, user2 = new UserData();
    PetModel cat1, cat2 = new PetModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String breederId = getIntent().getStringExtra("id");

        TextInputEditText message = findViewById(R.id.textInputEditText);
        MaterialButton request = findViewById(R.id.request);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String myCatId = getIntent().getStringExtra("cat1");
        String otherCat = getIntent().getStringExtra("cat2");

        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UserData userData = dataSnapshot.getValue(UserData.class);

                    if(userData.getUid() != null && userData.getUid().equals(breederId)){
                        user2 = userData;
                    }else if(userData.getUid() != null && userData.getUid().equals(userId)){
                        user = userData;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                request.setEnabled(false);
            }
        });

        reference.child("cats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    PetModel cat = dataSnapshot.getValue(PetModel.class);

                    if(cat.getId().equals(myCatId)){
                        cat1 = cat;
                    }else if(cat.getId().equals(otherCat)){
                        cat2 = cat;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                request.setEnabled(false);
            }
        });

        if(breederId.equals(userId)){
            request.setVisibility(View.GONE);
        }

        request.setOnClickListener(v -> {
            if(TextUtils.isEmpty(message.getText().toString())){
                message.setError("Enter message");
            }else{
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                ChatsModel chatItem = new ChatsModel();
                chatItem.setDate(dateFormat.format(date));
                chatItem.setUserId(userId);
                chatItem.setMessage("Hello there,\nI have sent a request to breed my cat with yours");
                chatItem.setId(String.valueOf(System.currentTimeMillis()));
                chatItem.setTime(timeFormat.format(date));

                //notify the other person so the chat appears in the chatview
                reference.child("chats").child(breederId.concat(userId)).child(chatItem.getId()).setValue(chatItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Request posted",Toast.LENGTH_LONG).show();
                    }
                });

                //save this request to breed so admin can approve and user can view progress
                RequestModel requestModel = new RequestModel();
                requestModel.setId(String.valueOf(System.currentTimeMillis()));
                requestModel.setCat1(cat1);
                requestModel.setCat2(cat2);
                requestModel.setStatus("Pending");
                requestModel.setNextSteps("");
                requestModel.setRejectionReason("");
                requestModel.setUser1(user);
                requestModel.setUser2(user2);

                reference.child("req").child(requestModel.getId()).setValue(requestModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //start the activity
                        startActivity(new Intent(ContactActivity.this, MainActivity.class));
                        finish();
                    }
                });

            }
        });

    }
}