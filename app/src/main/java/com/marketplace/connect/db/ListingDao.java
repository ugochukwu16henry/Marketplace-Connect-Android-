package com.marketplace.connect.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.marketplace.connect.model.Listing;

import java.util.List;

@Dao
public interface ListingDao {

    @Insert
    void insert(Listing listing);

    @Query("SELECT * FROM listings ORDER BY createdAt DESC")
    LiveData<List<Listing>> getAllListings();

    @Query("SELECT * FROM listings WHERE id = :listingId LIMIT 1")
    Listing getById(long listingId);

    @Query("SELECT * FROM listings " +
            "WHERE (:searchQuery = '' OR title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%') " +
            "AND (:category = 'All' OR category = :category) " +
            "ORDER BY createdAt DESC")
    LiveData<List<Listing>> search(String searchQuery, String category);
}
