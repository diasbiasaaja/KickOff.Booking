package com.example.kickoffbooking;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.example.kickoffbooking.model.GaleriItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper v_flipperr;
    private LinearLayout menuJadwal, menuLapangan, menuBooking, menuPesan, layoutGaleri;
    private Button btnUpload;
    private ImageView adminIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.WHITE);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false); // karena pakai background gelap
        controller.setAppearanceLightNavigationBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0); // HANYA padding top agar isi tidak tertutup status bar
            return insets;
        });


        // Inisialisasi view
        v_flipperr = findViewById(R.id.v_flipper);
        layoutGaleri = findViewById(R.id.layoutGaleri);
        btnUpload = findViewById(R.id.tambahfoto);
        adminIcon = findViewById(R.id.admin);

        // Gambar carousel
        int[] images = {R.drawable.sl3, R.drawable.sl2, R.drawable.sl1};
        for (int img : images) {
            flipperImages(img);
        }

        // Navigasi menu
        menuJadwal = findViewById(R.id.jadwal);
        menuLapangan = findViewById(R.id.lapangan);
        menuBooking = findViewById(R.id.booking);
        menuPesan = findViewById(R.id.chat);

        menuJadwal.setOnClickListener(v -> startActivity(new Intent(this, jadwal.class)));
        menuLapangan.setOnClickListener(v -> startActivity(new Intent(this, lapangan.class)));
        menuBooking.setOnClickListener(v -> startActivity(new Intent(this, booking.class)));
        menuPesan.setOnClickListener(v -> startActivity(new Intent(this, pesan.class)));

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, tambahfoto.class);
            startActivity(intent);
        });

        adminIcon.setOnClickListener(v -> startActivity(new Intent(this, loginadmin.class)));

        // Ambil data galeri dari Firebase
        ambilDataGaleriDariFirebase();
    }

    private void flipperImages(int imageResId) {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(imageResId);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        v_flipperr.addView(imageView);
        v_flipperr.setFlipInterval(4000);
        v_flipperr.setAutoStart(true);
        v_flipperr.setInAnimation(this, android.R.anim.slide_in_left);
        v_flipperr.setOutAnimation(this, android.R.anim.slide_out_right);
    }

    private void ambilDataGaleriDariFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("galeri");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                layoutGaleri.removeAllViews(); // Bersihkan galeri
                for (DataSnapshot data : snapshot.getChildren()) {
                    GaleriItem item = data.getValue(GaleriItem.class);
                    if (item != null) {
                        tampilkanGaleri(item);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log error (opsional)
            }
        });
    }

    private void tampilkanGaleri(GaleriItem item) {
        View galeriView = LayoutInflater.from(this).inflate(R.layout.item_galeri, layoutGaleri, false);

        ImageView imageView = galeriView.findViewById(R.id.itemImage);
        TextView textJudul = galeriView.findViewById(R.id.itemJudul);
        TextView textDeskripsi = galeriView.findViewById(R.id.itemDeskripsi);

        Glide.with(this).load(Uri.parse(item.getFoto())).into(imageView);
        textJudul.setText(item.getJudul());
        textDeskripsi.setText(item.getDeskripsi());

        layoutGaleri.addView(galeriView);
    }
}
