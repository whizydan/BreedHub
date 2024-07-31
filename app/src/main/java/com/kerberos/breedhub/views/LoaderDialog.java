package com.kerberos.breedhub.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.kerberos.breedhub.R;

public class LoaderDialog extends Dialog {
    public LoaderDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);
        getWindow().getDecorView().setBackground(null);
    }
}
