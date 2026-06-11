package com.example.mobilprogramlamaodev;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    private List<Asset> assetList;
    private List<Asset> filteredList;
    private DataManager dataManager;

    public MarketAdapter(List<Asset> assetList, DataManager dataManager) {
        this.assetList = assetList;
        this.dataManager = dataManager;
        updateFilteredList("");
    }

    private void updateFilteredList(String query) {
        filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            // Favoriler önce, geri kalanlar sonra
            List<String> favs = dataManager.getFavorites();
            for (Asset asset : assetList) {
                if (favs.contains(asset.getSymbol())) filteredList.add(asset);
            }
            for (Asset asset : assetList) {
                if (!favs.contains(asset.getSymbol())) filteredList.add(asset);
            }
        } else {
            boolean found = false;
            for (Asset asset : assetList) {
                if (asset.getName().toLowerCase().contains(query.toLowerCase()) ||
                    asset.getSymbol().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(asset);
                    found = true;
                }
            }
            if (!found && query.contains(":") && query.length() > 5) {
                Asset newAsset = new Asset(query, query, 0.0, 0.0);
                filteredList.add(newAsset);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Asset asset = filteredList.get(position);
        holder.txtName.setText(asset.getName());
        holder.txtSymbol.setText(asset.getSymbol());
        
        String currency = asset.getSymbol().contains("TRY") ? "₺" : "$";
        holder.txtPrice.setText(currency + String.format("%.2f", asset.getPrice()));
        
        holder.txtChange.setText(String.format("%+.2f%%", asset.getChange()));
        holder.txtChange.setTextColor(asset.getChange() >= 0 ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

        boolean isFav = dataManager.isFavorite(asset.getSymbol());
        holder.btnFavorite.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        holder.btnFavorite.setOnClickListener(v -> {
            if (dataManager.isFavorite(asset.getSymbol())) {
                dataManager.removeFavorite(asset.getSymbol());
            } else {
                dataManager.addFavorite(asset.getSymbol());
            }
            notifyItemChanged(position);
            // Eğer favoriler listesindeysek ve favoriden çıkarıldıysa listeyi yenile
            if (searchViewQuery.isEmpty()) {
                filter("");
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TradeActivity.class);
            intent.putExtra("ASSET_SYMBOL", asset.getSymbol());
            intent.putExtra("ASSET_NAME", asset.getName());
            v.getContext().startActivity(intent);
        });
    }

    private String searchViewQuery = "";

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        this.searchViewQuery = query;
        updateFilteredList(query);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSymbol, txtPrice, txtChange;
        ImageButton btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtItemName);
            txtSymbol = itemView.findViewById(R.id.txtItemSymbol);
            txtPrice = itemView.findViewById(R.id.txtItemPrice);
            txtChange = itemView.findViewById(R.id.txtItemChange);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
