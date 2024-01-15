package com.example.mingle.girisactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.databinding.KayitOlAktivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class KayitOlAktivity extends AppCompatActivity {
    private KayitOlAktivityBinding binding;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = KayitOlAktivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        binding.kayitOlActivityTextViewGalibaHesabimVardi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GirisEkraniActivity.class));
            }
        });

        binding.kayitOlActivityButonKayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = binding.kayitOlActivityEditTextEmail.getText().toString().trim();
                String parola = binding.kayitOlActivityEditTextParola.getText().toString().trim();

                if (mail.isEmpty() || parola.isEmpty()){
                    Toast.makeText(KayitOlAktivity.this, "Lütfen boş yer bırakmayınız", Toast.LENGTH_SHORT).show();
                } else if (parola.length() <8) {
                    Toast.makeText(KayitOlAktivity.this, "Parola 8 karakterden az olamaz", Toast.LENGTH_SHORT).show();
                }else{

                    firebaseAuth.createUserWithEmailAndPassword(mail, parola).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(KayitOlAktivity.this, "Kullanıcı kaydı başarılı", Toast.LENGTH_LONG).show();
                            dogrulamaLinkiGonder();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                // Bu hata, kullanıcının daha önce kayıt olduğu anlamına gelir.
                                Toast.makeText(KayitOlAktivity.this, "Bu e-posta adresi zaten kullanımda", Toast.LENGTH_SHORT).show();
                            } else {
                                // Diğer hatalar için genel bir mesaj gösterilebilir.
                                Toast.makeText(KayitOlAktivity.this, "Kayıt gerçekleştirilemedi", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private void dogrulamaLinkiGonder(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(KayitOlAktivity.this, "mail adresinize gönderilen doğrulama linkini onaylayınız", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(KayitOlAktivity.this, GirisEkraniActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(KayitOlAktivity.this, "Doğrulama linki gönderilemedi. Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}