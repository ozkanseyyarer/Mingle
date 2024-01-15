package com.example.mingle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.mingle.R;
import com.example.mingle.databinding.KitapDetayActivityBinding;
import com.example.mingle.databinding.OkudugumKitaplarActivityBinding;
import com.squareup.picasso.Picasso;

public class KitapDetayActivity extends AppCompatActivity {

    KitapDetayActivityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = KitapDetayActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.kitapDetayActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        Intent data = getIntent();

        binding.kitapDetayActivityTextViewKitapIsmi.setText(data.getStringExtra("kitapAdi"));
        binding.kitapDetayActivityTextViewKitapOzeti.setText(data.getStringExtra("kitapOzeti"));
        binding.kitapDetayActivityTextViewKitapYazari.setText(data.getStringExtra("kitapYazari"));
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.kitapDetayActivityImageViewKitapResmi);
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.kitapDetayActivityYildizlar.setRating(kullaniciPuani);

        binding.kitapDetayActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), KitapDuzenlemeActivity.class);
                intent.putExtra("downloadUrl", data.getStringExtra("downloadUrl"));
                intent.putExtra("kitapAdi", data.getStringExtra("kitapAdi"));
                intent.putExtra("kitapOzeti", data.getStringExtra("kitapOzeti"));
                intent.putExtra("kitapYazari", data.getStringExtra("kitapYazari"));
                intent.putExtra("kullaniciPuani", data.getStringExtra("kullaniciPuani"));
                intent.putExtra("kitapId",data.getStringExtra("kitapId") );
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