package com.kerberos.breedhub.activities;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.textview.MaterialTextView;
import com.kerberos.breedhub.databinding.ActivityAboutBinding;

import com.kerberos.breedhub.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        MaterialTextView aboutUsTextView = findViewById(R.id.about);
        aboutUsTextView.setText(Html.fromHtml(getString(R.string.about_us_content)));
    }
}