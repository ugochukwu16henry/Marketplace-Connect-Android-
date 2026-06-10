package com.marketplace.connect.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.marketplace.connect.data.ListingRepository;
import com.marketplace.connect.db.AppDatabase;
import com.marketplace.connect.model.Listing;

public class AddListingViewModel extends AndroidViewModel {

    private final ListingRepository repository;

    public AddListingViewModel(@NonNull Application application) {
        super(application);
        repository = new ListingRepository(AppDatabase.getInstance(application).listingDao());
    }

    public void loadListing(long listingId, ListingRepository.ListingCallback callback) {
        repository.getById(listingId, callback);
    }

    public ValidationResult validateAndSave(String title, String description, String priceText, String category, String imagePath) {
        ValidationResult validation = validateInput(title, description, priceText);
        if (!validation.isSuccess) {
            return validation;
        }

        Listing listing = new Listing(
                validation.parsedTitle,
                validation.parsedDescription,
                validation.parsedPrice,
                category,
                System.currentTimeMillis(),
                imagePath
        );
        repository.insert(listing);
        return ValidationResult.success();
    }

    public ValidationResult validateAndUpdate(
            long listingId,
            long createdAt,
            String title,
            String description,
            String priceText,
            String category,
            String imagePath
    ) {
        ValidationResult validation = validateInput(title, description, priceText);
        if (!validation.isSuccess) {
            return validation;
        }

        Listing listing = new Listing(
                validation.parsedTitle,
                validation.parsedDescription,
                validation.parsedPrice,
                category,
                createdAt,
                imagePath
        );
        listing.setId(listingId);
        repository.update(listing);
        return ValidationResult.success();
    }

    private ValidationResult validateInput(String title, String description, String priceText) {
        String safeTitle = title == null ? "" : title.trim();
        String safeDescription = description == null ? "" : description.trim();
        String safePrice = priceText == null ? "" : priceText.trim();

        if (safeTitle.isEmpty()) {
            return ValidationResult.titleError();
        }

        if (safeDescription.isEmpty()) {
            return ValidationResult.descriptionError();
        }

        double price;
        try {
            price = Double.parseDouble(safePrice);
        } catch (NumberFormatException e) {
            return ValidationResult.priceError();
        }

        if (price <= 0) {
            return ValidationResult.priceError();
        }

        return ValidationResult.valid(safeTitle, safeDescription, price);
    }

    public static class ValidationResult {
        public final boolean isSuccess;
        public final ErrorField errorField;
        public final String parsedTitle;
        public final String parsedDescription;
        public final double parsedPrice;

        private ValidationResult(
                boolean isSuccess,
                ErrorField errorField,
                String parsedTitle,
                String parsedDescription,
                double parsedPrice
        ) {
            this.isSuccess = isSuccess;
            this.errorField = errorField;
            this.parsedTitle = parsedTitle;
            this.parsedDescription = parsedDescription;
            this.parsedPrice = parsedPrice;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, ErrorField.NONE, "", "", 0);
        }

        public static ValidationResult valid(String title, String description, double price) {
            return new ValidationResult(true, ErrorField.NONE, title, description, price);
        }

        public static ValidationResult titleError() {
            return new ValidationResult(false, ErrorField.TITLE, "", "", 0);
        }

        public static ValidationResult descriptionError() {
            return new ValidationResult(false, ErrorField.DESCRIPTION, "", "", 0);
        }

        public static ValidationResult priceError() {
            return new ValidationResult(false, ErrorField.PRICE, "", "", 0);
        }
    }

    public enum ErrorField {
        NONE,
        TITLE,
        DESCRIPTION,
        PRICE
    }
}
