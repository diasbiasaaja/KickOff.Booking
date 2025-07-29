package com.example.kickoffbooking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kickoffbooking.model.GaleriItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class tambahfoto extends AppCompatActivity {

    private EditText edtJudul, edtDeskripsi;
    private ImageView imgPreview;
    private Button btnPilih, btnSimpan;
    private ProgressBar progressBar;

    private Uri selectedImageUri;
    private DatabaseReference dbRef;

    private final String IMGBB_API_KEY = "c5c01e442ea9bde4dbe044f65c0d4b7c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahfoto);

        edtJudul = findViewById(R.id.edtJudul);
        edtDeskripsi = findViewById(R.id.edtDeskripsi);
        imgPreview = findViewById(R.id.imgPreview);
        btnPilih = findViewById(R.id.btnPilihGambar);
        btnSimpan = findViewById(R.id.btnSimpanFoto);
        progressBar = findViewById(R.id.progressBar);

        dbRef = FirebaseDatabase.getInstance().getReference("galeri");

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgPreview.setImageURI(selectedImageUri);
                    }
                });

        btnPilih.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickIntent);
        });

        btnSimpan.setOnClickListener(v -> {
            String judul = edtJudul.getText().toString().trim();
            String deskripsi = edtDeskripsi.getText().toString().trim();

            if (judul.isEmpty() || deskripsi.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Semua field dan gambar wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                uploadToImgBB(bitmap, judul, deskripsi);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToImgBB(Bitmap bitmap, String judul, String deskripsi) {
        progressBar.setVisibility(View.VISIBLE);
        btnSimpan.setEnabled(false);
        btnPilih.setEnabled(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        String imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("key", IMGBB_API_KEY)
                .add("image", imageBase64)
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSimpan.setEnabled(true);
                    btnPilih.setEnabled(true);
                    Toast.makeText(tambahfoto.this, "Gagal upload gambar", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    String imageUrl = parseImageUrl(responseBody);

                    if (imageUrl != null) {
                        String id = dbRef.push().getKey();
                        GaleriItem item = new GaleriItem(judul, deskripsi, imageUrl);
                        dbRef.child(id).setValue(item);

                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnSimpan.setEnabled(true);
                            btnPilih.setEnabled(true);
                            Toast.makeText(tambahfoto.this, "Berhasil disimpan", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnSimpan.setEnabled(true);
                            btnPilih.setEnabled(true);
                            Toast.makeText(tambahfoto.this, "Gagal parsing URL gambar", Toast.LENGTH_SHORT).show();
                        });
                    }

                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnSimpan.setEnabled(true);
                        btnPilih.setEnabled(true);
                        Toast.makeText(tambahfoto.this, "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private String parseImageUrl(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONObject data = root.getJSONObject("data");
            return data.getString("url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
