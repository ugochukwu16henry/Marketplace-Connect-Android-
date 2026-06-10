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

    public ValidationResult validateAndSave(String title, String description, String priceText, String category, String imagePath) {
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

        Listing listing = new Listing(safeTitle, safeDescription, price, category, System.currentTimeMillis(), imagePath);
        repository.insert(listing);

        return ValidationResult.success();
    }

    public static class ValidationResult {
        public final boolean isSuccess;
        public final ErrorField errorField;

        private ValidationResult(boolean isSuccess, ErrorField errorField) {
            this.isSuccess = isSuccess;
            this.errorField = errorField;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, ErrorField.NONE);
        }

        public static ValidationResult titleError() {
            return new ValidationResult(false, ErrorField.TITLE);
        }

        public static ValidationResult descriptionError() {
            return new ValidationResult(false, ErrorField.DESCRIPTION);
        }

        public static ValidationResult priceError() {
            return new ValidationResult(false, ErrorField.PRICE);
        }
    }

    public enum ErrorField {
        NONE,
        TITLE,
        DESCRIPTION,
        PRICE
    }
}
