package com.example.mingle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.adapter.GezdigimYerlerPostAdapter;
import com.example.mingle.adapter.KitapPostAdapter;
import com.example.mingle.databinding.GezdigimYerlerActivityBinding;
import com.example.mingle.post.GezdigimYerlerPost;
import com.example.mingle.post.KitapPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GezdigimYerlerActivity extends AppCompatActivity {

    private GezdigimYerlerActivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<GezdigimYerlerPost> gezdigimYerlerPostArrayList;
    private GezdigimYerlerPostAdapter gezdigimYerlerPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GezdigimYerlerActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.gezdigimYerlerActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        gezdigimYerlerPostArrayList = new ArrayList<>();
        binding.gezdigimYerlerActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GezdigimYerlerActivity.this, GezdigimYerlerEkleActvity.class));
            }
        });

        if (firebaseUser != null) {
            verileriGetir(firebaseUser.getUid());
        }

        binding.gezdigimYerlerActivityRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE); // Boşlukları daha dengeli dağıtmak için
        binding.gezdigimYerlerActivityRecyclerView.setLayoutManager(layoutManager);
        gezdigimYerlerPostAdapter = new GezdigimYerlerPostAdapter(GezdigimYerlerActivity.this,gezdigimYerlerPostArrayList);
        binding.gezdigimYerlerActivityRecyclerView.setAdapter(gezdigimYerlerPostAdapter);


    }

    private void verileriGetir(String userId) {

        gezdigimYerlerPostArrayList.clear();
        //bu şekilde koleksiyon altındaki her şeyi alabiliriz.
        firebaseFirestore.collection("gezilen_yerler").whereEqualTo("userId", userId).orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    error.printStackTrace(); // Hata detaylarını logla
                    Toast.makeText(GezdigimYerlerActivity.this, "Veri alınamadı: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("hata mesajı : "+error.getMessage());
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        //verileri alma işlemir (dünüşüm işlemeli)
                        String yerAdi = (String) data.get("yerAdi");
                        String downloadUrl = (String) data.get("downloadurl");
                        String yerYorumu = (String) data.get("yerYorumu");
                        String tarihGun = (String) data.get("tarihGun");
                        String tarihAy = (String) data.get("tarihAy");
                        String tarihYil = (String) data.get("tarihYil");
                        String zamanSaat = (String) data.get("zamanSaat");
                        String zamanDakika = (String) data.get("zamanDakika");
                        String latitude = (String) data.get("latitude");
                        String longitude = (String) data.get("longitude");
                        String kullaniciPuani = (String) data.get("kullaniciPuani");
                        String yerId = (String)snapshot.getId();
                        GezdigimYerlerPost gezdigimYerlerPost = new GezdigimYerlerPost(downloadUrl,yerAdi,yerYorumu,tarihGun,tarihAy,tarihYil,zamanSaat,zamanDakika,latitude,longitude, kullaniciPuani,yerId);
                        gezdigimYerlerPostArrayList.add(gezdigimYerlerPost);
                    }
                    gezdigimYerlerPostAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), ListelemeSayfasiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Diğer aktiviteleri temizle
            startActivity(intent);
            finish(); // Şu anki aktiviteyi sonlandır
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ListelemeSayfasiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Diğer aktiviteleri temizle
        startActivity(intent);
        finish(); // Şu anki aktiviteyi sonlandır

    }
}