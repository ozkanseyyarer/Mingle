package com.example.mingle.post;

public class KitapPost {
    public String kitapAdi;
    public String kitapOzeti;
    public String kitapYazari;
    public String downloadUrl;
    public String kullaniciPuani;
    public String kitapId;

    public KitapPost(String kitapAdi, String kitapOzeti, String kitapYazari, String downloadUrl, String kullaniciOyu,String kitapId) {
        this.kitapAdi = kitapAdi;
        this.kitapOzeti = kitapOzeti;
        this.kitapYazari = kitapYazari;
        this.downloadUrl = downloadUrl;
        this.kullaniciPuani = kullaniciOyu;
        this.kitapId = kitapId;
    }
}
