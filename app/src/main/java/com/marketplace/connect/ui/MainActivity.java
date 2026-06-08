package com.marketplace.connect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marketplace.connect.R;
import com.marketplace.connect.adapter.ListingAdapter;
import com.marketplace.connect.model.Listing;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_QUERY = "state_query";
    private static final String STATE_CATEGORY_POSITION = "state_category_position";

    private MainViewModel viewModel;
    private ListingAdapter adapter;
    private TextView emptyStateText;
    private EditText searchInput;
    private Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        searchInput = findViewById(R.id.inputSearch);
        categorySpinner = findViewById(R.id.spinnerCategoryFilter);
        emptyStateText = findViewById(R.id.textEmptyState);

        setupCategorySpinner();
        setupRecyclerView();
        setupActions();

        if (savedInstanceState != null) {
            searchInput.setText(savedInstanceState.getString(STATE_QUERY, ""));
            int categoryPosition = savedInstanceState.getInt(STATE_CATEGORY_POSITION, 0);
            categorySpinner.setSelection(categoryPosition);
        } else {
            searchInput.setText(viewModel.getCurrentQuery());
            setCategoryByValue(viewModel.getCurrentCategory());
        }

        viewModel.getListings().observe(this, listings -> {
            adapter.submitList(listings);
            boolean isEmpty = listings == null || listings.isEmpty();
            emptyStateText.setText(isEmpty ? getString(R.string.no_listings_found) : "");
        });

        viewModel.ensureSeededData();
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
        viewModel.applyFilters(query, category);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_QUERY, searchInput.getText().toString());
        outState.putInt(STATE_CATEGORY_POSITION, categorySpinner.getSelectedItemPosition());
    }

    private void setCategoryByValue(String categoryValue) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) categorySpinner.getAdapter();
        if (adapter == null) {
            return;
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            if (categoryValue.equals(String.valueOf(adapter.getItem(i)))) {
                categorySpinner.setSelection(i);
                return;
            }
        }
    }
}
