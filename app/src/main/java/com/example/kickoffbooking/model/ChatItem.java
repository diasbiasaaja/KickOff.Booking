package com.example.kickoffbooking.model;

public class ChatItem {
    private String pesan;
    private String status;
    private String qrUrl;

    public ChatItem() {
        // Required for Firebase
    }

    public ChatItem(String pesan, String status, String qrUrl) {
        this.pesan = pesan;
        this.status = status;
        this.qrUrl = qrUrl;
    }

    public String getPesan() {
        return pesan;
    }

    public String getStatus() {
        return status;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
