package com.example.ocelotnovels.utils;

import android.os.Build;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

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
    private static void qrCodeGenerator(String info) throws WriterException, IOException {
        String path = "C:/Users/Cmput301/ocelot-qr";
        BitMatrix matrix = new MultiFormatWriter().encode(info, BarcodeFormat.QR_CODE,500,500);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MatrixToImageWriter.writeToPath(matrix,"jpg", Paths.get(path));
        }
    }
}
