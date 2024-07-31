package com.kerberos.breedhub.activities;

import android.os.Bundle;
import android.text.Html;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textview.MaterialTextView;
import com.kerberos.breedhub.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        MaterialTextView aboutUsTextView = findViewById(R.id.about);
        MaterialTextView title = findViewById(R.id.title);
        title.setText("Help");
        aboutUsTextView.setText(Html.fromHtml(getString(R.string.help_content)));
    }
}