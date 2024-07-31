package com.kerberos.breedhub.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.UserModel;

public class EditProfileDetails extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_profile_details,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputLayout name = view.findViewById(R.id.name);
        TextInputLayout phone = view.findViewById(R.id.phone);
        TextInputLayout status = view.findViewById(R.id.status);
        MaterialButton save = view.findViewById(R.id.save);
        final UserModel[] userModel = new UserModel[1];
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel[0] = snapshot.getValue(UserModel.class);
                phone.getEditText().setText(userModel[0].getPhone());
                status.getEditText().setText(userModel[0].getStatus());
                name.getEditText().setText(userModel[0].getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new NotificationDialog(error.getDetails()).show(getChildFragmentManager(),"EditProfile");
            }
        });

        save.setOnClickListener(v -> {
            if(TextUtils.isEmpty(name.getEditText().getText().toString())){
                name.setError("Enter name");
            } else if (TextUtils.isEmpty(phone.getEditText().getText().toString())) {
                phone.setError("Enter phone");
            } else if (TextUtils.isEmpty(status.getEditText().getText().toString())) {
                status.setError("Enter status text");
            }else{
                userModel[0].setName(name.getEditText().getText().toString());
                userModel[0].setStatus(status.getEditText().getText().toString());
                userModel[0].setPhone(phone.getEditText().getText().toString());

                reference.setValue(userModel[0])
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dismiss();
                                new NotificationDialog("Profile updated").show(getChildFragmentManager(),"EditProfile");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dismiss();
                                new NotificationDialog(e.getMessage()).show(getChildFragmentManager(),"EditProfile");
                            }
                        });
            }
        });
    }
}
