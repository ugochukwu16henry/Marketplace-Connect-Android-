package com.marketplace.connect.util;

import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

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
}
