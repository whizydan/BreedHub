package com.kerberos.breedhub.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.RequestModel;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.views.NotificationDialog;

import java.util.ArrayList;
import java.util.List;

public class BreedingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_breeding_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final RequestModel[] requestModel = {new RequestModel()};
        ShapeableImageView cat1 = findViewById(R.id.cat2);
        ShapeableImageView cat2 = findViewById(R.id.cat3);
        String requestId = getIntent().getStringExtra("id");
        MaterialTextView cat1Name = findViewById(R.id.catName);
        MaterialTextView cat1Breed =  findViewById(R.id.catBreed);
        MaterialTextView cat2Name = findViewById(R.id.cat2Name);
        MaterialTextView cat2Breed = findViewById(R.id.cat2Breed);
        MaterialTextView user1Name = findViewById(R.id.user1Name);
        MaterialTextView user2Name = findViewById(R.id.user2Name);
        MaterialTextView statusText = findViewById(R.id.statusText);
        RadioGroup status = findViewById(R.id.status);
        TextInputLayout rejectionReason = findViewById(R.id.rejectionReason);
        TextInputLayout nextSteps = findViewById(R.id.nextSteps);
        MaterialButton submit = findViewById(R.id.submit);
        MaterialTextView reason = findViewById(R.id.reason);
        LinearLayout adminSection = findViewById(R.id.adminSection);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("req").child(requestId);
        TinyDB tinyDB = new TinyDB(this);

        if(tinyDB.getBoolean("isVet")){
            adminSection.setVisibility(View.VISIBLE);
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestModel[0] = snapshot.getValue(RequestModel.class);

                //set the view content
                reason.setText(requestModel[0].getRejectionReason());
                cat1Name.setText("Cat1Name: "+requestModel[0].getCat1().getName());
                cat2Name.setText("Cat2Name: "+requestModel[0].getCat2().getName());
                user1Name.setText(requestModel[0].getUser1().getFirstName() + ": " + requestModel[0].getCat1().getName());
                user2Name.setText(requestModel[0].getUser2().getFirstName() + ": " + requestModel[0].getCat2().getName());
                statusText.setText(requestModel[0].getRejectionReason());
                cat1Breed.setText("Breed: " + requestModel[0].getCat1().getBreed());
                cat2Breed.setText("Breed: " + requestModel[0].getCat2().getBreed());
                rejectionReason.getEditText().setText(requestModel[0].getRejectionReason());

                switch (requestModel[0].getStatus()){
                    case "Pending":
                        break;
                    case "Approved":
                        nextSteps.setVisibility(View.VISIBLE);
                        rejectionReason.setVisibility(View.GONE);
                        nextSteps.getEditText().setText(requestModel[0].getNextSteps());
                        reason.setText(requestModel[0].getNextSteps());
                        break;
                    case "Rejected":
                        rejectionReason.setVisibility(View.VISIBLE);
                        nextSteps.setVisibility(View.GONE);
                        rejectionReason.getEditText().setText(requestModel[0].getRejectionReason());
                        reason.setText(requestModel[0].getRejectionReason());
                        break;
                }


                try{
                    Glide.with(BreedingDetailsActivity.this)
                            .load(requestModel[0].getCat1().getPhoto().get(0))
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(cat1);
                }catch (Exception ignored){}
                try{
                    Glide.with(BreedingDetailsActivity.this)
                            .load(requestModel[0].getCat2().getPhoto().get(0))
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(cat2);
                }catch (Exception ignored){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new NotificationDialog(error.getDetails()).show(getSupportFragmentManager(), "BreedingDetailsActivity");
            }
        });

        status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(!requestModel[0].getStatus().equalsIgnoreCase("")){
                    List<String> statuses = new ArrayList<>();
                    statuses.add("Pending");statuses.add("Approved");statuses.add("Rejected");

                    requestModel[0].setStatus(statuses.get(i));
                    reference.setValue(requestModel[0]);
                }
            }
        });

        submit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(rejectionReason.getEditText().getText())){
                rejectionReason.setError("Enter a reason");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rejectionReason.setError("");
                    }
                }, 1000);
            }else if(rejectionReason.getEditText().getText().toString().trim().length() < 10){
                rejectionReason.setError("Enter more than 10 characters");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rejectionReason.setError("");
                    }
                }, 1000);
            }else if(TextUtils.isEmpty(nextSteps.getEditText().getText())){
                nextSteps.setError("Enter next steps");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextSteps.setError("");
                    }
                }, 1000);
            }else if(nextSteps.getEditText().getText().toString().trim().length() < 10){
                nextSteps.setError("Enter more than 10 characters");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextSteps.setError("");
                    }
                }, 1000);
            }else{
                requestModel[0].setRejectionReason(rejectionReason.getEditText().getText().toString());
                requestModel[0].setNextSteps(nextSteps.getEditText().getText().toString());

                reference.setValue(requestModel[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        new NotificationDialog("Updated").show(getSupportFragmentManager(), "BreedingDetailsActivity");
                    }
                });
            }
        });

    }
}