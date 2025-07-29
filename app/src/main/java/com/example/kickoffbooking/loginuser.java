package com.example.kickoffbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class loginuser extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView registerLink;
    private ImageView adminBtn;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginuser);

        emailInput = findViewById(R.id.emailuser);
        passwordInput = findViewById(R.id.pwuser);
        loginBtn = findViewById(R.id.kirimuser);
        adminBtn = findViewById(R.id.btnAdmin);
        registerLink = findViewById(R.id.registerLink);

        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(view -> loginUser());

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(loginuser.this, register.class));
        });

        adminBtn.setOnClickListener(v -> {
            startActivity(new Intent(loginuser.this, loginadmin.class));
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Email tidak valid");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Password minimal 6 karakter");
            passwordInput.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(loginuser.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(loginuser.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(loginuser.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
