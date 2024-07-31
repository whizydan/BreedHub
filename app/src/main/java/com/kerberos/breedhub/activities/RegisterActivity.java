package com.kerberos.breedhub.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.intro.LocationActivity;
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.models.UserModel;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.views.LoaderDialog;
import com.kerberos.breedhub.views.NotificationDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MaterialTextView login = findViewById(R.id.login);
        MaterialButton register = findViewById(R.id.register);
        TextInputLayout email = findViewById(R.id.email);
        TextInputLayout password = findViewById(R.id.password);
        RadioGroup userType = findViewById(R.id.userType);
        final boolean[] isAdmin = {false};

        userType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == 0){
                    isAdmin[0] = false;
                } else if (i == 1) {
                    isAdmin[0] = true;
                }
            }
        });

        register.setOnClickListener(view -> {
            if(TextUtils.isEmpty(email.getEditText().getText().toString())){
                email.setError("Enter email");
            }else if (TextUtils.isEmpty(password.getEditText().getText().toString())) {
                password.setError("Enter password");
            }else{
                UserData user = new UserData();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String today = dateFormat.format(new Date());

                LoaderDialog loaderDialog = new LoaderDialog(RegisterActivity.this);
                loaderDialog.show();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                mAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loaderDialog.dismiss();
                                new NotificationDialog(e.getMessage()).show(getSupportFragmentManager(),"Register");
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user.setUid(authResult.getUser().getUid());
                                user.setCreatedOn(today);
                                user.setEmail(authResult.getUser().getEmail());
                                user.setPhoto("https://ttwo.dk/wp-content/uploads/2017/08/person-placeholder.jpg");
                                user.setFirstName("");
                                user.setLastName("");
                                user.setPhone("");
                                user.setVet(isAdmin[0]);
                                TinyDB tinyDB  = new TinyDB(RegisterActivity.this);
                                tinyDB.putBoolean("isVet",user.isVet());

                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(user.getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                loaderDialog.dismiss();
                                                mAuth.signInWithEmailAndPassword(email.getEditText().getText().toString(),password.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        startActivity(new Intent(RegisterActivity.this, LocationActivity.class));
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        new NotificationDialog("Error occurred: \n" + e.getMessage()).show(getSupportFragmentManager(),"TAG");
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loaderDialog.dismiss();
                                                new NotificationDialog(e.getMessage()).show(getSupportFragmentManager(),"Register");
                                            }
                                        });
                            }
                        });
            }
        });

        login.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}