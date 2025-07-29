package com.example.kickoffbooking;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.graphics.Color;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.*;

public class jadwal extends AppCompatActivity {

    private Spinner spinnerLapangan;
    private TextView tvTanggal;
    private TableLayout tableJadwal;
    private String selectedLapangan = "";
    private String selectedTanggal = "";

    private final String[] daftarJam = {
            "08.00", "09.00", "10.00", "11.00", "12.00", "13.00", "14.00",
            "15.00", "16.00", "17.00", "18.00", "19.00", "20.00", "21.00"
    };

    private final Map<String, Map<String, Map<String, String>>> dataBooking = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        spinnerLapangan = findViewById(R.id.spinnerLapangan);
        tvTanggal = findViewById(R.id.tvTanggal);
        tableJadwal = findViewById(R.id.tableJadwal);

        String[] lapanganList = {"Lapangan Sintetis", "Lapangan Vinyl", "Mini Soccer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lapanganList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLapangan.setAdapter(adapter);

        spinnerLapangan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLapangan = parent.getItemAtPosition(position).toString();
                updateTable();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        tvTanggal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(jadwal.this, (view, year, month, dayOfMonth) -> {
                selectedTanggal = dayOfMonth + "/" + (month + 1) + "/" + year;
                tvTanggal.setText(selectedTanggal);
                updateTable();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Ambil data booking dari Firebase
        ambilDataDariFirebase();
    }

    private void ambilDataDariFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataBooking.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    for (DataSnapshot bookingSnap : userSnap.getChildren()) {
                        String lapangan = bookingSnap.child("lapangan").getValue(String.class);
                        String tanggal = bookingSnap.child("tanggal").getValue(String.class);
                        String club = bookingSnap.child("club").getValue(String.class);
                        String status = bookingSnap.child("status").getValue(String.class);
                        List<String> jamList = (List<String>) bookingSnap.child("jam").getValue();

                        if (lapangan != null && tanggal != null && club != null && jamList != null && "approved".equalsIgnoreCase(status)) {
                            for (String jam : jamList) {
                                addBooking(lapangan, tanggal, jam, club);
                            }
                        }
                    }
                }

                updateTable();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(jadwal.this, "Gagal memuat data booking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTable() {
        // Hapus semua baris kecuali header
        int childCount = tableJadwal.getChildCount();
        if (childCount > 1) {
            tableJadwal.removeViews(1, childCount - 1);
        }

        int index = 0;
        for (String jam : daftarJam) {
            TableRow row = new TableRow(this);

            // Ganti background warna selang-seling
            int bgColor = (index % 2 == 0) ? 0xFFFFFFFF : 0xFFF5F5F5;
            row.setBackgroundColor(bgColor);

            // Kolom JAM
            TextView tvJam = new TextView(this);
            tvJam.setText(jam);
            tvJam.setPadding(20, 16, 20, 16);
            tvJam.setTextColor(Color.BLACK);
            tvJam.setTextSize(14);
            tvJam.setGravity(Gravity.CENTER_VERTICAL);

            // Kolom STATUS
            TextView tvStatus = new TextView(this);
            tvStatus.setPadding(20, 16, 20, 16);
            tvStatus.setTextColor(Color.BLACK);
            tvStatus.setTextSize(14);
            tvStatus.setGravity(Gravity.CENTER_VERTICAL);

            String status = "Tersedia";

            if (!selectedLapangan.isEmpty() && !selectedTanggal.isEmpty()) {
                Map<String, Map<String, String>> lapanganData = dataBooking.get(selectedLapangan);
                if (lapanganData != null) {
                    Map<String, String> tanggalData = lapanganData.get(selectedTanggal);
                    if (tanggalData != null && tanggalData.containsKey(jam)) {
                        String klub = tanggalData.get(jam);
                        status = "Sudah Dipesan (" + klub + ")";
                        tvStatus.setTextColor(Color.RED);
                    }
                }
            }

            tvStatus.setText(status);

            row.addView(tvJam);
            row.addView(tvStatus);
            tableJadwal.addView(row);

            index++;
        }
    }

    private void addBooking(String lapangan, String tanggal, String jam, String klub) {
        dataBooking
                .computeIfAbsent(lapangan, k -> new HashMap<>())
                .computeIfAbsent(tanggal, k -> new HashMap<>())
                .put(jam, klub);
    }
}
