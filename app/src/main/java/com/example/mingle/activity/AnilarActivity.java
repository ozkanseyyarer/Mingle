package com.example.mingle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.R;
import com.example.mingle.adapter.AniPostAdapter;
import com.example.mingle.adapter.KitapPostAdapter;
import com.example.mingle.databinding.AnilarActivityBinding;
import com.example.mingle.databinding.OkudugumKitaplarActivityBinding;
import com.example.mingle.post.AniPost;
import com.example.mingle.post.KitapPost;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
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

public class AnilarActivity extends AppCompatActivity {
    AnilarActivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<AniPost> aniPostArrayList;
    private AniPostAdapter aniPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AnilarActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.anilarActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        aniPostArrayList = new ArrayList<>();

        binding.anilarActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AniEkleActivity.class));
            }
        });
        if (firebaseUser != null) {
            verileriGetir(firebaseUser.getUid());
        }
        binding.anilarActivityRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS); // Boşlukları daha dengeli dağıtmak için
        binding.anilarActivityRecyclerView.setLayoutManager(layoutManager);
        aniPostAdapter = new AniPostAdapter(this, aniPostArrayList);
        binding.anilarActivityRecyclerView.setAdapter(aniPostAdapter);


    }

    private void verileriGetir(String userId) {

        aniPostArrayList.clear();
        //bu şekilde koleksiyon altındaki her şeyi alabiliriz.
        firebaseFirestore.collection("anilar").whereEqualTo("userId", userId).orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    error.printStackTrace(); // Hata detaylarını logla
                    Toast.makeText(AnilarActivity.this, "Veri alınamadı: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("hata mesajı : " + error.getMessage());
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        //verileri alma işlemir (dünüşüm işlemeli)
                        String aniBaslik = (String) data.get("aniBaslik");
                        String downloadUrl = (String) data.get("downloadurl");
                        String aniOzeti = (String) data.get("aniOzeti");
                        String kullaniciPuani = (String) data.get("kullaniciPuani");
                        String aniId = (String) snapshot.getId();
                        AniPost aniPost = new AniPost(aniBaslik, aniOzeti, downloadUrl, kullaniciPuani, aniId);
                        aniPostArrayList.add(aniPost);
                    }
                    //  aniPostAdapter.notifyDataSetChanged();
                    aniPostAdapter = new AniPostAdapter(getApplicationContext(), aniPostArrayList);
                    binding.anilarActivityRecyclerView.setAdapter(aniPostAdapter);
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