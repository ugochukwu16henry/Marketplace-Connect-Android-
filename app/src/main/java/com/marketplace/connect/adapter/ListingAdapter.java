package com.marketplace.connect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marketplace.connect.R;
import com.marketplace.connect.model.Listing;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private final List<Listing> listings = new ArrayList<>();
    private final OnListingClickListener clickListener;
    private final OnListingDeleteListener deleteListener;
    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    public ListingAdapter(OnListingClickListener clickListener, OnListingDeleteListener deleteListener) {
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    public void submitList(List<Listing> newItems) {
        listings.clear();
        if (newItems != null) {
            listings.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        holder.bind(listings.get(position));
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    class ListingViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final TextView categoryText;
        private final TextView priceText;
        private final ImageButton deleteButton;

        ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.textTitle);
            categoryText = itemView.findViewById(R.id.textCategory);
            priceText = itemView.findViewById(R.id.textPrice);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }

        void bind(Listing listing) {
            titleText.setText(listing.getTitle());
            categoryText.setText(listing.getCategory());
            priceText.setText(numberFormat.format(listing.getPrice()));

            itemView.setOnClickListener(v -> clickListener.onClicked(listing));
            deleteButton.setOnClickListener(v -> deleteListener.onDeleteRequested(listing));
        }
    }

    public interface OnListingClickListener {
        void onClicked(Listing listing);
    }

    public interface OnListingDeleteListener {
        void onDeleteRequested(Listing listing);
    }
}
