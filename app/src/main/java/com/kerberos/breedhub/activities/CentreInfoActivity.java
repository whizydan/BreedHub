package com.kerberos.breedhub.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.kerberos.breedhub.R;

public class CentreInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_centre_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String lat = getIntent().getStringExtra("lat");
        String lon = getIntent().getStringExtra("long");
        String name = getIntent().getStringExtra("name");
        int image = getIntent().getIntExtra("img", R.drawable.breeding_image);
        String desc = getIntent().getStringExtra("desc");
        String phone = getIntent().getStringExtra("phone");
        String sp = getIntent().getStringExtra("sp");

        MaterialButton visit = findViewById(R.id.visit);
        TextView description = findViewById(R.id.textView7);
        TextView title = findViewById(R.id.textView5);
        ImageView imageView = findViewById(R.id.imageView4);

        description.setText(desc);
        title.setText(name);
        imageView.setImageDrawable(getDrawable(image));


        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");


        visit.setOnClickListener(v -> {
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });

    }
}