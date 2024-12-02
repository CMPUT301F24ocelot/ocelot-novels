/**
 * This utility class provides methods for generating QR codes.
 * It includes functionality to:
 * 1. Generate a QR code from a string and save it as a JPG file to a specified path.
 * 2. Generate a QR code as a Bitmap image for use in Android applications.
 *
 * The utility leverages the ZXing library to create QR codes and handle various
 * barcode formats. It also includes error handling for scenarios such as file writing
 * and QR code generation issues.
 */

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
     * Generates a QR code from the provided info string and writes it as a JPG file
     * to a local path. Currently writes to the hardcoded path "C:/Users/Cmput301/ocelot-qr".
     * This method should ideally be updated to store the QR code on Firebase or another dynamic location.
     *
     * @param info the content to encode in the QR code
     * @return the local path where the QR code is saved
     * @throws WriterException if an error occurs during the QR code generation
     * @throws IOException if an error occurs during file writing
     */
    public static String qrCodeGenerator(String info) throws WriterException, IOException {
        String path = "C:/Users/Cmput301/ocelot-qr";
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(info, BarcodeFormat.QR_CODE, 500, 500);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MatrixToImageWriter.writeToPath(matrix, "jpg", Paths.get(path));
            }
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Generates a QR code as a Bitmap image from the provided content string.
     *
     * @param content the content to encode in the QR code
     * @param width the width of the QR code image
     * @param height the height of the QR code image
     * @return a Bitmap representation of the generated QR code
     * @throws WriterException if an error occurs during the QR code generation
     */
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
