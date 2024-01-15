package com.example.mingle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.adapter.KitapPostAdapter;
import com.example.mingle.databinding.OkudugumKitaplarActivityBinding;
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

public class OkudugumKitaplarActivity extends AppCompatActivity {

    private OkudugumKitaplarActivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<KitapPost>  kitapPostArrayList;
    private KitapPostAdapter kitapPostAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OkudugumKitaplarActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.okudugumKitaplarActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        kitapPostArrayList = new ArrayList<>();

        binding.okudugumKitaplarActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), KitapEkleActivity.class));
            }
        });

        if (firebaseUser != null) {
            verileriGetir(firebaseUser.getUid());
        }
        binding.okudugumKitaplarActivityRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE); // Boşlukları daha dengeli dağıtmak için
        binding.okudugumKitaplarActivityRecyclerView.setLayoutManager(layoutManager);
        kitapPostAdapter = new KitapPostAdapter(getApplicationContext(),kitapPostArrayList);
        binding.okudugumKitaplarActivityRecyclerView.setAdapter(kitapPostAdapter);



    }

    private void verileriGetir(String userId) {

        kitapPostArrayList.clear();
        //bu şekilde koleksiyon altındaki her şeyi alabiliriz.
        firebaseFirestore.collection("okunan_kitaplar").whereEqualTo("userId", userId).orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    error.printStackTrace(); // Hata detaylarını logla
                    Toast.makeText(OkudugumKitaplarActivity.this, "Veri alınamadı: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("hata mesajı : "+error.getMessage());
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        //verileri alma işlemir (dünüşüm işlemeli)
                        String kitapAdi = (String) data.get("kitapAdi");
                        String downloadUrl = (String) data.get("downloadurl");
                        String kitapOzeti = (String) data.get("kitapOzeti");
                        String kitapYazari = (String) data.get("kitapYazari");
                        String kullaniciPuani = (String) data.get("kullaniciPuani");
                        String kitapId = (String) snapshot.getId();
                        KitapPost kitapPost = new KitapPost(kitapAdi,kitapOzeti,kitapYazari,downloadUrl,kullaniciPuani,kitapId);
                        kitapPostArrayList.add(kitapPost);
                    }
                    kitapPostAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Intent intent = new Intent(getApplicationContext(),ListelemeSayfasiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Diğer aktiviteleri temizle
            startActivity(intent);
            finish(); // Şu anki aktiviteyi sonlandır
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),ListelemeSayfasiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Diğer aktiviteleri temizle
        startActivity(intent);
        finish(); // Şu anki aktiviteyi sonlandır

    }

}