package com.marketplace.connect.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "listings")
public class Listing {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String description;
    private double price;
    private String category;
    private long createdAt;
    private String imagePath;

    @Ignore
    public Listing(String title, String description, double price, String category, long createdAt) {
        this(title, description, price, category, createdAt, null);
    }

    public Listing(String title, String description, double price, String category, long createdAt, String imagePath) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.createdAt = createdAt;
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
