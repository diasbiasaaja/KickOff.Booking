package com.example.kickoffbooking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kickoffbooking.R;
import com.example.kickoffbooking.model.ChatItem;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;

    public ChatAdapter(List<ChatItem> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem item = chatList.get(position);

        holder.txtPengirim.setText("Admin");
        holder.txtPesan.setText(item.getPesan());

        // ✅ Tampilkan QR jika status approved dan url ada
        if ("approved".equals(item.getStatus()) && item.getQrUrl() != null && !item.getQrUrl().isEmpty()) {
            holder.imgQr.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(item.getQrUrl())
                    .into(holder.imgQr);
        } else {
            holder.imgQr.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtPengirim, txtPesan;
        ImageView imgQr;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPengirim = itemView.findViewById(R.id.txtIsiPesan);
            txtPesan = itemView.findViewById(R.id.imgQr);
            imgQr = itemView.findViewById(R.id.imgQrChat); // sesuai ID di chat_item.xml
        }
    }
}
