package com.example.kickoffbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginadmin extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    private final String ADMIN_EMAIL = "futsal@gmail.com";
    private final String ADMIN_PASSWORD = "12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginadmin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ambil view dari XML
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.pw);
        btnLogin = findViewById(R.id.kirim);

        // Aksi ketika tombol login ditekan
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = etEmail.getText().toString().trim();
                String passwordInput = etPassword.getText().toString().trim();

                if (emailInput.equals(ADMIN_EMAIL) && passwordInput.equals(ADMIN_PASSWORD)) {
                    Toast.makeText(loginadmin.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                    // Pindah ke halaman admin (ganti dengan activity yang kamu punya)
                    Intent intent = new Intent(loginadmin.this, homeadmin.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(loginadmin.this, "Email atau password salah!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
