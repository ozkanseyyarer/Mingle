package com.example.mingle.post;

public class FilmPost {
    public String filmAdi, filmYonetmeni, filmOzeti, downloadUrl,kullaniciPuani, filmId;

    public FilmPost(String filmAdi, String filmYonetmeni, String filmOzeti, String downloadUrl, String kullaniciPuani, String filmId) {
        this.filmAdi = filmAdi;
        this.filmYonetmeni = filmYonetmeni;
        this.filmOzeti = filmOzeti;
        this.downloadUrl = downloadUrl;
        this.kullaniciPuani = kullaniciPuani;
        this.filmId = filmId;
    }
}
