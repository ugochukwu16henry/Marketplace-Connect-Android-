package com.marketplace.connect.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ListingImageHelper {

    private ListingImageHelper() {
    }

    public static void bind(ImageView imageView, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imageView.setVisibility(View.GONE);
            imageView.setImageDrawable(null);
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            imageView.setVisibility(View.GONE);
            imageView.setImageDrawable(null);
            return;
        }

        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }

    public static String copyFromUri(Context context, Uri sourceUri) throws IOException {
        File picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (picturesDir == null) {
            throw new IOException("Pictures directory is unavailable.");
        }

        File destinationFile = File.createTempFile("listing_", ".jpg", picturesDir);
        try (InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {
            if (inputStream == null) {
                throw new IOException("Could not read selected image.");
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return destinationFile.getAbsolutePath();
    }
}
