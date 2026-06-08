package com.marketplace.connect.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.marketplace.connect.data.ListingRepository;
import com.marketplace.connect.db.AppDatabase;
import com.marketplace.connect.model.Listing;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final ListingRepository repository;
    private final MutableLiveData<FilterState> filters = new MutableLiveData<>(new FilterState("", "All"));
    private final LiveData<List<Listing>> listings;
    private boolean seeded;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new ListingRepository(AppDatabase.getInstance(application).listingDao());
        listings = Transformations.switchMap(filters, state -> repository.search(state.query, state.category));
    }

    public LiveData<List<Listing>> getListings() {
        return listings;
    }

    public void applyFilters(String query, String category) {
        filters.setValue(new FilterState(
                query == null ? "" : query.trim(),
                category == null || category.trim().isEmpty() ? "All" : category
        ));
    }

    public String getCurrentQuery() {
        FilterState state = filters.getValue();
        return state == null ? "" : state.query;
    }

    public String getCurrentCategory() {
        FilterState state = filters.getValue();
        return state == null ? "All" : state.category;
    }

    public void ensureSeededData() {
        if (seeded) {
            return;
        }

        seeded = true;
        repository.seedDemoDataIfEmpty(buildDemoListings());
    }

    private List<Listing> buildDemoListings() {
        long now = System.currentTimeMillis();
        List<Listing> items = new ArrayList<>();
        items.add(new Listing("Samsung A54", "Neat condition, 128GB, charger included.", 260.00, "Electronics", now - 60000));
        items.add(new Listing("Nike Running Shoes", "Size 43, lightly used for two weeks.", 55.00, "Fashion", now - 50000));
        items.add(new Listing("Wooden Study Desk", "Strong and clean, suitable for home office.", 120.00, "Home", now - 40000));
        return items;
    }

    private static class FilterState {
        private final String query;
        private final String category;

        private FilterState(String query, String category) {
            this.query = query;
            this.category = category;
        }
    }
}
