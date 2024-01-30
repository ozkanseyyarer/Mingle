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

import com.example.mingle.R;
import com.example.mingle.adapter.FilmPostAdapter;
import com.example.mingle.adapter.KitapPostAdapter;
import com.example.mingle.databinding.FilmlerActivityBinding;
import com.example.mingle.databinding.ListelemeSayfasiActivityBinding;
import com.example.mingle.post.FilmPost;
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

public class FilmlerActivity extends AppCompatActivity {

    FilmlerActivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<FilmPost> filmPostArrayList;
    private FilmPostAdapter filmPostAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FilmlerActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.filmlerActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        filmPostArrayList = new ArrayList<>();
        binding.filmlerActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FilmEkleActivity.class));
            }
        });

        if (firebaseUser != null) {
            verileriGetir(firebaseUser.getUid());
        }

        binding.filmlerActivityRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS); // Boşlukları daha dengeli dağıtmak için


        binding.filmlerActivityRecyclerView.setLayoutManager(layoutManager);

        filmPostAdapter = new FilmPostAdapter(getApplicationContext(),filmPostArrayList);
        binding.filmlerActivityRecyclerView.setAdapter(filmPostAdapter);


    }

    //...

    private void verileriGetir(String userId) {

        filmPostArrayList.clear();

        firebaseFirestore.collection("filmler")
                .whereEqualTo("userId", userId)
                .orderBy("tarih", Query.Direction.DESCENDING)  // "tarih" alanına göre azalan sırayla
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            error.printStackTrace();
                            Toast.makeText(FilmlerActivity.this, "Veri alınamadı: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println("Hata mesajı: " + error.getMessage());
                        }

                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();

                                String filmAdi = (String) data.get("filmAdi");
                                String downloadUrl = (String) data.get("downloadurl");
                                String filmOzeti = (String) data.get("filmOzeti");
                                String filmYonetmeni = (String) data.get("filmYonetmeni");
                                String kullaniciPuani = (String) data.get("kullaniciPuani");
                                String filmId = (String) snapshot.getId();
                                FilmPost filmPost = new FilmPost(filmAdi, filmYonetmeni, filmOzeti, downloadUrl, kullaniciPuani, filmId);
                                filmPostArrayList.add(filmPost);
                            }
                            filmPostAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

//...


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