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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mingle.R;
import com.example.mingle.databinding.GezdigimYerlerEkleActvityBinding;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class GezdigimYerlerEkleActvity extends AppCompatActivity {
    GezdigimYerlerEkleActvityBinding binding;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    Uri imageData;
    private int yil, ay, gun, saat, dakika;
    private String eklenecekDakika, eklenecekSaat, eklenecekAy, eklenecekGun;

    private float kullaniciOyu;
    private String gelenLatitude, gelenLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GezdigimYerlerEkleActvityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        anlikTarihiveZamaniAl();
        System.out.println(imageData);
        kullaniciOyu=0;


        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseUser = auth.getCurrentUser();

        registerLauncher();
        setSupportActionBar(binding.gezdigimYerlerEkleActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        Intent getIntent = getIntent();
        if (getIntent.hasExtra("latitude") && getIntent.hasExtra("longitude")) {
            // Intent'ten gelen latitude ve longitude bilgilerini al
            gelenLatitude = getIntent.getStringExtra("latitude");
            gelenLongitude = getIntent.getStringExtra("longitude");

            binding.gezdigimYerlerActivityTextViewKonumEkleYazisi.setText("Seçilmiş");
            binding.gezdigimYerlerActivityTextViewKonumEkleYazisi.setTextColor(ContextCompat.getColor(this, R.color.textViewVeEditTextYazirengi));

        }else{
            gelenLongitude="0.0";
            gelenLatitude="0.0";

        }

        binding.gezdigimYerlerEkleActivityYildizlar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                kullaniciOyu = rating;
            }
        });
        binding.gezdigimYerlerEkleActivityLinearLayoutTarihEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog takvim = new DatePickerDialog(GezdigimYerlerEkleActvity.this, new DatePickerDialog.OnDateSetListener() {
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
                        else eklenecekAy = String.valueOf(zaman+1);


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
                        binding.gezdigimYerlerEkleActivityTextViewAy.setText(eklenecekAy);
                        binding.gezdigimYerlerEkleActivityTextViewGun.setText(String.valueOf(eklenecekGun));
                        binding.gezdigimYerlerEkleActivityTextViewYil.setText(String.valueOf(year));
                    }
                }, yil, ay-1, gun);
                takvim.show();

            }
        });
        binding.gezdigimYerlerEkleActivityLinearLayoutSaatEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TimePickerDialog zaman = new TimePickerDialog(GezdigimYerlerEkleActvity.this, new TimePickerDialog.OnTimeSetListener() {
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


                        binding.gezdigimYerlerEkleActivityTextViewSaat.setText(eklenecekSaat);
                        binding.gezdigimYerlerEkleActivityTextViewDakika.setText(eklenecekDakika);
                    }
                }, saat, dakika, true);

                zaman.setButton(DatePickerDialog.BUTTON_POSITIVE, "Tamam", zaman);
                zaman.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", zaman);

                zaman.show();
            }
        });
        binding.gezdigimYerlerEkleActivityImageViewGezdigimYerEkleResmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(GezdigimYerlerEkleActvity.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(GezdigimYerlerEkleActvity.this, android.Manifest.permission.READ_MEDIA_IMAGES)) {
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
                    if (ContextCompat.checkSelfPermission(GezdigimYerlerEkleActvity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(GezdigimYerlerEkleActvity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
        binding.gezdigimYerlerEkleActivityFabEkleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String yerAdi = binding.gezdigimYerlerEkleActivityEditTextYerIsmi.getText().toString();
                String yerYorumu = binding.gezdigimYerlerEkleActivityEditTextYerOzeti.getText().toString();
                String tarihGun = binding.gezdigimYerlerEkleActivityTextViewGun.getText().toString();
                String tarihAy = binding.gezdigimYerlerEkleActivityTextViewAy.getText().toString();
                String tarihYil = binding.gezdigimYerlerEkleActivityTextViewYil.getText().toString();
                String zamanSaat = binding.gezdigimYerlerEkleActivityTextViewSaat.getText().toString();
                String zamanDakika = binding.gezdigimYerlerEkleActivityTextViewDakika.getText().toString();
                String kullaniciPuani = String.valueOf(kullaniciOyu);
                String userId = firebaseUser.getUid();

                if (imageData == null || yerAdi.isEmpty() || yerYorumu.isEmpty() || kullaniciOyu == 0 || gelenLatitude.equals("0.0")  || gelenLongitude.equals("0.0")) {
                    binding.gezdigimYerlerEkleActivityFabEkleButonu.setClickable(true);
                    Toast.makeText(GezdigimYerlerEkleActvity.this, "Lütfen istenilen bilgileri doldurunuz", Toast.LENGTH_SHORT).show();

                } else {

                    binding.gezdigimYerlerEkleActivityFabEkleButonu.setClickable(false);
                    binding.gezdigimYerlerEkleActivityProgressBar.setVisibility(View.VISIBLE);
                    UUID uuid = UUID.randomUUID();
                    String imgName = "gezilen_yer_resimleri/" + uuid + ".jpg";

                    storageReference.child(imgName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference newReference = firebaseStorage.getReference(imgName);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    HashMap<String, Object> gezilenYerVerisi = new HashMap<>();

                                    gezilenYerVerisi.put("downloadurl", downloadUrl);
                                    gezilenYerVerisi.put("yerAdi", yerAdi);
                                    gezilenYerVerisi.put("yerYorumu", yerYorumu);
                                    gezilenYerVerisi.put("tarihGun", tarihGun);
                                    gezilenYerVerisi.put("tarihAy", tarihAy);
                                    gezilenYerVerisi.put("tarihYil", tarihYil);
                                    gezilenYerVerisi.put("zamanSaat", zamanSaat);
                                    gezilenYerVerisi.put("zamanDakika", zamanDakika);
                                    gezilenYerVerisi.put("latitude", String.valueOf(gelenLatitude));
                                    gezilenYerVerisi.put("longitude", String.valueOf(gelenLongitude));
                                    gezilenYerVerisi.put("kullaniciPuani", kullaniciPuani);
                                    gezilenYerVerisi.put("userId", userId);
                                    gezilenYerVerisi.put("tarih", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection("gezilen_yerler").add(gezilenYerVerisi).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {


                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            binding.gezdigimYerlerEkleActivityFabEkleButonu.setClickable(false);
                                            Toast.makeText(GezdigimYerlerEkleActvity.this, "Yer kaydedildi", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(GezdigimYerlerEkleActvity.this, GezdigimYerlerActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            binding.gezdigimYerlerEkleActivityProgressBar.setVisibility(View.INVISIBLE);

                                            binding.gezdigimYerlerEkleActivityFabEkleButonu.setClickable(true);
                                            Toast.makeText(GezdigimYerlerEkleActvity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.gezdigimYerlerEkleActivityProgressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(GezdigimYerlerEkleActvity.this, "Görselin yüklenme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }



            }
        });
        binding.gezdigimYerlerEkleActivityLinearLayoutKonumEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GezdigimYerlerEkleActvity.this, MapsActivity.class);
                if (binding.gezdigimYerlerActivityTextViewKonumEkleYazisi.getText().equals("Seçilmiş")) {

                    intent.putExtra("latitude", gelenLatitude);
                    intent.putExtra("longitude", gelenLongitude);

                    intent.putExtra("eskimiyenimi", "yeni");
                    startActivity(intent);
                } else {


                    intent.putExtra("eskimiyenimi", "yeni");
                    startActivity(intent);

                }
            }
        });


    }



    private void anlikTarihiveZamaniAl() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Istanbul"), Locale.getDefault());
        yil = calendar.get(Calendar.YEAR);
        ay = calendar.get(Calendar.MONTH) + 1;
        gun = calendar.get(Calendar.DAY_OF_MONTH);
        saat = calendar.get(Calendar.HOUR_OF_DAY);
        dakika = calendar.get(Calendar.MINUTE);

        if (ay == 0) eklenecekAy = "0" + ay;
        else if (ay == 1) eklenecekAy = "0" + ay;
        else if (ay == 2) eklenecekAy = "0" + ay;
        else if (ay == 3) eklenecekAy = "0" + ay;
        else if (ay == 4) eklenecekAy = "0" + ay;
        else if (ay == 5) eklenecekAy = "0" + ay;
        else if (ay == 6) eklenecekAy = "0" + ay;
        else if (ay == 7) eklenecekAy = "0" + ay;
        else if (ay == 8) eklenecekAy = "0" + ay;
        else if (ay == 9) eklenecekAy = "0" + ay;
        else eklenecekAy = String.valueOf(ay);

        if (saat == 0) eklenecekSaat = "0" + saat;
        else if (saat == 1) eklenecekSaat = "0" + saat;
        else if (saat == 2) eklenecekSaat = "0" + saat;
        else if (saat == 3) eklenecekSaat = "0" + saat;
        else if (saat == 4) eklenecekSaat = "0" + saat;
        else if (saat == 5) eklenecekSaat = "0" + saat;
        else if (saat == 6) eklenecekSaat = "0" + saat;
        else if (saat == 7) eklenecekSaat = "0" + saat;
        else if (saat == 8) eklenecekSaat = "0" + saat;
        else if (saat == 9) eklenecekSaat = "0" + saat;
        else eklenecekSaat = String.valueOf(saat);


        if (dakika == 0) eklenecekDakika = "0" + dakika;
        else if (dakika == 1) eklenecekDakika = "0" + dakika;
        else if (dakika == 2) eklenecekDakika = "0" + dakika;
        else if (dakika == 3) eklenecekDakika = "0" + dakika;
        else if (dakika == 4) eklenecekDakika = "0" + dakika;
        else if (dakika == 5) eklenecekDakika = "0" + dakika;
        else if (dakika == 6) eklenecekDakika = "0" + dakika;
        else if (dakika == 7) eklenecekDakika = "0" + dakika;
        else if (dakika == 8) eklenecekDakika = "0" + dakika;
        else if (dakika == 9) eklenecekDakika = "0" + dakika;
        else eklenecekDakika = String.valueOf(dakika);


        binding.gezdigimYerlerEkleActivityTextViewAy.setText(eklenecekAy);
        binding.gezdigimYerlerEkleActivityTextViewGun.setText(String.valueOf(gun));
        binding.gezdigimYerlerEkleActivityTextViewYil.setText(String.valueOf(yil));
        binding.gezdigimYerlerEkleActivityTextViewSaat.setText(eklenecekSaat);
        binding.gezdigimYerlerEkleActivityTextViewDakika.setText(eklenecekDakika);


    }
    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.gezdigimYerlerEkleActivityImageViewGezdigimYerEkleResmi.setImageURI(imageData);
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

                    Toast.makeText(GezdigimYerlerEkleActvity.this, "Galeriye gitmemiz için izin gerekli", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}