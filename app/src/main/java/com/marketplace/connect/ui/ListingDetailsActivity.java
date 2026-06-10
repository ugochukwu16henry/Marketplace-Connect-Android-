package com.marketplace.connect.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.marketplace.connect.R;
import com.marketplace.connect.data.ListingRepository;
import com.marketplace.connect.db.AppDatabase;
import com.marketplace.connect.model.Listing;
import com.marketplace.connect.util.ListingImageHelper;

import java.text.NumberFormat;
import java.util.Locale;

public class ListingDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_LISTING_ID = "extra_listing_id";

    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    private ListingRepository repository;
    private long listingId = -1;
    private Listing currentListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        listingId = getIntent().getLongExtra(EXTRA_LISTING_ID, -1);
        if (listingId == -1) {
            finish();
            return;
        }

        repository = new ListingRepository(AppDatabase.getInstance(this).listingDao());

        ImageView imageView = findViewById(R.id.detailsImage);
        TextView titleText = findViewById(R.id.detailsTitle);
        TextView categoryText = findViewById(R.id.detailsCategory);
        TextView priceText = findViewById(R.id.detailsPrice);
        TextView descriptionText = findViewById(R.id.detailsDescription);
        Button deleteButton = findViewById(R.id.buttonDeleteListing);

        deleteButton.setOnClickListener(v -> confirmDelete());

        repository.getById(listingId, listing -> runOnUiThread(() ->
                bindListing(listing, imageView, titleText, categoryText, priceText, descriptionText)));
    }

    private void bindListing(
            Listing listing,
            ImageView imageView,
            TextView titleText,
            TextView categoryText,
            TextView priceText,
            TextView descriptionText
    ) {
        if (listing == null) {
            finish();
            return;
        }

        currentListing = listing;
        ListingImageHelper.bind(imageView, listing.getImagePath());
        titleText.setText(listing.getTitle());
        categoryText.setText(listing.getCategory());
        priceText.setText(numberFormat.format(listing.getPrice()));
        descriptionText.setText(listing.getDescription());
    }

    private void confirmDelete() {
        if (currentListing == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_listing_confirm_title)
                .setMessage(getString(R.string.delete_listing_confirm_message, currentListing.getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    repository.deleteById(listingId);
                    Toast.makeText(this, R.string.listing_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
