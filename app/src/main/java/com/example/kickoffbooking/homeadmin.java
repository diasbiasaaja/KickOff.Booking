package com.example.kickoffbooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.*;

public class homeadmin extends AppCompatActivity {

    Spinner spinnerLapangan;
    TextView tvTanggal;
    TableLayout tableJadwal;

    private String selectedLapangan = "";
    private String selectedTanggal = "";

    private final String[] daftarJam = {
            "08.00", "09.00", "10.00", "11.00", "12.00", "13.00",
            "14.00", "15.00", "16.00", "17.00", "18.00", "19.00",
            "20.00", "21.00"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeadmin);

        // Inisialisasi tampilan
        spinnerLapangan = findViewById(R.id.spinnerLapangan);
        tvTanggal = findViewById(R.id.tvTanggal);
        tableJadwal = findViewById(R.id.tableJadwal);

        // Tambahan: menu horizontal yang bisa diklik
        LinearLayout klgaleri = findViewById(R.id.klgaleri);
        LinearLayout klbooking = findViewById(R.id.klbooking);
        LinearLayout chat = findViewById(R.id.chat);

        // Klik menu: Kelola Galeri
        klgaleri.setOnClickListener(v -> {
            Intent intent = new Intent(homeadmin.this, com.example.kickoffbooking.klgaleri.class);
            startActivity(intent);
        });

        // Klik menu: Kelola Booking
        klbooking.setOnClickListener(v -> {
            Intent intent = new Intent(homeadmin.this, klbooking.class);
            startActivity(intent);
        });

        // Klik menu: Scan QR
        chat.setOnClickListener(v -> {
            Intent intent = new Intent(homeadmin.this, klqr.class);
            startActivity(intent);
        });

        // Spinner lapangan
        String[] lapanganList = {"Lapangan Sintetis", "Lapangan Vinyl", "Mini Soccer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lapanganList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLapangan.setAdapter(adapter);

        spinnerLapangan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLapangan = parent.getItemAtPosition(position).toString();
                updateTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Pilih tanggal
        tvTanggal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(homeadmin.this, (view, year, month, day) -> {
                selectedTanggal = day + "/" + (month + 1) + "/" + year;
                tvTanggal.setText(selectedTanggal);
                updateTable();
            }, y, m, d).show();
        });
    }

    private void updateTable() {
        int count = tableJadwal.getChildCount();
        if (count > 1) tableJadwal.removeViews(1, count - 1);

        if (selectedTanggal.isEmpty() || selectedLapangan.isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Map<String, Object>> dataJam = new HashMap<>();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    for (DataSnapshot bookSnap : userSnap.getChildren()) {
                        String tgl = bookSnap.child("tanggal").getValue(String.class);
                        String lap = bookSnap.child("lapangan").getValue(String.class);
                        List<String> jamList = (List<String>) bookSnap.child("jam").getValue();
                        String club = bookSnap.child("club").getValue(String.class);

                        if (tgl == null || lap == null || jamList == null || club == null) continue;
                        if (!tgl.equals(selectedTanggal)) continue;
                        if (!lap.equals(selectedLapangan)) continue;

                        for (String jam : jamList) {
                            Map<String, Object> info = new HashMap<>();
                            info.put("club", club);
                            info.put("path", bookSnap.getRef().toString());
                            dataJam.put(jam, info);
                        }
                    }
                }

                for (String jam : daftarJam) {
                    TableRow row = new TableRow(homeadmin.this);

                    TextView tvJam = new TextView(homeadmin.this);
                    tvJam.setText(jam);
                    tvJam.setPadding(12, 12, 12, 12);

                    TextView tvStatus = new TextView(homeadmin.this);
                    tvStatus.setPadding(12, 12, 12, 12);

                    Button btnHapus = new Button(homeadmin.this);
                    btnHapus.setText("Hapus");
                    btnHapus.setTextSize(12);
                    btnHapus.setPadding(8, 8, 8, 8);

                    if (dataJam.containsKey(jam)) {
                        String clubName = (String) dataJam.get(jam).get("club");
                        String path = (String) dataJam.get(jam).get("path");

                        tvStatus.setText("Dipesan oleh: " + clubName);
                        tvStatus.setBackgroundColor(0xFFFFF0F0);
                        btnHapus.setEnabled(true);

                        btnHapus.setOnClickListener(v -> {
                            new android.app.AlertDialog.Builder(homeadmin.this)
                                    .setTitle("Konfirmasi Hapus")
                                    .setMessage("Hapus booking jam " + jam + "?")
                                    .setPositiveButton("Hapus", (dialog, which) -> {
                                        FirebaseDatabase.getInstance().getReferenceFromUrl(path)
                                                .removeValue()
                                                .addOnSuccessListener(unused -> updateTable());
                                    })
                                    .setNegativeButton("Batal", null)
                                    .show();
                        });

                    } else {
                        tvStatus.setText("Tersedia");
                        tvStatus.setBackgroundColor(0xFFE0FFE0);
                        btnHapus.setText("-");
                        btnHapus.setEnabled(false);
                    }

                    row.addView(tvJam);
                    row.addView(tvStatus);
                    row.addView(btnHapus);
                    tableJadwal.addView(row);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(homeadmin.this, "Gagal ambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
