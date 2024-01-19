package com.example.mingle.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mingle.databinding.GezdigimYerlerActivityBinding;

public class GezdigimYerlerActivity extends AppCompatActivity {

    private GezdigimYerlerActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GezdigimYerlerActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.gezdigimYerlerActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GezdigimYerlerActivity.this, GezdigimYerlerEkleActvity.class));
            }
        });
    }
}