package com.example.mingle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.mingle.R;
import com.example.mingle.databinding.ListelemeSayfasiActivityBinding;
import com.example.mingle.girisactivity.GirisEkraniActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ListelemeSayfasiActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private ListelemeSayfasiActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ListelemeSayfasiActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        toolbar = binding.listelemeSayfasiActivityToolBar;
        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();

        binding.kitapCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OkudugumKitaplarActivity.class));
            }
        });

        binding.izledigimFilmler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FilmlerActivity.class));
            }
        });

        binding.listelemeSayfasiCardViewAnilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AnilarActivity.class));
            }
        });

        binding.gezdigimYerler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GezdigimYerlerActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cikis_yap_menusu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cikis_yap_menusu_cikis_yap_tusu) {
            firebaseAuth.signOut();
            startActivity(new Intent(getApplicationContext(), GirisEkraniActivity.class));
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

}