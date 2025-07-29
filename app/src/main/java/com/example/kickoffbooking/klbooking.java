package com.example.kickoffbooking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickoffbooking.adapter.BookingAdapter;
import com.example.kickoffbooking.model.bookingitem;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class klbooking extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<bookingitem> bookingList;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klbooking); // layout xml untuk tampilan admin booking

        recyclerView = findViewById(R.id.recyclerBooking);
        progressBar = findViewById(R.id.progressBarBooking);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(this, bookingList);
        recyclerView.setAdapter(adapter);

        loadBookingData();
    }

    private void loadBookingData() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();

                // Loop semua UID dulu
                for (DataSnapshot uidSnap : snapshot.getChildren()) {
                    for (DataSnapshot bookingSnap : uidSnap.getChildren()) {
                        bookingitem item = bookingSnap.getValue(bookingitem.class);
                        if (item != null) {
                            item.setId(bookingSnap.getKey()); // supaya bisa update
                            bookingList.add(item);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(klbooking.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
