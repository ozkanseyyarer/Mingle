package com.example.mingle.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mingle.databinding.AniDuzenlemeActivityBinding;
import com.example.mingle.post.AniPost;
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

public class AniDuzenlemeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Intent data;
    private AniDuzenlemeActivityBinding binding;
    private Uri imageData;
    private String aniId;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AniDuzenlemeActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.aniDuzenlemeActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        gorselDegistir();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = firebaseStorage.getReference();

        data = getIntent();
        aniId = data.getStringExtra("id");

        binding.aniDuzenlemeActivityEditTextAniIsmi.setText(data.getStringExtra("aniBaslik"));
        binding.aniDuzenlemeActivityEditTextaniOzeti.setText(data.getStringExtra("aniOzeti"));
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.aniDuzenlemeActivityImageViewAniResmi);
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.aniDuzenlemeActivityYildizlar.setRating(kullaniciPuani);

        binding.aniDuzenlemeActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Güncelleme işlemi burada gerçekleştirilecek
                String aniBaslik = binding.aniDuzenlemeActivityEditTextAniIsmi.getText().toString();
                String aniOzeti = binding.aniDuzenlemeActivityEditTextaniOzeti.getText().toString();
                String kullaniciPuani = String.valueOf(binding.aniDuzenlemeActivityYildizlar.getRating());
                String userId = firebaseUser.getUid();

                if (aniBaslik.isEmpty() || aniOzeti.isEmpty()) {
                    Toast.makeText(AniDuzenlemeActivity.this, "Lütfen istenilen bilgileri doldurunuz", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.aniDuzenlemeActivityProgressBar.setVisibility(View.VISIBLE);

                    // Eğer yeni bir görsel seçilmediyse, mevcut görsel URL'sini kullan
                    if (imageData == null) {
                        updateAni(aniId, aniBaslik, aniOzeti, kullaniciPuani, userId, data.getStringExtra("downloadUrl"));
                    } else {
                        // Yeni bir görsel seçildiyse, görseli yükle ve güncelle
                        UUID uuid = UUID.randomUUID();
                        String imgName = "anigorselleri/" + uuid + ".jpg";

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

                                                        updateAni(aniId, aniBaslik, aniOzeti, kullaniciPuani, userId, downloadUrl);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        binding.aniDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(AniDuzenlemeActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.aniDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AniDuzenlemeActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                binding.aniDuzenlemeActivityFabEkleButonu.setClickable(false);
            }
        });

        binding.aniDuzenlemeActivityImageViewAniResmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
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
    public void gorselDegistir(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.aniDuzenlemeActivityImageViewAniResmi.setImageURI(imageData);
                    }
                }

            }
        });
    }
    private void updateAni(String aniId, String aniBaslik, String aniOzeti, String kullaniciPuani, String userId, String downloadUrl) {
        HashMap<String, Object> aniVerisi = new HashMap<>();
        aniVerisi.put("downloadurl", downloadUrl);
        aniVerisi.put("aniBaslik", aniBaslik);
        aniVerisi.put("aniOzeti", aniOzeti);
        aniVerisi.put("kullaniciPuani", kullaniciPuani);
        aniVerisi.put("userId", userId);

        firebaseFirestore.collection("anilar").document(aniId)
                .update(aniVerisi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.aniDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AniDuzenlemeActivity.this, "Anı güncellendi", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AniDuzenlemeActivity.this, AnilarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.aniDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AniDuzenlemeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
