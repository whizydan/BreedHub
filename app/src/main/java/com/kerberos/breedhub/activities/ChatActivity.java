package com.kerberos.breedhub.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.adapters.ChatsAdapter;
import com.kerberos.breedhub.models.ChatsModel;
import com.kerberos.breedhub.models.UserData;
import com.kerberos.breedhub.models.UserModel;
import com.kerberos.breedhub.views.NotificationDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private String phone;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextInputLayout send = findViewById(R.id.textInputLayout2);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageView back = findViewById(R.id.back);
        ImageView call = findViewById(R.id.call);
        MaterialTextView name = findViewById(R.id.name);
        ImageView image = findViewById(R.id.image);
        String key = getIntent().getStringExtra("id");
        ArrayList<ChatsModel> chats = new ArrayList<>();

        if(key.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            path = key;
        }else{
            path = key + FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(path);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ChatsModel chat = dataSnapshot.getValue(ChatsModel.class);
                    chats.add(chat);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this,LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(new ChatsAdapter(ChatActivity.this,chats));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new NotificationDialog(error.getDetails()).show(getSupportFragmentManager(),"ChatActivity");
            }
        });

        back.setOnClickListener(v ->{
            super.onBackPressed();
        });

        call.setOnClickListener(v -> {
            if(phone != null){
                dialPhoneNumber(phone);
            }
        });

        String id = getIntent().getStringExtra("id").replace(FirebaseAuth.getInstance().getCurrentUser().getUid(),"");
        FirebaseDatabase.getInstance().getReference("users").child(id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserData userModel = snapshot.getValue(UserData.class);
                                name.setText(userModel.getFirstName());
                                phone = userModel.getPhone();

                                try{
                                    Glide.with(ChatActivity.this)
                                            .load(userModel.getPhoto())
                                            .placeholder(R.drawable.logo)
                                            .error(R.drawable.logo)
                                            .into(image);
                                }catch (Exception ignored){}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new NotificationDialog(error.getDetails()).show(getSupportFragmentManager(),"ChatActivity");
                            }
                        });

        send.setEndIconOnClickListener(v -> {
            if(TextUtils.isEmpty(send.getEditText().getText())){
                send.setError("Enter Message");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        send.setError("");
                    }
                }, 2000);
            }else{
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                ChatsModel chatsModel = new ChatsModel();
                chatsModel.setId(String.valueOf(System.currentTimeMillis()));
                chatsModel.setMessage(send.getEditText().getText().toString());
                chatsModel.setTime(timeFormat.format(new Date()));
                chatsModel.setDate(dateFormat.format(new Date()));
                chatsModel.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(chatsModel.getId()).setValue(chatsModel).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new NotificationDialog(e.getMessage()).show(getSupportFragmentManager(),"ChatActivity");
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        send.getEditText().getText().clear();
                    }
                });
            }
        });
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}