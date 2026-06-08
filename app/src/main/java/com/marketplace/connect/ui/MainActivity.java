package com.marketplace.connect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marketplace.connect.R;
import com.marketplace.connect.adapter.ListingAdapter;
import com.marketplace.connect.data.ListingRepository;
import com.marketplace.connect.db.AppDatabase;
import com.marketplace.connect.model.Listing;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListingRepository repository;
    private ListingAdapter adapter;
    private TextView emptyStateText;
    private EditText searchInput;
    private Spinner categorySpinner;

    private LiveData<List<Listing>> activeSource;
    private final Observer<List<Listing>> listingObserver = listings -> {
        adapter.submitList(listings);
        boolean isEmpty = listings == null || listings.isEmpty();
        emptyStateText.setText(isEmpty ? getString(R.string.no_listings_found) : "");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new ListingRepository(AppDatabase.getInstance(this).listingDao());

        searchInput = findViewById(R.id.inputSearch);
        categorySpinner = findViewById(R.id.spinnerCategoryFilter);
        emptyStateText = findViewById(R.id.textEmptyState);

        setupCategorySpinner();
        setupRecyclerView();
        setupActions();

        applyFilters();
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_with_all,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupRecyclerView() {
        RecyclerView listingsRecycler = findViewById(R.id.recyclerListings);
        listingsRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListingAdapter(listing -> {
            Intent intent = new Intent(this, ListingDetailsActivity.class);
            intent.putExtra(ListingDetailsActivity.EXTRA_LISTING_ID, listing.getId());
            startActivity(intent);
        });

        listingsRecycler.setAdapter(adapter);
    }

    private void setupActions() {
        FloatingActionButton fabAdd = findViewById(R.id.fabAddListing);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddListingActivity.class)));

        findViewById(R.id.buttonSearch).setOnClickListener(v -> applyFilters());
    }

    private void applyFilters() {
        String query = searchInput.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();

        if (activeSource != null) {
            activeSource.removeObservers(this);
        }

        activeSource = repository.search(query, category);
        activeSource.observe(this, listingObserver);
    }
}
