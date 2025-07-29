package com.example.kickoffbooking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.*;

import okhttp3.*;
import com.google.firebase.auth.FirebaseAuth;


public class Pembayaran extends AppCompatActivity {

    private static final String IMGBB_API_KEY = "c5c01e442ea9bde4dbe044f65c0d4b7c";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    private ImageView imgBukti;
    private Button btnUploadBukti, btnPilihGambar;
    private TextView txtLapangan, txtTotal, txtJam;

    private String nama, club, tanggal, lapangan;
    private ArrayList<String> jam;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        imgBukti = findViewById(R.id.imgPreviewBukti);
        btnUploadBukti = findViewById(R.id.btnKirimPembayaran);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
        txtLapangan = findViewById(R.id.txtLapangan);
        txtTotal = findViewById(R.id.txtTotal);
        txtJam = findViewById(R.id.txtJam);

        // Ambil data dari Intent
        nama = getIntent().getStringExtra("nama");
        club = getIntent().getStringExtra("club");
        tanggal = getIntent().getStringExtra("tanggal");
        lapangan = getIntent().getStringExtra("lapangan");
        jam = getIntent().getStringArrayListExtra("jam");
        total = getIntent().getIntExtra("total", 0);

        // Tampilkan data
        txtLapangan.setText("Lapangan: " + lapangan);
        txtTotal.setText("Total: Rp" + total);
        txtJam.setText("Jam: " + jam.toString());

        // Pilih gambar dari galeri
        btnPilihGambar.setOnClickListener(v -> openFileChooser());

        // Upload gambar ke Imgbb
        btnUploadBukti.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadToImgbb(imageUri);
            } else {
                Toast.makeText(this, "Pilih gambar dulu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgBukti.setImageURI(imageUri);
        }
    }

    private void uploadToImgbb(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);

            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("key", IMGBB_API_KEY)
                    .add("image", encodedImage)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, java.io.IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(Pembayaran.this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws java.io.IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            String imageUrl = json.getJSONObject("data").getString("url");
                            saveToFirebase(imageUrl);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(Pembayaran.this, "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveToFirebase(String imageUrl) {
        String bookingId = getIntent().getStringExtra("bookingId");

        // Ambil UID user yang login
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Arahkan ke node booking/UID/bookingId
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("booking")
                .child(uid)
                .child(bookingId);

        Map<String, Object> update = new HashMap<>();
        update.put("buktiPembayaran", imageUrl);
        update.put("status", "menunggu verifikasi");

        // Optional: tambahkan lagi data booking-nya agar lengkap
        update.put("nama", nama);
        update.put("club", club);
        update.put("tanggal", tanggal);
        update.put("lapangan", lapangan);
        update.put("jam", jam);
        update.put("total", total);

        ref.updateChildren(update).addOnCompleteListener(task -> {
            runOnUiThread(() -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Bukti pembayaran berhasil diupload", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Gagal menyimpan bukti", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


}
