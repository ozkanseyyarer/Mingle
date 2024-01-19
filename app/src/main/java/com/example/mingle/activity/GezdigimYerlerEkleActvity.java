package com.example.mingle.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.mingle.databinding.GezdigimYerlerEkleActvityBinding;

import java.util.Locale;

public class GezdigimYerlerEkleActvity extends AppCompatActivity {
    GezdigimYerlerEkleActvityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GezdigimYerlerEkleActvityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.gezdigimYerlerEkleActivityLinearLayoutTarihEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("tr");
                Locale.setDefault(locale);

                Resources resources = getResources();
                Configuration configuration;
                configuration = resources.getConfiguration();
                configuration.setLocale(locale);

                Context context = createConfigurationContext(configuration);
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                DatePickerDialog takvim = new DatePickerDialog(GezdigimYerlerEkleActvity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        binding.gezdigimYerlerEkleActivityTextViewAy.setText(String.valueOf(month+1));
                        binding.gezdigimYerlerEkleActivityTextViewGun.setText(String.valueOf(dayOfMonth));
                        binding.gezdigimYerlerEkleActivityTextViewYil.setText(String.valueOf(year));


                    }
                },2024,1,1);


                takvim.show();
            }
        });

        binding.gezdigimYerlerEkleActivityLinearLayoutSaatEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog saat = new TimePickerDialog(GezdigimYerlerEkleActvity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        binding.gezdigimYerlerEkleActivityTextViewSaat.setText(String.valueOf(hourOfDay));
                        binding.gezdigimYerlerEkleActivityTextViewDakika.setText(String.valueOf(minute));
                    }
                },23,00,true);

                saat.setButton(DatePickerDialog.BUTTON_POSITIVE, "Tamam", saat);
                saat.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", saat);

                saat.show();
            }
        });
    }
}