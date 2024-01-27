package com.example.mingle.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mingle.R;
import com.example.mingle.databinding.FilmDetayActivityBinding;
import com.example.mingle.databinding.GezdigimYerlerDetayActivityBinding;
import com.squareup.picasso.Picasso;

public class GezdigimYerlerDetayActivity extends AppCompatActivity {

    private GezdigimYerlerDetayActivityBinding binding;
    private String latitute;
    private String longitute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GezdigimYerlerDetayActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        Intent data = getIntent();
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.imageViewYerResmi);
        binding.textViewYerIsmi.setText(data.getStringExtra("yerAdi"));
        binding.textViewYerOzeti.setText(data.getStringExtra("yerYorumu"));
        binding.textViewGun.setText(data.getStringExtra("tarihGun"));
        binding.textViewAy.setText(data.getStringExtra("tarihAy"));
        binding.textViewYil.setText(data.getStringExtra("tarihYil"));
        binding.textViewSaat.setText(data.getStringExtra("zamanSaat"));
        binding.textViewDakika.setText(data.getStringExtra("zamanDakika"));
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.ratingBarYildizlar.setRating(kullaniciPuani);


        binding.linearLayoutKonumEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GezdigimYerlerDetayActivity.this, MapsActivity.class);

                intent.putExtra("latitude", data.getStringExtra("latitude"));
                intent.putExtra("longitude", data.getStringExtra("longitude"));
                intent.putExtra("eskimiyenimi", "eski");
                startActivity(intent);

            }
        });

        binding.fabDuzenleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GezdigimYerlerDuzenlemeActivity.class);
                intent.putExtra("downloadUrl", data.getStringExtra("downloadUrl"));
                intent.putExtra("yerAdi", data.getStringExtra("yerAdi"));
                intent.putExtra("yerYorumu", data.getStringExtra("yerYorumu"));
                intent.putExtra("tarihGun", data.getStringExtra("tarihGun"));
                intent.putExtra("tarihAy", data.getStringExtra("tarihAy"));
                intent.putExtra("tarihYil",data.getStringExtra("tarihYil") );
                intent.putExtra("zamanSaat",data.getStringExtra("zamanSaat") );
                intent.putExtra("zamanDakika",data.getStringExtra("zamanDakika") );
                intent.putExtra("kullaniciPuani",data.getStringExtra("kullaniciPuani"));
                intent.putExtra("latitude", data.getStringExtra("latitude"));
                intent.putExtra("longitude", data.getStringExtra("longitude"));
                intent.putExtra("yerId", data.getStringExtra("yerId"));
                v.getContext().startActivity(intent);
            }
        });


    }
}