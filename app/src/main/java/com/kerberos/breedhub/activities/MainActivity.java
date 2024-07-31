package com.kerberos.breedhub.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.kerberos.breedhub.R;
import com.kerberos.breedhub.fragments.AnalysisFragment;
import com.kerberos.breedhub.fragments.ChatFragment;
import com.kerberos.breedhub.fragments.FavoritesFragment;
import com.kerberos.breedhub.fragments.HomeFragment;
import com.kerberos.breedhub.fragments.MapFragment;
import com.kerberos.breedhub.fragments.NearbyFragment;
import com.kerberos.breedhub.fragments.PetFragment;
import com.kerberos.breedhub.fragments.ProfileFragment;
import com.kerberos.breedhub.fragments.VetBreedingFragment;
import com.kerberos.breedhub.fragments.VetClinicFragment;
import com.kerberos.breedhub.toolkit.TinyDB;
import com.kerberos.breedhub.views.NotificationDialog;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    public static String getCountry(Context context) {
        Configuration config = context.getResources().getConfiguration();
        Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = config.getLocales().get(0);
        } else {
            locale = config.locale;
        }
        return locale.getCountry();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyDB tinyDB = new TinyDB(this);
        tinyDB.putString("country", getCountry(this));
        boolean isVet = tinyDB.getBoolean("isVet");
        if (isVet) {
            setContentView(R.layout.vet_activity_home);
        }else{
            setContentView(R.layout.activity_main);
        }


        //MaterialToolbar materialToolbar = findViewById(R.id.materialToolbar);

//        materialToolbar.findViewById(R.id.share).setOnClickListener(v -> {
//            String packageName = getPackageName();
//            String appLink = "https://play.google.com/store/apps/details?id=" + packageName;
//            String shareText = "Check out this cool app: " + appLink;
//
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BreedHub");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
//
//            startActivity(Intent.createChooser(shareIntent, "Share via"));
//        });
//
//        materialToolbar.findViewById(R.id.exit).setOnClickListener(v -> {
//            finishAffinity();
//        });
//
//        materialToolbar.findViewById(R.id.logout).setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        });

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (isVet) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.Home) {
                        loadFragment(new VetBreedingFragment());
                        return true;
                    } else if (itemId == R.id.Analysis) {
                        loadFragment(new AnalysisFragment());
                        return true;
                    } else if (itemId == R.id.Profile) {
                        loadFragment(new ProfileFragment());
                        return true;
                    }

                    return false;
                }
            });
        } else {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.Home) {
                        loadFragment(new HomeFragment());
                        return true;
                    } else if (itemId == R.id.Favorites) {
                        loadFragment(new FavoritesFragment());
                        return true;
                    } else if (itemId == R.id.Location) {
                        loadFragment(new MapFragment());
                        return true;
                    } else if (itemId == R.id.Profile) {
                        loadFragment(new ProfileFragment());
                        return true;
                    } else if (itemId == R.id.Chat) {
                        loadFragment(new ChatFragment());
                        return true;
                    }

                    return false;
                }
            });
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.Home);
        }
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragHolder, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            // Update BottomNavigationView based on the current fragment
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragHolder);
            if (currentFragment instanceof HomeFragment) {
                bottomNavigationView.setSelectedItemId(R.id.Home);
            } else if (currentFragment instanceof NearbyFragment) {
                bottomNavigationView.setSelectedItemId(R.id.Favorites);
            } else if (currentFragment instanceof FavoritesFragment) {
                bottomNavigationView.setSelectedItemId(R.id.Chat);
            }else if (currentFragment instanceof MapFragment) {
                bottomNavigationView.setSelectedItemId(R.id.Location);
            }else if (currentFragment instanceof ProfileFragment) {
                bottomNavigationView.setSelectedItemId(R.id.Profile);
            }
        } else {
            super.onBackPressed();
        }
    }
}
