package com.marketplace.connect.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.marketplace.connect.R;

public class AddListingActivity extends AppCompatActivity {

    private static final String STATE_TITLE = "state_title";
    private static final String STATE_DESCRIPTION = "state_description";
    private static final String STATE_PRICE = "state_price";
    private static final String STATE_CATEGORY_POSITION = "state_category_position";

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText priceInput;
    private Spinner categorySpinner;

    private AddListingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        viewModel = new ViewModelProvider(this).get(AddListingViewModel.class);

        titleInput = findViewById(R.id.inputTitle);
        descriptionInput = findViewById(R.id.inputDescription);
        priceInput = findViewById(R.id.inputPrice);
        categorySpinner = findViewById(R.id.spinnerCategory);

        setupCategorySpinner();

        if (savedInstanceState != null) {
            titleInput.setText(savedInstanceState.getString(STATE_TITLE, ""));
            descriptionInput.setText(savedInstanceState.getString(STATE_DESCRIPTION, ""));
            priceInput.setText(savedInstanceState.getString(STATE_PRICE, ""));
            categorySpinner.setSelection(savedInstanceState.getInt(STATE_CATEGORY_POSITION, 0));
        }

        findViewById(R.id.buttonSaveListing).setOnClickListener(v -> saveListing());
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

    private void saveListing() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        AddListingViewModel.ValidationResult result = viewModel.validateAndSave(title, description, priceText, category);
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

        Toast.makeText(this, R.string.listing_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_TITLE, titleInput.getText().toString());
        outState.putString(STATE_DESCRIPTION, descriptionInput.getText().toString());
        outState.putString(STATE_PRICE, priceInput.getText().toString());
        outState.putInt(STATE_CATEGORY_POSITION, categorySpinner.getSelectedItemPosition());
    }
}
