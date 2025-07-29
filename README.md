# KickOff Booking ⚽📱
KickOff Booking adalah aplikasi Android yang memudahkan pengguna untuk:
- Booking lapangan sepak bola
- Melihat jadwal booking
- Menyimpan galeri kenangan
- Mengirim pesan
- Menjelajahi daftar lapangan
- Dilengkapi halaman khusus untuk admin

---

## 📌 Fitur-Fitur yang di miliki

### . Menu Navigasi Horizontal
- Terdiri dari 4 menu utama:
  - **Jadwal**
  - **Lapangan**
  - **Booking**
  - **Pesan**
- Tampilan interaktif dan animasi klik saat berpindah halaman
  
### 1. Booking Lapangan ⚽
- Input data: Nama, Nama Klub, Tanggal, dan Pilihan Jam
- Jam bisa dipilih lebih dari satu
- Jam yang sudah dibooking tidak bisa dipilih ulang
- Total harga dihitung otomatis, harga di sesuaikan lapangan yang di pilih
- Tombol untuk lanjut ke halaman pembayaran

### 2. Galeri Kenangan 🎴
- Pengguna dapat menambahkan foto, judul (teks bold), dan deskripsi (teks biasa)
- Data ditampilkan di halaman utama tepat di bawah tombol “Tambah Kenangan Kamu!”
- Galeri bisa di-*scroll* ke bawah
- Fitur upload foto dari link gambar (via Firebase)
  
### 3. Lihat Jadwal Booking 📕
- Melihat semua jadwal lapangan yang sudah dibooking oleh pengguna lain
- Jam yang sudah dibooking akan muncul sebagai *terisi* pada tampilan jadwal
- Nama Klub akan ditampilkan di slot jam yang sudah dibooking

### 4. Pesan ✉
- Fitur untuk mengirim dan melihat pesan antar pengguna atau ke admin
- Bila booking di "Approve" oleh admin, maka user akan mendapatkan QR code untuk validasi lapangan
- Bila booking di "tolak" oleh admin, maka user akan mendapatkan Pesan "Maaf Bookingan Anda gagal dan tidak valid"
- Disimpan di Firebase agar data tetap tersimpan online

### 5. Daftar Lapangan 📋
- Menampilkan daftar lapangan yang tersedia untuk booking
- Setiap lapangan memiliki informasi: nama lapangan, jenis lapangan (rumput sintetis/Vinly/Mini Soccer), dan Diameter lapangan nya.
- Desain layout bersih dan mudah dipahami

## 🛡️ Fitur Admin

Fitur khusus untuk akun admin (akses terbatas):

### ✅ Kelola Booking
- Admin dapat melihat semua booking yang masuk
- Bisa **approve** atau **tolak** permintaan booking

### 🖼️ Kelola Galeri
- Admin dapat menghapus foto galeri yang tidak sesuai

### 📅 Kelola Jadwal
- Menampilkan dan mengatur jadwal lapangan
- Layout menyatu dengan halaman Home Admin

### 📷 Scan QR Code
- Fitur untuk **scan QR**
- Setelah scan, akan menampilkan hasil tertentu (misalnya data booking)
- Digunakan untuk validasi di lapangan

---


---

## 🛠 Teknologi yang Digunakan

- **Bahasa Pemrograman:** Java
- **Database:** Firebase Realtime Database
- **Database:** Firebase Authentication
- **imgBB** untuk aplod gambar
- **IDE:** Android Studio
- **Desain UI:** XML
- **Manajemen Proyek:** Gradle

---

## 🚀 Cara Menjalankan

1. Clone repository ini:
   ```bash
   git clone https://github.com/diasbiasasaja/KickOff.Booking.git
