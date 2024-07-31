package com.kerberos.breedhub.intro;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.activities.LoginActivity;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OnBoardingActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ArrayList<DataItem> dataItems = new ArrayList<>();
        dataItems.add(new DataItem("Breeding", "Upload your pet and chat with other breeders in your area", R.drawable.cats1));
        dataItems.add(new DataItem("Find or offer pet services", "Walking, Grooming, Training", R.drawable.cats2));
        dataItems.add(new DataItem("Find pet centres in your area", "Adoption centres, veterenians", R.drawable.cats3));

        AtomicInteger currentSet = new AtomicInteger();
        AtomicInteger currentItem = new AtomicInteger(currentSet.get() + 1);

        ImageView petImage = findViewById(R.id.imageView);
        CircularProgressIndicator circularProgressIndicator = findViewById(R.id.progress);
        MaterialTextView title = findViewById(R.id.title);
        MaterialTextView description = findViewById(R.id.description);
        MaterialButton next = findViewById(R.id.next);
        MaterialButton skip = findViewById(R.id.skip);
        SpringDotsIndicator springDotsIndicator = findViewById(R.id.spring_dots_indicator);

        petImage.setImageDrawable(getResources().getDrawable(dataItems.get(currentSet.get()).getDrawable()));
        int progress = (currentItem.get() /dataItems.size()) * 100;
        //circularProgressIndicator.setProgress(progress, true);
        title.setText(dataItems.get(currentSet.get()).getTitle());
        description.setText(dataItems.get(currentSet.get()).getDescription());
        for (DataItem item:
             dataItems) {
            springDotsIndicator.addDot(0);
        }

        skip.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        AtomicBoolean finished = new AtomicBoolean(false);

        next.setOnClickListener(v -> {
            if(finished.get()){
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }else{
                currentSet.getAndIncrement(); currentItem.getAndIncrement();
                DataItem newItem = dataItems.get(currentSet.get());
                springDotsIndicator.removeDot();
                springDotsIndicator.refreshDots();

                petImage.setImageDrawable(getResources().getDrawable(newItem.getDrawable()));
                int progres = (currentItem.get() /dataItems.size()) * 100;
                //circularProgressIndicator.setProgress(progres, true);
                title.setText(newItem.getTitle());
                description.setText(newItem.getDescription());

                if(currentItem.get() == 3){
                    finished.set(true);
                }
            }

        });
    }
}

class DataItem{
    private String title, description;
    private int drawable;

    public DataItem(String title, String description, int drawable){
        this.title = title;
        this.description = description;
        this.drawable = drawable;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public int getDrawable() {
        return drawable;
    }
}