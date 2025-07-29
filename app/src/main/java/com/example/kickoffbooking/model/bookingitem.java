package com.example.kickoffbooking.model;

import java.util.List;

public class bookingitem {
    private String id;
    private String nama;
    private String club;
    private String tanggal;
    private String lapangan;
    private List<String> jam;
    private int total;
    private String status;
    private String buktiPembayaran;
    private String uid; // ✅ Tambahkan di sini

    public bookingitem() {}

    public bookingitem(String id, String nama, String club, String tanggal, String lapangan, List<String> jam, int total) {
        this.id = id;
        this.nama = nama;
        this.club = club;
        this.tanggal = tanggal;
        this.lapangan = lapangan;
        this.jam = jam;
        this.total = total;
        this.status = "pending";
        this.buktiPembayaran = "";
    }

    // Getter dan Setter
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getClub() { return club; }
    public String getTanggal() { return tanggal; }
    public String getLapangan() { return lapangan; }
    public List<String> getJam() { return jam; }
    public int getTotal() { return total; }
    public String getStatus() { return status; }
    public String getBuktiPembayaran() { return buktiPembayaran; }
    public String getUid() { return uid; } // ✅ getter uid

    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setClub(String club) { this.club = club; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public void setLapangan(String lapangan) { this.lapangan = lapangan; }
    public void setJam(List<String> jam) { this.jam = jam; }
    public void setTotal(int total) { this.total = total; }
    public void setStatus(String status) { this.status = status; }
    public void setBuktiPembayaran(String buktiPembayaran) { this.buktiPembayaran = buktiPembayaran; }
    public void setUid(String uid) { this.uid = uid; } // ✅ setter uid
}
