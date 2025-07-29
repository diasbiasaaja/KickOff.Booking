package com.example.kickoffbooking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickoffbooking.adapter.GaleriAdminAdapter;
import com.example.kickoffbooking.model.GaleriItem;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class klgaleri extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GaleriAdminAdapter adapter;
    private List<GaleriItem> galeriList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klgaleri);

        recyclerView = findViewById(R.id.recyclerGaleri);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        galeriList = new ArrayList<>();
        adapter = new GaleriAdminAdapter(this, galeriList);
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("galeri");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                galeriList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    GaleriItem item = data.getValue(GaleriItem.class);
                    if (item != null) {
                        item.setId(data.getKey()); // agar bisa dihapus
                        galeriList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error
            }
        });
    }
}

