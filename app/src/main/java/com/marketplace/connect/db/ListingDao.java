package com.marketplace.connect.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.marketplace.connect.model.Listing;

import java.util.List;

@Dao
public interface ListingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Listing listing);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Listing> listings);

    @Query("SELECT * FROM listings ORDER BY createdAt DESC")
    LiveData<List<Listing>> getAllListings();

    @Query("SELECT * FROM listings WHERE id = :listingId LIMIT 1")
    Listing getById(long listingId);

    @Query("SELECT COUNT(*) FROM listings")
    int count();

    @Query("SELECT * FROM listings " +
            "WHERE (:searchQuery = '' OR title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%') " +
            "AND (:category = 'All' OR category = :category) " +
            "ORDER BY createdAt DESC")
    LiveData<List<Listing>> search(String searchQuery, String category);

    @Query("DELETE FROM listings WHERE id = :listingId")
    void deleteById(long listingId);
}
