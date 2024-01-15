package com.example.mingle.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.R;
import com.example.mingle.databinding.FilmDuzenleActivityBinding;
import com.example.mingle.databinding.KitapDuzenlemeActivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class FilmDuzenleActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Intent data;
    private FilmDuzenleActivityBinding binding;
    private Uri imageData;
    private String filmId;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FilmDuzenleActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.filmDuzenlemeActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        gorselDegistir();

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = firebaseStorage.getReference();

        data = getIntent();
        filmId = data.getStringExtra("filmId");


        binding.filmDuzenlemeActivityEditTextFilmIsmi.setText(data.getStringExtra("filmAdi"));
        binding.filmDuzenlemeActivityEditTextFilmOzeti.setText(data.getStringExtra("filmOzeti"));
        binding.filmDuzenlemeActivityEditTextFilmYonetmeni.setText(data.getStringExtra("filmYonetmeni"));
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.filmDuzenlemeActivityImageViewFilmResmi);
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.filmDuzenlemeActivityYildizlar.setRating(kullaniciPuani);

        binding.filmDuzenlemeActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Güncelleme işlemi burada gerçekleştirilecek
                String filmAdi = binding.filmDuzenlemeActivityEditTextFilmIsmi.getText().toString();
                String filmOzeti = binding.filmDuzenlemeActivityEditTextFilmOzeti.getText().toString();
                String filmYonetmeni = binding.filmDuzenlemeActivityEditTextFilmYonetmeni.getText().toString();
                String kullaniciPuani = String.valueOf(binding.filmDuzenlemeActivityYildizlar.getRating());
                String userId = firebaseUser.getUid();

                if (filmAdi.isEmpty() || filmOzeti.isEmpty() ||filmYonetmeni.isEmpty()){
                    Toast.makeText(FilmDuzenleActivity.this, "Lütfen istenilen bilgileri doldurunuz", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.filmDuzenlemeActivityProgressBar.setVisibility(View.VISIBLE);

                    // Eğer yeni bir görsel seçilmediyse, mevcut görsel URL'sini kullan
                    if (imageData == null) {
                        updateAni(filmId, filmAdi, filmOzeti, filmYonetmeni,kullaniciPuani, userId, data.getStringExtra("downloadUrl"));
                    } else {
                        // Yeni bir görsel seçildiyse, görseli yükle ve güncelle
                        UUID uuid = UUID.randomUUID();
                        String imgName = "film_gorselleri/" + uuid + ".jpg";

                        storageReference.child(imgName).putFile(imageData)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        StorageReference newReference = firebaseStorage.getReference(imgName);
                                        newReference.getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadUrl = uri.toString();

                                                        updateAni(filmId, filmAdi, filmOzeti, filmYonetmeni,kullaniciPuani, userId, data.getStringExtra("downloadUrl"));
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        binding.filmDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(FilmDuzenleActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.filmDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(FilmDuzenleActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }


                binding.filmDuzenlemeActivityFabEkleButonu.setClickable(false);
            }
        });
    }
    private void updateAni(String filmId, String filmAdi,String filmOzeti, String filmYonetmeni, String kullaniciPuani, String userId, String downloadUrl) {
        HashMap<String, Object> kitapVerisi = new HashMap<>();
        kitapVerisi.put("downloadurl", downloadUrl);
        kitapVerisi.put("filmAdi", filmAdi);
        kitapVerisi.put("filmOzeti", filmOzeti);
        kitapVerisi.put("filmYonetmeni", filmYonetmeni);
        kitapVerisi.put("kullaniciPuani", kullaniciPuani);
        kitapVerisi.put("userId", userId);

        firebaseFirestore.collection("filmler").document(filmId)
                .update(kitapVerisi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.filmDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(FilmDuzenleActivity.this, "İzlenen film güncellendi", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FilmDuzenleActivity.this, FilmlerActivity.class);
                        finish();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.filmDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(FilmDuzenleActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void gorselDegistir(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.filmDuzenlemeActivityImageViewFilmResmi.setImageURI(imageData);
                    }
                }

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish(); // Şu anki aktiviteyi sonlandır
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Şu anki aktiviteyi sonlandır

    }
}