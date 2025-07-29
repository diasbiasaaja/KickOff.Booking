package com.example.kickoffbooking.model;

public class GaleriItem {
    private String id;         // ini opsional untuk hapus
    private String judul;
    private String deskripsi;
    private String foto;

    // Wajib constructor kosong untuk Firebase
    public GaleriItem() {
    }

    public GaleriItem(String judul, String deskripsi, String foto) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.foto = foto;
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
