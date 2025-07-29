package com.example.kickoffbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class register extends AppCompatActivity {

    private EditText regEmail, regPassword;
    private Button btnRegister;
    private TextView loginLink;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        btnRegister = findViewById(R.id.btnRegister);
        loginLink = findViewById(R.id.loginLink);

        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        btnRegister.setOnClickListener(v -> registerUser());

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(register.this, loginuser.class));
            finish();
        });
    }

    private void registerUser() {
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();

        // Validasi input
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            regEmail.setError("Email tidak valid");
            regEmail.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            regPassword.setError("Password minimal 6 karakter");
            regPassword.requestFocus();
            return;
        }

        // Register dengan Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        // Simpan data user ke Firebase Realtime Database
                        User user = new User(email);
                        databaseRef.child(uid).setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(register.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(register.this, loginuser.class));
                                        finish();
                                    } else {
                                        Toast.makeText(register.this, "Gagal simpan data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(register.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Model user sederhana
    public static class User {
        public String email;

        public User() {} // Diperlukan Firebase

        public User(String email) {
            this.email = email;
        }
    }
}
