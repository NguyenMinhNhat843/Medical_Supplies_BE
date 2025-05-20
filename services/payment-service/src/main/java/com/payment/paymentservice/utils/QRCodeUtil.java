package com.payment.paymentservice.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QRCodeUtil {
    public static String generateQRCodeBase64(String text) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 250, 250);
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            byte[] qrBytes = out.toByteArray();
            return Base64.getEncoder().encodeToString(qrBytes);
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo mã QR", e);
        }
    }
}
