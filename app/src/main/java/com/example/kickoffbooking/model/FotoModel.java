package com.example.kickoffbooking.model;

public class FotoModel {
    public String judul;
    public String deskripsi;
    public String foto;

    public FotoModel() {
        // diperlukan oleh Firebase
    }

    public FotoModel(String judul, String deskripsi, String foto) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.foto = foto;
    }

    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getFoto() {
        return foto;
    }
}
