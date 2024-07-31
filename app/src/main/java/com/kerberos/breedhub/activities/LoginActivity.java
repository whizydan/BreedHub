package com.kerberos.breedhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.models.UserModel;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.views.LoaderDialog;
import com.kerberos.breedhub.views.NotificationDialog;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TinyDB tinyDB = new TinyDB(this);

        MaterialTextView forgotPassword = findViewById(R.id.forgot_password);
        MaterialButton login = findViewById(R.id.login);
        MaterialTextView register = findViewById(R.id.register);
        MaterialTextView register2 = findViewById(R.id.register2);
        TextInputLayout email = findViewById(R.id.email);
        TextInputLayout password = findViewById(R.id.password);

        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
        login.setOnClickListener(view -> {
            String emailValue = email.getEditText().getText().toString();
            String passwordValue = password.getEditText().getText().toString();
            if(TextUtils.isEmpty(emailValue)){
                email.setError("Enter email");
            } else if (TextUtils.isEmpty(passwordValue)) {
                password.setError("Enter password");
            }else{
                LoaderDialog loaderDialog = new LoaderDialog(LoginActivity.this);
                loaderDialog.show();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailValue, passwordValue)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loaderDialog.dismiss();
                                new NotificationDialog(e.getMessage()).show(getSupportFragmentManager(),"Login");
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseDatabase.getInstance().getReference("users").child(authResult.getUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                UserData userModel = snapshot.getValue(UserData.class);
                                                tinyDB.putString("user",userModel.toString());
                                                tinyDB.putBoolean("isVet",userModel.isVet());
                                                tinyDB.putString("fname", userModel.getFirstName());

                                                loaderDialog.dismiss();
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                loaderDialog.dismiss();
                                                new NotificationDialog(error.getMessage()).show(getSupportFragmentManager(),"Login");
                                            }
                                        });
                            }
                        });

            }
        });
        register.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        register2.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}