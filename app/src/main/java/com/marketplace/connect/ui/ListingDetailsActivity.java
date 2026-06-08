package com.marketplace.connect.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.marketplace.connect.R;
import com.marketplace.connect.data.ListingRepository;
import com.marketplace.connect.db.AppDatabase;
import com.marketplace.connect.model.Listing;

import java.text.NumberFormat;
import java.util.Locale;

public class ListingDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_LISTING_ID = "extra_listing_id";

    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        long listingId = getIntent().getLongExtra(EXTRA_LISTING_ID, -1);
        if (listingId == -1) {
            finish();
            return;
        }

        TextView titleText = findViewById(R.id.detailsTitle);
        TextView categoryText = findViewById(R.id.detailsCategory);
        TextView priceText = findViewById(R.id.detailsPrice);
        TextView descriptionText = findViewById(R.id.detailsDescription);

        ListingRepository repository = new ListingRepository(AppDatabase.getInstance(this).listingDao());
        repository.getById(listingId, listing -> runOnUiThread(() -> bindListing(listing, titleText, categoryText, priceText, descriptionText)));
    }

    private void bindListing(
            Listing listing,
            TextView titleText,
            TextView categoryText,
            TextView priceText,
            TextView descriptionText
    ) {
        if (listing == null) {
            finish();
            return;
        }

        titleText.setText(listing.getTitle());
        categoryText.setText(listing.getCategory());
        priceText.setText(numberFormat.format(listing.getPrice()));
        descriptionText.setText(listing.getDescription());
    }
}
