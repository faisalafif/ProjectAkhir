package com.faislll.projectakhir.aktivitas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.faislll.projectakhir.R;
import com.faislll.projectakhir.adapter.CommentAdapter;
import com.faislll.projectakhir.fragment.FragmentCari;
import com.faislll.projectakhir.fragment.FragmentHome;
import com.faislll.projectakhir.fragment.FragmentProfile;
import com.faislll.projectakhir.fragment.FragmentSuka;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment fragmentAktif;
    public static final String DATA_UID = "PREF_UID";
    public static final String KEY = "idprofile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedNavigasi);

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String pengapload = intent.getString(CommentAdapter.ID_PUBRISHER);
            SharedPreferences.Editor editor = getSharedPreferences(DATA_UID, MODE_PRIVATE).edit();
            editor.putString(KEY, pengapload);
            editor.apply();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment, new FragmentProfile()).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment, new FragmentHome()).commit();
        }

    }

    private void init() {
        bottomNavigationView = findViewById(R.id.navigasi_bawah);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedNavigasi =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home_navigasi_bawah:
                            fragmentAktif = new FragmentHome();
                            break;
                        case R.id.suka_navigasi_bawah:
                            fragmentAktif = new FragmentSuka();
                            break;
                        case R.id.cari_navigasi_bawah:
                            fragmentAktif = new FragmentCari();
                            break;
                        case R.id.add_navigasi_bawah:
                            fragmentAktif = null;
                            startActivity(new Intent(MainActivity.this, AktivityAdd.class));
                            break;
                        case R.id.profile_navigasi_bawah:
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences(DATA_UID, MODE_PRIVATE).edit();
                            editor.putString(KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            fragmentAktif = new FragmentProfile();
                            break;
                    }

                    if (fragmentAktif != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_fragment, fragmentAktif).commit();
                    }
                    return true;
                }
            };
}