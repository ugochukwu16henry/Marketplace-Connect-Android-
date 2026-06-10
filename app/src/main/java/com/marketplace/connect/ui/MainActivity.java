package com.marketplace.connect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private static final String STATE_SORT_POSITION = "state_sort_position";

    private MainViewModel viewModel;
    private ListingAdapter adapter;
    private TextView emptyStateText;
    private EditText searchInput;
    private Spinner categorySpinner;
    private Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        searchInput = findViewById(R.id.inputSearch);
        categorySpinner = findViewById(R.id.spinnerCategoryFilter);
        sortSpinner = findViewById(R.id.spinnerSort);
        emptyStateText = findViewById(R.id.textEmptyState);

        setupCategorySpinner();
        setupSortSpinner();
        setupRecyclerView();
        setupActions();

        if (savedInstanceState != null) {
            searchInput.setText(savedInstanceState.getString(STATE_QUERY, ""));
            int categoryPosition = savedInstanceState.getInt(STATE_CATEGORY_POSITION, 0);
            categorySpinner.setSelection(categoryPosition);
            int sortPosition = savedInstanceState.getInt(STATE_SORT_POSITION, 0);
            sortSpinner.setSelection(sortPosition);
        } else {
            searchInput.setText(viewModel.getCurrentQuery());
            setCategoryByValue(viewModel.getCurrentCategory());
            setSortByOption(viewModel.getCurrentSortOption());
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
                R.layout.spinner_item
        );
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupRecyclerView() {
        RecyclerView listingsRecycler = findViewById(R.id.recyclerListings);
        listingsRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListingAdapter(
                listing -> {
                    Intent intent = new Intent(this, ListingDetailsActivity.class);
                    intent.putExtra(ListingDetailsActivity.EXTRA_LISTING_ID, listing.getId());
                    startActivity(intent);
                },
                this::openEditListing,
                this::confirmDeleteListing
        );

        listingsRecycler.setAdapter(adapter);
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_options,
                R.layout.spinner_item
        );
        sortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
    }

    private void setupActions() {
        FloatingActionButton fabAdd = findViewById(R.id.fabAddListing);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddListingActivity.class)));

        findViewById(R.id.buttonSearch).setOnClickListener(v -> applyFilters());
    }

    private void applyFilters() {
        String query = searchInput.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        MainViewModel.SortOption sortOption = parseSortOption(sortSpinner.getSelectedItemPosition());
        viewModel.applyFilters(query, category, sortOption);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_QUERY, searchInput.getText().toString());
        outState.putInt(STATE_CATEGORY_POSITION, categorySpinner.getSelectedItemPosition());
        outState.putInt(STATE_SORT_POSITION, sortSpinner.getSelectedItemPosition());
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

    private MainViewModel.SortOption parseSortOption(int position) {
        if (position == 1) {
            return MainViewModel.SortOption.PRICE_LOW_HIGH;
        }
        if (position == 2) {
            return MainViewModel.SortOption.PRICE_HIGH_LOW;
        }
        return MainViewModel.SortOption.NEWEST;
    }

    private void openEditListing(Listing listing) {
        Intent intent = new Intent(this, AddListingActivity.class);
        intent.putExtra(AddListingActivity.EXTRA_LISTING_ID, listing.getId());
        startActivity(intent);
    }

    private void confirmDeleteListing(Listing listing) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_listing_confirm_title)
                .setMessage(getString(R.string.delete_listing_confirm_message, listing.getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteListing(listing.getId());
                    Toast.makeText(this, R.string.listing_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void setSortByOption(MainViewModel.SortOption sortOption) {
        if (sortOption == MainViewModel.SortOption.PRICE_LOW_HIGH) {
            sortSpinner.setSelection(1);
            return;
        }
        if (sortOption == MainViewModel.SortOption.PRICE_HIGH_LOW) {
            sortSpinner.setSelection(2);
            return;
        }
        sortSpinner.setSelection(0);
    }
}
