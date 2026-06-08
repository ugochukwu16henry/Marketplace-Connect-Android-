package com.marketplace.connect.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.marketplace.connect.R;
import com.marketplace.connect.data.ListingRepository;
import com.marketplace.connect.db.AppDatabase;
import com.marketplace.connect.model.Listing;

public class AddListingActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText priceInput;
    private Spinner categorySpinner;

    private ListingRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        repository = new ListingRepository(AppDatabase.getInstance(this).listingDao());

        titleInput = findViewById(R.id.inputTitle);
        descriptionInput = findViewById(R.id.inputDescription);
        priceInput = findViewById(R.id.inputPrice);
        categorySpinner = findViewById(R.id.spinnerCategory);

        setupCategorySpinner();
        findViewById(R.id.buttonSaveListing).setOnClickListener(v -> saveListing());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void saveListing() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (title.isEmpty()) {
            titleInput.setError(getString(R.string.error_title_required));
            return;
        }

        if (description.isEmpty()) {
            descriptionInput.setError(getString(R.string.error_description_required));
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            priceInput.setError(getString(R.string.error_price_invalid));
            return;
        }

        if (price <= 0) {
            priceInput.setError(getString(R.string.error_price_invalid));
            return;
        }

        Listing listing = new Listing(title, description, price, category, System.currentTimeMillis());
        repository.insert(listing);

        Toast.makeText(this, R.string.listing_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}
