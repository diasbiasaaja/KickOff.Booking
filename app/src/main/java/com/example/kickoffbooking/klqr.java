package com.example.kickoffbooking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class klqr extends AppCompatActivity {

    Button btnScan;
    TextView txtHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klqr); // pastikan xml-nya cocok

        btnScan = findViewById(R.id.btnScan);
        txtHasil = findViewById(R.id.txtHasil);

        btnScan.setOnClickListener(view -> startScan());
    }

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    txtHasil.setText("Hasil QR: " + result.getContents());
                }
            });

    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barcodeLauncher.launch(options);
    }
}
