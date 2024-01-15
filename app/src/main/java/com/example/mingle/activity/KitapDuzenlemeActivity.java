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
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.R;
import com.example.mingle.databinding.AniDuzenlemeActivityBinding;
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

public class KitapDuzenlemeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Intent data;
    private KitapDuzenlemeActivityBinding binding;
    private Uri imageData;
    private String kitapId;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = KitapDuzenlemeActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.kitapDuzenlemeActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);


        gorselDegistir();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = firebaseStorage.getReference();

        data = getIntent();
        kitapId = data.getStringExtra("kitapId");

        binding.kitapDuzenlemeActivityEditTextKitapIsmi.setText(data.getStringExtra("kitapAdi"));
        binding.kitapDuzenlemeActivityEditTextKitapOzeti.setText(data.getStringExtra("kitapOzeti"));
        binding.kitapDuzenlemeActivityEditTextKitapYazari.setText(data.getStringExtra("kitapYazari"));
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.kitapDuzenlemeActivityImageViewKitapResmi);
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.kitapDuzenlemeActivityYildizlar.setRating(kullaniciPuani);

        binding.kiatpDuzenlemeActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Güncelleme işlemi burada gerçekleştirilecek
                String kitapAdi = binding.kitapDuzenlemeActivityEditTextKitapIsmi.getText().toString();
                String kitapOzeti = binding.kitapDuzenlemeActivityEditTextKitapOzeti.getText().toString();
                String kitapYazari = binding.kitapDuzenlemeActivityEditTextKitapYazari.getText().toString();
                String kullaniciPuani = String.valueOf(binding.kitapDuzenlemeActivityYildizlar.getRating());
                String userId = firebaseUser.getUid();

                if (kitapAdi.isEmpty() || kitapOzeti.isEmpty() ||kitapYazari.isEmpty()){
                    Toast.makeText(KitapDuzenlemeActivity.this, "Lütfen istenilen bilgileri doldurunuz", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.kitapDuzenlemeActivityProgressBar.setVisibility(View.VISIBLE);

                    // Eğer yeni bir görsel seçilmediyse, mevcut görsel URL'sini kullan
                    if (imageData == null) {
                        updateAni(kitapId, kitapAdi, kitapOzeti, kitapYazari,kullaniciPuani, userId, data.getStringExtra("downloadUrl"));
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

                                                        updateAni(kitapId, kitapAdi,kitapYazari, kitapOzeti, kullaniciPuani, userId, downloadUrl);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        binding.kitapDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(KitapDuzenlemeActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.kitapDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(KitapDuzenlemeActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }


                binding.kiatpDuzenlemeActivityFabEkleButonu.setClickable(false);
            }

        });

        binding.kitapDuzenlemeActivityImageViewKitapResmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        });

    }
    private void updateAni(String aniId, String kitapAdi,String kitapOzeti, String kitapYazari, String kullaniciPuani, String userId, String downloadUrl) {
        HashMap<String, Object> kitapVerisi = new HashMap<>();
        kitapVerisi.put("downloadurl", downloadUrl);
        kitapVerisi.put("kitapAdi", kitapAdi);
        kitapVerisi.put("kitapOzeti", kitapOzeti);
        kitapVerisi.put("kitapYazari", kitapYazari);
        kitapVerisi.put("kullaniciPuani", kullaniciPuani);
        kitapVerisi.put("userId", userId);

        firebaseFirestore.collection("okunan_kitaplar").document(aniId)
                .update(kitapVerisi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.kitapDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(KitapDuzenlemeActivity.this, "Okunan kitap güncellendi", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(KitapDuzenlemeActivity.this, OkudugumKitaplarActivity.class);
                        finish();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.kitapDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(KitapDuzenlemeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    public void gorselDegistir(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.kitapDuzenlemeActivityImageViewKitapResmi.setImageURI(imageData);
                    }
                }

            }
        });
    }
}