package com.example.mingle.post;

public class GezdigimYerlerPost {
    public String downloadUrl, yerAdi,yerYorumu,  tarihGun, tarihAy, tarihYil, zamanSaat, zamanDakika, latitude, longitude, kullaniciPuani,yerId;

    public GezdigimYerlerPost(String downloadUrl, String yerAdi, String yerYorumu,
                              String tarihGun, String tarihAy, String tarihYil,
                              String zamanSaat, String zamanDakika, String latitude,
                              String longitude, String kullaniciPuani, String yerId) {
        this.downloadUrl = downloadUrl;
        this.yerAdi = yerAdi;
        this.yerYorumu = yerYorumu;
        this.tarihGun = tarihGun;
        this.tarihAy = tarihAy;
        this.tarihYil = tarihYil;
        this.zamanSaat = zamanSaat;
        this.zamanDakika = zamanDakika;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kullaniciPuani = kullaniciPuani;
        this.yerId = yerId;
    }
}
