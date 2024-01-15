package com.example.mingle.girisactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.mingle.R;
import com.example.mingle.activity.ListelemeSayfasiActivity;
import com.example.mingle.databinding.GirisEkraniActivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GirisEkraniActivity extends AppCompatActivity {

    private GirisEkraniActivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GirisEkraniActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
       if (firebaseUser != null) {
            finish();
            startActivity(new Intent(GirisEkraniActivity.this, ListelemeSayfasiActivity.class));
        }


        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.girisEkraniActivityAnimasyonGorseli.startAnimation(fadeInAnimation);

        binding.girisEkraniActivityTextViewSifremiUnuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisEkraniActivity.this, SifremiUnuttumAktivity.class));
            }
        });

        binding.girisEkraniActivityButonKayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), KayitOlAktivity.class));
            }
        });

        binding.girisEkraniActivityButonGirisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailAdresi = binding.girisEkraniActivityEditTextEmail.getText().toString().trim();
                String parola = binding.girisEkraniActivityEditTextParola.getText().toString().trim();

                if (mailAdresi.isEmpty() || parola.isEmpty()) {
                    Toast.makeText(GirisEkraniActivity.this, "Lütfen boş yer bırakmayınız", Toast.LENGTH_SHORT).show();
                    binding.girisEkraniActivityProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    binding.girisEkraniActivityProgressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(mailAdresi, parola).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            dogrulamaLinkiOnalanmisMi();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Email adresi bulunamazsa
                            if (e.getMessage() != null && e.getMessage().contains("user-not-found")) {
                                Toast.makeText(GirisEkraniActivity.this, "Girilen email adresi bulunamadı", Toast.LENGTH_SHORT).show();

                            }
                            // Şifre yanlışsa
                            else if (e.getMessage() != null && e.getMessage().contains("invalid-password")) {
                                Toast.makeText(GirisEkraniActivity.this, "Şifre yanlış, lütfen tekrar deneyin", Toast.LENGTH_SHORT).show();
                            }
                            // Diğer hatalar
                            else {
                                Toast.makeText(GirisEkraniActivity.this, "Giriş başarısız", Toast.LENGTH_SHORT).show();
                            }
                            binding.girisEkraniActivityProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void dogrulamaLinkiOnalanmisMi() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.isEmailVerified()) {
            Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(GirisEkraniActivity.this, ListelemeSayfasiActivity.class));
        } else {
            Toast.makeText(this, "Mail adresinize gelen doğrulama linkini onaylamalısınız", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}