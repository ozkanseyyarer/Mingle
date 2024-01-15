package com.example.mingle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.mingle.R;
import com.example.mingle.databinding.FilmDetayActivityBinding;
import com.example.mingle.databinding.KitapDetayActivityBinding;
import com.squareup.picasso.Picasso;

public class FilmDetayActivity extends AppCompatActivity {

    private FilmDetayActivityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FilmDetayActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.filmDetayActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        Intent data = getIntent();

        binding.filmDetayActivityTextViewFilmIsmi.setText(data.getStringExtra("filmAdi"));
        binding.filmDetayActivityTextViewFilmOzeti.setText(data.getStringExtra("filmOzeti"));
        binding.filmDetayActivityTextViewFilmYonetmeni.setText(data.getStringExtra("filmYonetmeni"));
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.filmDetayActivityImageViewFilmResmi);
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.kitapDetayActivityYildizlar.setRating(kullaniciPuani);

        binding.filmDetayActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), FilmDuzenleActivity.class);
                intent.putExtra("downloadUrl", data.getStringExtra("downloadUrl"));
                intent.putExtra("filmAdi", data.getStringExtra("filmAdi"));
                intent.putExtra("filmOzeti", data.getStringExtra("filmOzeti"));
                intent.putExtra("filmYonetmeni", data.getStringExtra("filmYonetmeni"));
                intent.putExtra("kullaniciPuani", data.getStringExtra("kullaniciPuani"));
                intent.putExtra("filmId",data.getStringExtra("filmId") );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Diğer aktiviteleri temizle
            finish(); // Şu anki aktiviteyi sonlandır
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Diğer aktiviteleri temizle
        finish(); // Şu anki aktiviteyi sonlandır

    }
}