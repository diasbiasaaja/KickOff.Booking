package com.example.kickoffbooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.NumberFormat;
import java.util.*;

public class booking extends AppCompatActivity {

    EditText edtNama, edtNamaClub;
    TextView txtTanggal, txtTotalHarga;
    GridLayout jamContainer;
    Spinner spinnerLapangan;
    Button btnPesan;

    final Map<String, Integer> hargaLapanganMap = new HashMap<>();
    Set<String> jamDipilih = new HashSet<>();
    String[] jamList = {
            "08.00", "09.00", "10.00", "11.00", "12.00", "13.00", "14.00",
            "15.00", "16.00", "17.00", "18.00", "19.00", "20.00", "21.00"
    };

    String tanggalDipilih = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        edtNama = findViewById(R.id.edtNama);
        edtNamaClub = findViewById(R.id.edtNamaClub);
        txtTanggal = findViewById(R.id.txtTanggal);
        txtTotalHarga = findViewById(R.id.txtTotalHarga);
        jamContainer = findViewById(R.id.jamContainer);
        btnPesan = findViewById(R.id.btnPesan);
        spinnerLapangan = findViewById(R.id.spinnerLapangan);

        hargaLapanganMap.put("Lapangan Sintetis", 100000);
        hargaLapanganMap.put("Lapangan Vinyl", 90000);
        hargaLapanganMap.put("Mini Soccer", 120000);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new ArrayList<>(hargaLapanganMap.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLapangan.setAdapter(adapter);

        buatTombolJam(false, new HashSet<>());

        txtTanggal.setOnClickListener(v -> showDatePicker());

        btnPesan.setOnClickListener(v -> {
            if (jamDipilih.isEmpty()) {
                Toast.makeText(this, "Pilih minimal 1 jam!", Toast.LENGTH_SHORT).show();
                return;
            }

            String lapanganDipilih = spinnerLapangan.getSelectedItem().toString();
            int hargaPerJam = hargaLapanganMap.get(lapanganDipilih);
            int total = jamDipilih.size() * hargaPerJam;

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking").child(uid);
            String bookingId = ref.push().getKey();

            Map<String, Object> data = new HashMap<>();
            data.put("uid", uid); // ← tambahkan ini agar nanti bisa dibaca adapter
            data.put("id", bookingId);
            data.put("nama", edtNama.getText().toString());
            data.put("club", edtNamaClub.getText().toString());
            data.put("tanggal", tanggalDipilih);
            data.put("lapangan", lapanganDipilih);
            data.put("jam", new ArrayList<>(jamDipilih));
            data.put("total", total);
            data.put("status", "pending");

            if (bookingId != null) {
                ref.child(bookingId).setValue(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Booking dikirim, menunggu konfirmasi admin", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, Pembayaran.class);
                        intent.putExtra("bookingId", bookingId);
                        intent.putExtra("nama", edtNama.getText().toString());
                        intent.putExtra("club", edtNamaClub.getText().toString());
                        intent.putExtra("tanggal", tanggalDipilih);
                        intent.putExtra("lapangan", lapanganDipilih);
                        intent.putStringArrayListExtra("jam", new ArrayList<>(jamDipilih));
                        intent.putExtra("total", total);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Gagal menyimpan booking", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    Calendar now = Calendar.getInstance();
                    now.set(Calendar.HOUR_OF_DAY, 0);
                    now.set(Calendar.MINUTE, 0);
                    now.set(Calendar.SECOND, 0);

                    if (selected.before(now)) {
                        Toast.makeText(this, "Tidak bisa memilih tanggal yang sudah lewat", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tanggalDipilih = dayOfMonth + "/" + (month + 1) + "/" + year;
                    txtTanggal.setText(tanggalDipilih);
                    ambilJamTerpakai(tanggalDipilih);
                },
                tahun, bulan, hari
        );
        dpd.show();
    }

    private void ambilJamTerpakai(String tanggal) {
        String lapanganDipilih = spinnerLapangan.getSelectedItem().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Set<String> jamTerpakai = new HashSet<>();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    for (DataSnapshot bookingSnap : userSnap.getChildren()) {
                        String tanggalData = bookingSnap.child("tanggal").getValue(String.class);
                        String lapanganData = bookingSnap.child("lapangan").getValue(String.class);
                        String status = bookingSnap.child("status").getValue(String.class);

                        // Bandingkan tanggal, lapangan, dan status "approved"
                        if (tanggal.equals(tanggalData) &&
                                lapanganDipilih.equalsIgnoreCase(lapanganData) &&
                                status != null &&
                                status.equalsIgnoreCase("approved")) {

                            List<String> jamList = (List<String>) bookingSnap.child("jam").getValue();
                            if (jamList != null) {
                                jamTerpakai.addAll(jamList);
                            }
                        }
                    }
                }

                buatTombolJam(true, jamTerpakai);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(booking.this, "Gagal memuat data jam", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void buatTombolJam(boolean aktifkan, Set<String> jamTerpakai) {
        jamContainer.removeAllViews();
        for (String jam : jamList) {
            ToggleButton toggle = new ToggleButton(this);
            toggle.setText(jam);
            toggle.setTextOn(jam);
            toggle.setTextOff(jam);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(12, 12, 12, 12);
            params.width = 200;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            toggle.setLayoutParams(params);

            toggle.setBackgroundColor(Color.LTGRAY);
            toggle.setTextColor(Color.BLACK);

            if (jamTerpakai.contains(jam)) {
                toggle.setEnabled(false);
                toggle.setBackgroundColor(Color.GRAY);
            } else {
                toggle.setEnabled(aktifkan);
            }

            toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!toggle.isEnabled()) return;

                if (isChecked) {
                    jamDipilih.add(jam);
                    toggle.setBackgroundColor(Color.parseColor("#001F5B"));
                    toggle.setTextColor(Color.WHITE);
                } else {
                    jamDipilih.remove(jam);
                    toggle.setBackgroundColor(Color.LTGRAY);
                    toggle.setTextColor(Color.BLACK);
                }
                updateTotalHarga();
            });

            jamContainer.addView(toggle);
        }
    }

    private void updateTotalHarga() {
        if (spinnerLapangan.getSelectedItem() == null) return;
        String lapangan = spinnerLapangan.getSelectedItem().toString();
        int harga = hargaLapanganMap.get(lapangan);
        int total = jamDipilih.size() * harga;
        String totalFormatted = NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(total);
        txtTotalHarga.setText("Total: " + totalFormatted);
    }
}
