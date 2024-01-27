package com.example.mingle.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mingle.R;
import com.example.mingle.databinding.GezdigimYerlerDuzenlemeActivityBinding;
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

public class GezdigimYerlerDuzenlemeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Intent data;
    private GezdigimYerlerDuzenlemeActivityBinding binding;
    private Uri imageData;
    private String yerId;

    private int yil, ay, gun, saat, dakika;
    private String eklenecekDakika, eklenecekSaat, eklenecekAy, eklenecekGun;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GezdigimYerlerDuzenlemeActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.gezdigimYerlerDuzenlemeActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);


        gorselDegistir();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = firebaseStorage.getReference();

        data = getIntent();
        yerId = data.getStringExtra("yerId");
        Picasso.get().load(data.getStringExtra("downloadUrl")).into(binding.gezdigimYerlerDuzenlemeActivityImageViewKitapResmi);
        binding.gezdigimYerlerDuzenlemeActivityEditTextKitapIsmi.setText(data.getStringExtra("yerAdi"));
        binding.gezdigimYerlerDuzenlemeActivityEditTextKitapOzeti.setText(data.getStringExtra("yerYorumu"));
        binding.textViewGun.setText(data.getStringExtra("tarihGun"));
        binding.textViewAy.setText(data.getStringExtra("tarihAy"));
        binding.textViewYil.setText(data.getStringExtra("tarihYil"));
        binding.textViewSaat.setText(data.getStringExtra("zamanSaat"));
        binding.textViewDakika.setText(data.getStringExtra("zamanDakika"));
        float kullaniciPuani = Float.parseFloat(data.getStringExtra("kullaniciPuani"));
        binding.gezdigimYerlerDuzenlemeActivityYildizlar.setRating(kullaniciPuani);

        ay = Integer.parseInt(data.getStringExtra("tarihAy"));
        gun = Integer.parseInt(data.getStringExtra("tarihGun"));
        yil = Integer.parseInt(data.getStringExtra("tarihYil"));
        saat = Integer.parseInt(data.getStringExtra("zamanSaat"));
        dakika = Integer.parseInt(data.getStringExtra("zamanDakika"));
        binding.linearLayoutTarihEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog takvim = new DatePickerDialog(GezdigimYerlerDuzenlemeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int zaman, int dayOfMonth) {


                        if (zaman == 0) eklenecekAy = "01";
                        else if (zaman == 1) eklenecekAy = "02";
                        else if (zaman == 2) eklenecekAy = "03";
                        else if (zaman == 3) eklenecekAy = "04";
                        else if (zaman == 4) eklenecekAy = "05";
                        else if (zaman == 5) eklenecekAy = "06";
                        else if (zaman == 6) eklenecekAy = "07";
                        else if (zaman == 7) eklenecekAy = "08";
                        else if (zaman == 8) eklenecekAy = "09";
                        else if (zaman == 9) eklenecekAy = "10";
                        else eklenecekAy = String.valueOf(zaman + 1);


                        if (dayOfMonth == 1) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 2) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 3) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 4) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 5) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 6) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 7) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 8) eklenecekGun = "0" + dayOfMonth;
                        else if (dayOfMonth == 9) eklenecekGun = "0" + dayOfMonth;
                        else eklenecekGun = String.valueOf(dayOfMonth);
                        yil = year;
                        gun = dayOfMonth;
                        ay = zaman + 1;
                        binding.textViewAy.setText(eklenecekAy);
                        binding.textViewGun.setText(String.valueOf(eklenecekGun));
                        binding.textViewYil.setText(String.valueOf(year));
                    }
                }, yil, ay - 1, gun);
                takvim.show();

            }
        });
        binding.linearLayoutSaatEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TimePickerDialog zaman = new TimePickerDialog(GezdigimYerlerDuzenlemeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay == 0) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 1) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 2) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 3) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 4) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 5) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 6) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 7) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 8) eklenecekSaat = "0" + hourOfDay;
                        else if (hourOfDay == 9) eklenecekSaat = "0" + hourOfDay;
                        else eklenecekSaat = String.valueOf(hourOfDay);


                        if (minute == 0) eklenecekDakika = "0" + minute;
                        else if (minute == 1) eklenecekDakika = "0" + minute;
                        else if (minute == 2) eklenecekDakika = "0" + minute;
                        else if (minute == 3) eklenecekDakika = "0" + minute;
                        else if (minute == 4) eklenecekDakika = "0" + minute;
                        else if (minute == 5) eklenecekDakika = "0" + minute;
                        else if (minute == 6) eklenecekDakika = "0" + minute;
                        else if (minute == 7) eklenecekDakika = "0" + minute;
                        else if (minute == 8) eklenecekDakika = "0" + minute;
                        else if (minute == 9) eklenecekDakika = "0" + minute;
                        else eklenecekDakika = String.valueOf(minute);
                        saat = hourOfDay;
                        dakika = minute;

                        binding.textViewSaat.setText(eklenecekSaat);
                        binding.textViewDakika.setText(eklenecekDakika);
                    }
                }, saat, dakika, true);

                zaman.setButton(DatePickerDialog.BUTTON_POSITIVE, "Tamam", zaman);
                zaman.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", zaman);

                zaman.show();
            }
        });

        binding.gezdigimYerlerDuzenlemeActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Güncelleme işlemi burada gerçekleştirilecek
                String yerAdi = binding.gezdigimYerlerDuzenlemeActivityEditTextKitapIsmi.getText().toString();
                String yerYorumu = binding.gezdigimYerlerDuzenlemeActivityEditTextKitapOzeti.getText().toString();
                String tarihGun = binding.textViewGun.getText().toString();
                String tarihAy = binding.textViewAy.getText().toString();
                String tarihYil = binding.textViewYil.getText().toString();
                String zamanSaat = binding.textViewSaat.getText().toString();
                String zamanDakika = binding.textViewDakika.getText().toString();
                String kullaniciPuani = String.valueOf(binding.gezdigimYerlerDuzenlemeActivityYildizlar.getRating());
                String userId = firebaseUser.getUid();

                if (yerAdi.isEmpty() || yerYorumu.isEmpty()) {
                    Toast.makeText(GezdigimYerlerDuzenlemeActivity.this, "Lütfen istenilen bilgileri doldurunuz", Toast.LENGTH_SHORT).show();
                } else {
                    binding.gezdigimYerlerDuzenlemeActivityProgressBar.setVisibility(View.VISIBLE);

                    // Eğer yeni bir görsel seçilmediyse, mevcut görsel URL'sini kullan
                    if (imageData == null) {
                        updateVeri(yerAdi, yerYorumu, tarihGun, tarihAy, tarihYil, zamanSaat, zamanDakika, kullaniciPuani, userId, data.getStringExtra("downloadUrl"));
                    } else {
                        // Yeni bir görsel seçildiyse, görseli yükle ve güncelle
                        UUID uuid = UUID.randomUUID();
                        String imgName = "gezilen_yer_resimleri/" + uuid + ".jpg";

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

                                                        updateVeri(yerAdi, yerYorumu, tarihGun, tarihAy, tarihYil, zamanSaat, zamanDakika, kullaniciPuani, userId, downloadUrl);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        binding.gezdigimYerlerDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(GezdigimYerlerDuzenlemeActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.gezdigimYerlerDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(GezdigimYerlerDuzenlemeActivity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }


                binding.gezdigimYerlerDuzenlemeActivityFabEkleButonu.setClickable(false);
            }

        });

        binding.gezdigimYerlerDuzenlemeActivityImageViewKitapResmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        });


    }

    private void updateVeri(String yerAdi, String yerYorumu, String tarihGun, String tarihAy, String tarihYil, String zamanSaat, String zamanDakika, String kullaniciPuani, String userId, String downloadUrl) {
        HashMap<String, Object> yerVerisi = new HashMap<>();
        yerVerisi.put("downloadurl", downloadUrl);
        yerVerisi.put("yerAdi", yerAdi);
        yerVerisi.put("yerYorumu", yerYorumu);
        yerVerisi.put("tarihGun", tarihGun);
        yerVerisi.put("tarihAy", tarihAy);
        yerVerisi.put("tarihYil", tarihYil);
        yerVerisi.put("zamanSaat", zamanSaat);
        yerVerisi.put("zamanDakika", zamanDakika);
        yerVerisi.put("kullaniciPuani", kullaniciPuani);
        yerVerisi.put("userId", userId);

        firebaseFirestore.collection("gezilen_yerler").document(yerId)
                .update(yerVerisi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.gezdigimYerlerDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(GezdigimYerlerDuzenlemeActivity.this, "Güncellendi", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GezdigimYerlerDuzenlemeActivity.this, GezdigimYerlerActivity.class);
                        finish();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.gezdigimYerlerDuzenlemeActivityProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(GezdigimYerlerDuzenlemeActivity.this, "Düzenleme başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void gorselDegistir() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.gezdigimYerlerDuzenlemeActivityImageViewKitapResmi.setImageURI(imageData);
                    }
                }

            }
        });
    }


}