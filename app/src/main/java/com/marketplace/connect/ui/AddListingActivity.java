package com.marketplace.connect.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.marketplace.connect.R;
import com.marketplace.connect.model.Listing;
import com.marketplace.connect.util.ListingImageHelper;

import java.io.File;
import java.io.IOException;

public class AddListingActivity extends AppCompatActivity {

    public static final String EXTRA_LISTING_ID = "extra_listing_id";

    private static final String STATE_TITLE = "state_title";
    private static final String STATE_DESCRIPTION = "state_description";
    private static final String STATE_PRICE = "state_price";
    private static final String STATE_CATEGORY_POSITION = "state_category_position";
    private static final String STATE_IMAGE_PATH = "state_image_path";
    private static final String STATE_EDIT_LISTING_ID = "state_edit_listing_id";
    private static final String STATE_CREATED_AT = "state_created_at";

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText priceInput;
    private Spinner categorySpinner;
    private ImageView imagePreview;
    private TextView screenTitle;
    private Button saveButton;

    private AddListingViewModel viewModel;
    private String imagePath;
    private String savedImagePath;
    private long editListingId = -1;
    private long originalCreatedAt;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    savedImagePath = imagePath;
                    ListingImageHelper.bind(imagePreview, imagePath);
                } else {
                    imagePath = savedImagePath;
                    ListingImageHelper.bind(imagePreview, imagePath);
                    Toast.makeText(this, R.string.photo_capture_failed, Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::handlePickedImage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        viewModel = new ViewModelProvider(this).get(AddListingViewModel.class);

        titleInput = findViewById(R.id.inputTitle);
        descriptionInput = findViewById(R.id.inputDescription);
        priceInput = findViewById(R.id.inputPrice);
        categorySpinner = findViewById(R.id.spinnerCategory);
        imagePreview = findViewById(R.id.imagePreview);
        screenTitle = findViewById(R.id.textScreenTitle);
        saveButton = findViewById(R.id.buttonSaveListing);
        Button takePhotoButton = findViewById(R.id.buttonTakePhoto);
        Button chooseGalleryButton = findViewById(R.id.buttonChooseGallery);

        setupCategorySpinner();
        takePhotoButton.setOnClickListener(v -> requestCameraAndCapture());
        chooseGalleryButton.setOnClickListener(v -> launchGalleryPicker());
        saveButton.setOnClickListener(v -> saveListing());

        if (savedInstanceState != null) {
            restoreFormState(savedInstanceState);
        } else {
            editListingId = getIntent().getLongExtra(EXTRA_LISTING_ID, -1);
            if (isEditMode()) {
                configureEditMode();
                loadListingForEdit();
            }
        }
    }

    private void configureEditMode() {
        screenTitle.setText(R.string.edit_listing);
        saveButton.setText(R.string.update_listing);
    }

    private void loadListingForEdit() {
        viewModel.loadListing(editListingId, listing -> runOnUiThread(() -> populateForm(listing)));
    }

    private void populateForm(Listing listing) {
        if (listing == null) {
            Toast.makeText(this, R.string.no_listings_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        originalCreatedAt = listing.getCreatedAt();
        titleInput.setText(listing.getTitle());
        descriptionInput.setText(listing.getDescription());
        priceInput.setText(String.valueOf(listing.getPrice()));
        setCategorySelection(listing.getCategory());
        imagePath = listing.getImagePath();
        savedImagePath = imagePath;
        ListingImageHelper.bind(imagePreview, imagePath);
    }

    private void setCategorySelection(String category) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) categorySpinner.getAdapter();
        if (adapter == null) {
            return;
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            if (category.equals(String.valueOf(adapter.getItem(i)))) {
                categorySpinner.setSelection(i);
                return;
            }
        }
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                R.layout.spinner_item
        );
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void requestCameraAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchGalleryPicker() {
        pickImageLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    private void handlePickedImage(Uri imageUri) {
        if (imageUri == null) {
            return;
        }

        try {
            imagePath = ListingImageHelper.copyFromUri(this, imageUri);
            savedImagePath = imagePath;
            ListingImageHelper.bind(imagePreview, imagePath);
        } catch (IOException e) {
            imagePath = savedImagePath;
            ListingImageHelper.bind(imagePreview, imagePath);
            Toast.makeText(this, R.string.photo_upload_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void launchCamera() {
        try {
            File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (picturesDir == null) {
                Toast.makeText(this, R.string.photo_capture_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            File imageFile = File.createTempFile("listing_", ".jpg", picturesDir);
            imagePath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    imageFile
            );
            takePictureLauncher.launch(imageUri);
        } catch (IOException e) {
            imagePath = null;
            Toast.makeText(this, R.string.photo_capture_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveListing() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        AddListingViewModel.ValidationResult result;
        if (isEditMode()) {
            result = viewModel.validateAndUpdate(
                    editListingId,
                    originalCreatedAt,
                    title,
                    description,
                    priceText,
                    category,
                    imagePath
            );
        } else {
            result = viewModel.validateAndSave(title, description, priceText, category, imagePath);
        }

        if (!result.isSuccess) {
            if (result.errorField == AddListingViewModel.ErrorField.TITLE) {
                titleInput.setError(getString(R.string.error_title_required));
            } else if (result.errorField == AddListingViewModel.ErrorField.DESCRIPTION) {
                descriptionInput.setError(getString(R.string.error_description_required));
            } else if (result.errorField == AddListingViewModel.ErrorField.PRICE) {
                priceInput.setError(getString(R.string.error_price_invalid));
            }
            return;
        }

        int messageRes = isEditMode() ? R.string.listing_updated : R.string.listing_saved;
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isEditMode() {
        return editListingId != -1;
    }

    private void restoreFormState(Bundle savedInstanceState) {
        titleInput.setText(savedInstanceState.getString(STATE_TITLE, ""));
        descriptionInput.setText(savedInstanceState.getString(STATE_DESCRIPTION, ""));
        priceInput.setText(savedInstanceState.getString(STATE_PRICE, ""));
        categorySpinner.setSelection(savedInstanceState.getInt(STATE_CATEGORY_POSITION, 0));
        imagePath = savedInstanceState.getString(STATE_IMAGE_PATH);
        savedImagePath = imagePath;
        editListingId = savedInstanceState.getLong(STATE_EDIT_LISTING_ID, -1);
        originalCreatedAt = savedInstanceState.getLong(STATE_CREATED_AT, System.currentTimeMillis());
        ListingImageHelper.bind(imagePreview, imagePath);

        if (isEditMode()) {
            configureEditMode();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_TITLE, titleInput.getText().toString());
        outState.putString(STATE_DESCRIPTION, descriptionInput.getText().toString());
        outState.putString(STATE_PRICE, priceInput.getText().toString());
        outState.putInt(STATE_CATEGORY_POSITION, categorySpinner.getSelectedItemPosition());
        outState.putString(STATE_IMAGE_PATH, imagePath);
        outState.putLong(STATE_EDIT_LISTING_ID, editListingId);
        outState.putLong(STATE_CREATED_AT, originalCreatedAt);
    }
}
