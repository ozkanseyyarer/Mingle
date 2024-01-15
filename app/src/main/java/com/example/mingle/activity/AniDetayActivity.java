package com.example.mingle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.mingle.databinding.AniDetayActivityBinding;
import com.squareup.picasso.Picasso;

public class AniDetayActivity extends AppCompatActivity {
    AniDetayActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AniDetayActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.aniDetayActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        Intent data = getIntent();

        binding.aniDetayActivityTextViewAniIsmi.setText(data.getStringExtra("aniBaslik"));
        binding.aniDetayActivityTextViewAniOzeti.setText(data.getStringExtra("aniOzeti"));
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.aniDetayActivityImageViewAniResmi);
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.aniDetayActivityYildizlar.setRating(kullaniciPuani);

        binding.aniDetayActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AniDuzenlemeActivity.class);
                intent.putExtra("downloadUrl", data.getStringExtra("downloadUrl"));
                intent.putExtra("aniBaslik", data.getStringExtra("aniBaslik"));
                intent.putExtra("aniOzeti", data.getStringExtra("aniOzeti"));
                intent.putExtra("kullaniciPuani", data.getStringExtra("kullaniciPuani"));
                intent.putExtra("id",data.getStringExtra("aniId") );
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    }

}