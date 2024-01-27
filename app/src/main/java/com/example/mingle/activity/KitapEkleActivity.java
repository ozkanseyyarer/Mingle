package com.example.mingle.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.mingle.databinding.KitapEkleActivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class KitapEkleActivity extends AppCompatActivity {

    private KitapEkleActivityBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    Uri imageData;

    private float kullaniciOyu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = KitapEkleActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();


        setSupportActionBar(binding.kitapEkleActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);


        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseUser = auth.getCurrentUser();


        binding.kitapEkleActivityYildizlar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                kullaniciOyu = rating;
            }
        });

        binding.kitapEkleActivityImageViewKitapResmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(KitapEkleActivity.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(KitapEkleActivity.this, android.Manifest.permission.READ_MEDIA_IMAGES)) {
                            Snackbar.make(view, "İzin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                                }
                            }).show();
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);

                        }
                    } else {
                        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activityResultLauncher.launch(intentToGallery);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(KitapEkleActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(KitapEkleActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Snackbar.make(view, "İzin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                                }
                            }).show();
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                        }
                    } else {
                        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activityResultLauncher.launch(intentToGallery);
                    }
                }

            }
        });

        binding.kitapEkleActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String kitapAdi = binding.kitapEkleActivityEditTextKitapIsmi.getText().toString();
                String kitapYazari = binding.kitapEkleActivityEditTextKitapYazari.getText().toString();
                String kitapOzeti = binding.kitapEkleActivityEditTextKitapOzeti.getText().toString();
                String kullaniciPuani = String.valueOf(kullaniciOyu);
                String userId = firebaseUser.getUid();

                if (imageData == null || kitapAdi.isEmpty() || kitapYazari.isEmpty() || kitapOzeti.isEmpty() || kullaniciOyu == 0) {
                    binding.kitapEkleActivityFabEkleButonu.setClickable(true);
                    Toast.makeText(KitapEkleActivity.this, "Lütfen istenilen bilgileri doldurunuz", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.kitapEkleActivityFabEkleButonu.setClickable(false);

                    binding.kitapEkleActivityProgressBar.setVisibility(View.VISIBLE);
                    UUID uuid = UUID.randomUUID();
                    String imgName = "images/" + uuid + ".jpg";

                    storageReference.child(imgName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference newReference = firebaseStorage.getReference(imgName);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    HashMap<String, Object> kitapVerisi = new HashMap<>();

                                    kitapVerisi.put("downloadurl", downloadUrl);
                                    kitapVerisi.put("kitapAdi", kitapAdi);
                                    kitapVerisi.put("kitapYazari", kitapYazari);
                                    kitapVerisi.put("kitapOzeti", kitapOzeti);
                                    kitapVerisi.put("kullaniciPuani", kullaniciPuani);
                                    kitapVerisi.put("userId", userId);
                                    kitapVerisi.put("tarih", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection("okunan_kitaplar").add(kitapVerisi).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            binding.kitapEkleActivityFabEkleButonu.setClickable(false);

                                            Toast.makeText(KitapEkleActivity.this, "Kitap kaydedildi", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(KitapEkleActivity.this, OkudugumKitaplarActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            binding.kitapEkleActivityProgressBar.setVisibility(View.INVISIBLE);

                                            Toast.makeText(KitapEkleActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            binding.kitapEkleActivityFabEkleButonu.setClickable(true);
                                        }
                                    });

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.kitapEkleActivityProgressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(KitapEkleActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                            binding.kitapEkleActivityFabEkleButonu.setClickable(true);
                        }
                    });
                }



            }
        });


    }


    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.kitapEkleActivityImageViewKitapResmi.setImageURI(imageData);
                    }
                }

            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    //izin verildiği zaman girilecek yer
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                } else {
                    //izin verilmediği zaman gireceği yer

                    Toast.makeText(KitapEkleActivity.this, "Galeriye gitmemiz için izin gerekli", Toast.LENGTH_LONG).show();
                }
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