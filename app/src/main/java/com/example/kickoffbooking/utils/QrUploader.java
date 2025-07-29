package com.example.kickoffbooking.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class QrUploader {

    public interface UploadCallback {
        void onSuccess(String qrUrl);
        void onFailure(String error);
    }

    public static void uploadQrToImgbb(String content, UploadCallback callback) {
        new Thread(() -> {
            try {
                // Generate QR
                Bitmap qrBitmap = generateQrCode(content);

                // Kompres ke PNG dan encode base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                // Encode base64 agar URL-safe
                String encodedImage = java.net.URLEncoder.encode(imageBase64, "UTF-8");

                // Upload ke imgbb
                URL url = new URL("https://api.imgbb.com/1/upload?key=c5c01e442ea9bde4dbe044f65c0d4b7c");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "image=" + encodedImage;
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder result = new StringBuilder();
                    try (java.util.Scanner scanner = new java.util.Scanner(conn.getInputStream())) {
                        while (scanner.hasNextLine()) {
                            result.append(scanner.nextLine());
                        }
                    }

                    JSONObject json = new JSONObject(result.toString());
                    String imageUrl = json.getJSONObject("data").getString("url");

                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onSuccess(imageUrl);
                    });

                } else {
                    // Baca error
                    StringBuilder errorResult = new StringBuilder();
                    try (java.util.Scanner scanner = new java.util.Scanner(conn.getErrorStream())) {
                        while (scanner.hasNextLine()) {
                            errorResult.append(scanner.nextLine());
                        }
                    }
                    Log.e("QrUploader", "Upload failed. Code: " + responseCode + ", Error: " + errorResult);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onFailure("Upload failed. Code: " + responseCode);
                    });
                }

            } catch (Exception e) {
                Log.e("QrUploader", "Upload Exception: ", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onFailure("Exception: " + e.getMessage());
                });
            }
        }).start();
    }

    private static Bitmap generateQrCode(String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        int size = 300;
        com.google.zxing.common.BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
