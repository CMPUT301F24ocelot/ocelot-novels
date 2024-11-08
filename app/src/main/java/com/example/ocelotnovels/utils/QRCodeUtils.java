package com.example.ocelotnovels.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.os.Build;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class QRCodeUtils {
    /**
     * Generates a QR code from the info String passed to the method.
     * Writes QR code as a jpg to local path. (To Be Changed to FireBase)
     * @param info
     * @throws WriterException
     * @throws IOException
     */
    public static String qrCodeGenerator(String info) throws WriterException, IOException {
        String path = "C:/Users/Cmput301/ocelot-qr";
        try{
            BitMatrix matrix = new MultiFormatWriter().encode(info, BarcodeFormat.QR_CODE,500,500);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MatrixToImageWriter.writeToPath(matrix,"jpg", Paths.get(path));
            }
        } catch (WriterException | IOException e){
            e.printStackTrace();
        }
        return path;

    }

    public static Bitmap generateQrCode(String content, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565);

        for (int x = 0; x < bitMatrixWidth; x++) {
            for (int y = 0; y < bitMatrixHeight; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }



}
