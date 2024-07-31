package com.kerberos.breedhub.activities;

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
import com.google.firebase.auth.FirebaseAuth;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.views.NotificationDialog;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        MaterialButton reset = findViewById(R.id.reset);
        TextInputLayout email = findViewById(R.id.email);

        reset.setOnClickListener(view -> {
            if(TextUtils.isEmpty(email.getEditText().getText().toString())){
                email.setError("Enter email");
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getEditText().getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showMessage("An email with instructions to reset your password was " +
                                        "sent to your email: " + email.getEditText().getText().toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("An error occurred while trying to connect: " + e.getLocalizedMessage());
                            }
                        });
            }
        });
    }

    private void showMessage(String message){
        new NotificationDialog(message).show(getSupportFragmentManager(),"ResetPassword");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}