package com.example.kickoffbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kickoffbooking.R;
import com.example.kickoffbooking.model.GaleriItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GaleriAdminAdapter extends RecyclerView.Adapter<GaleriAdminAdapter.ViewHolder> {

    private final Context context;
    private final List<GaleriItem> galeriList;
    private final DatabaseReference dbRef;

    public GaleriAdminAdapter(Context context, List<GaleriItem> galeriList) {
        this.context = context;
        this.galeriList = galeriList;
        dbRef = FirebaseDatabase.getInstance().getReference("galeri");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_galeri_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GaleriItem item = galeriList.get(position);
        holder.txtJudul.setText(item.getJudul());
        holder.txtDeskripsi.setText(item.getDeskripsi());
        Glide.with(context).load(item.getFoto()).into(holder.imgGaleri);

        holder.btnHapus.setOnClickListener(v -> {
            dbRef.child(item.getId()).removeValue()
                    .addOnSuccessListener(unused -> {
                        galeriList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Foto berhasil dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Gagal menghapus: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }

    @Override
    public int getItemCount() {
        return galeriList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGaleri;
        TextView txtJudul, txtDeskripsi;
        Button btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGaleri = itemView.findViewById(R.id.imgGaleri);
            txtJudul = itemView.findViewById(R.id.txtJudul);
            txtDeskripsi = itemView.findViewById(R.id.txtDeskripsi);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }
}
