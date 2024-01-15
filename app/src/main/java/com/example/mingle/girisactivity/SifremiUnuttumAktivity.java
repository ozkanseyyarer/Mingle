package com.example.mingle.girisactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.databinding.SifremiUnuttumAktivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SifremiUnuttumAktivity extends AppCompatActivity {

    private SifremiUnuttumAktivityBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SifremiUnuttumAktivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.sifremiUnuttumActivityButtonSifremiYenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailAdresi = binding.sifremiUnuttumActivityEditTextEmail.getText().toString().trim();
                
                if (mailAdresi == null){
                    Toast.makeText(SifremiUnuttumAktivity.this, "Lütfen mail adresinizi giriniz", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(mailAdresi).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SifremiUnuttumAktivity.this, "Şifrenizi yenileme linki mail adresinize gönderildi.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(SifremiUnuttumAktivity.this, GirisEkraniActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                // Eğer hata, kullanıcının kayıtlı olmayan bir e-posta adresi girdiğini belirtiyorsa
                                Toast.makeText(SifremiUnuttumAktivity.this, "Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                            } else {
                                // Diğer hatalar için genel bir mesaj gösterilebilir.
                                Toast.makeText(SifremiUnuttumAktivity.this, "Şifre sıfırlama maili gönderilemedi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        binding.girisEkraniActivityTextViewSifremiHatirlarGibiyim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GirisEkraniActivity.class));
            }
        });
    }
}