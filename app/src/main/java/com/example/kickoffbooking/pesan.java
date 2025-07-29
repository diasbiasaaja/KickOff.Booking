package com.example.kickoffbooking;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickoffbooking.adapter.ChatAdapter;
import com.example.kickoffbooking.model.ChatItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class pesan extends AppCompatActivity {

    RecyclerView recyclerChat;
    ChatAdapter chatAdapter;
    List<ChatItem> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan);

        recyclerChat = findViewById(R.id.recyclerChat);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        chatAdapter = new ChatAdapter(chatList);

        recyclerChat.setAdapter(chatAdapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chat").child(uid);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ChatItem chat = snap.getValue(ChatItem.class);
                    if (chat != null) {
                        chatList.add(chat);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerChat.scrollToPosition(chatList.size() - 1); // Scroll ke paling bawah
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("pesan", "Gagal memuat pesan: " + error.getMessage());
            }
        });
    }
}
