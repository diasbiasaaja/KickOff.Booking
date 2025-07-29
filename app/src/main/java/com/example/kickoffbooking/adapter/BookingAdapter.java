package com.example.kickoffbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kickoffbooking.R;
import com.example.kickoffbooking.model.bookingitem;
import com.example.kickoffbooking.model.ChatItem;
import com.example.kickoffbooking.utils.QrUploader;
import com.google.firebase.database.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private Context context;
    private List<bookingitem> bookingList;

    public BookingAdapter(Context context, List<bookingitem> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position) {
        bookingitem item = bookingList.get(position);

        holder.txtNama.setText("Nama: " + (item.getNama() != null ? item.getNama() : "-"));
        holder.txtClub.setText("Club: " + (item.getClub() != null ? item.getClub() : "-"));
        holder.txtTanggal.setText("Tanggal: " + (item.getTanggal() != null ? item.getTanggal() : "-"));
        holder.txtLapangan.setText("Lapangan: " + (item.getLapangan() != null ? item.getLapangan() : "-"));
        holder.txtJam.setText("Jam: " + (item.getJam() != null ? item.getJam().toString() : "-"));
        holder.txtStatus.setText("Status: " + (item.getStatus() != null ? item.getStatus() : "-"));

        String totalFormatted = NumberFormat.getCurrencyInstance(new Locale("id", "ID"))
                .format(item.getTotal());
        holder.txtTotal.setText("Total: " + totalFormatted);

        if (item.getBuktiPembayaran() != null && !item.getBuktiPembayaran().isEmpty()) {
            Glide.with(context).load(item.getBuktiPembayaran()).into(holder.imgBukti);
        } else {
            holder.imgBukti.setImageResource(R.drawable.codeqr); // default
        }

        // ✅ APPROVE Booking: Upload QR ke Imgbb + kirim ke Firebase Chat
        holder.btnApprove.setOnClickListener(v -> {
            String uid = item.getUid();
            String bookingId = item.getId();
            String isiQr = "Club: " + item.getClub()
                    + "\nTanggal: " + item.getTanggal()
                    + "\nJam: " + item.getJam()
                    + "\nLapangan: " + item.getLapangan();
            String pesan = "Pesanan kamu sudah valid! Tunjukkan QR ini saat di lapangan.";

            QrUploader.uploadQrToImgbb(isiQr, new QrUploader.UploadCallback() {
                @Override
                public void onSuccess(String qrUrl) {
                    DatabaseReference bookingRef = FirebaseDatabase.getInstance()
                            .getReference("booking")
                            .child(uid)
                            .child(bookingId);
                    bookingRef.child("status").setValue("approved");

                    ChatItem chat = new ChatItem(pesan, "approved", qrUrl);
                    FirebaseDatabase.getInstance()
                            .getReference("chat")
                            .child(uid)
                            .push()
                            .setValue(chat);

                    Toast.makeText(context, "QR dikirim & booking disetujui", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Gagal upload QR: " + error, Toast.LENGTH_SHORT).show()
                        );
                    }
                }

            });
        });

        // ❌ REJECT Booking
        holder.btnReject.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("booking")
                    .child(item.getUid())
                    .child(item.getId());

            ref.removeValue().addOnSuccessListener(unused -> {
                ChatItem chat = new ChatItem("Maaf, bookingan Anda gagal dan tidak valid", "rejected", null);
                FirebaseDatabase.getInstance()
                        .getReference("chat")
                        .child(item.getUid())
                        .push()
                        .setValue(chat);

                Toast.makeText(context, "Booking ditolak & pesan dikirim", Toast.LENGTH_SHORT).show();
                bookingList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, bookingList.size());
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Gagal menghapus booking", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtClub, txtTanggal, txtLapangan, txtJam, txtTotal, txtStatus;
        ImageView imgBukti;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtClub = itemView.findViewById(R.id.txtClub);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtLapangan = itemView.findViewById(R.id.txtLapangan);
            txtJam = itemView.findViewById(R.id.txtJam);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgBukti = itemView.findViewById(R.id.imgBukti);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnTolak);
        }
    }
}
