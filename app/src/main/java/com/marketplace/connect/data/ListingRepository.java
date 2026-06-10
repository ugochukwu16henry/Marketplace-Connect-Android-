package com.marketplace.connect.data;

import androidx.lifecycle.LiveData;

import com.marketplace.connect.db.ListingDao;
import com.marketplace.connect.model.Listing;
import com.marketplace.connect.util.DatabaseExecutor;

import java.util.List;

public class ListingRepository {

    private final ListingDao listingDao;

    public ListingRepository(ListingDao listingDao) {
        this.listingDao = listingDao;
    }

    public LiveData<List<Listing>> getAllListings() {
        return listingDao.getAllListings();
    }

    public LiveData<List<Listing>> search(String query, String category) {
        return listingDao.search(query == null ? "" : query.trim(), category == null ? "All" : category);
    }

    public void insert(Listing listing) {
        DatabaseExecutor.run(() -> listingDao.insert(listing));
    }

    public void update(Listing listing) {
        DatabaseExecutor.run(() -> listingDao.insert(listing));
    }

    public void seedDemoDataIfEmpty(List<Listing> listings) {
        DatabaseExecutor.run(() -> {
            if (listingDao.count() == 0 && listings != null && !listings.isEmpty()) {
                listingDao.insertAll(listings);
            }
        });
    }

    public void getById(long id, ListingCallback callback) {
        DatabaseExecutor.run(() -> {
            Listing listing = listingDao.getById(id);
            callback.onLoaded(listing);
        });
    }

    public void deleteById(long id) {
        DatabaseExecutor.run(() -> listingDao.deleteById(id));
    }

    public interface ListingCallback {
        void onLoaded(Listing listing);
    }
}
