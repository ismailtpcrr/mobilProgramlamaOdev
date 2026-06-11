package com.example.mobilprogramlamaodev;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerMarket;
    private MarketAdapter adapter;
    private List<Asset> assetList;
    private SearchView searchView;

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = new DataManager(this);
        recyclerMarket = findViewById(R.id.recyclerMarket);
        searchView = findViewById(R.id.searchView);

        setupAssetList();

        recyclerMarket.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarketAdapter(assetList, dataManager);
        recyclerMarket.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_market);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_market) return true;
            if (id == R.id.nav_trade) {
                startActivity(new Intent(MainActivity.this, TradeActivity.class));
                return true;
            }
            if (id == R.id.nav_active_trades) {
                startActivity(new Intent(MainActivity.this, ActiveTradesActivity.class));
                return true;
            }
            if (id == R.id.nav_portfolio) {
                startActivity(new Intent(MainActivity.this, PortfolioActivity.class));
                return true;
            }
            return false;
        });

        updatePrices();
    }

    private void updatePrices() {
        for (Asset asset : assetList) {
            PriceService.getInstance().fetchPrice(asset.getSymbol(), (price, change) -> {
                if (price > 0) {
                    asset.setPrice(price);
                    asset.setChange(change);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            });
        }
    }

    private void setupAssetList() {
        assetList = new ArrayList<>();
        assetList.add(new Asset("Bitcoin", "BINANCE:BTCUSDT", 0.0, 0.0));
        assetList.add(new Asset("Ethereum", "BINANCE:ETHUSDT", 0.0, 0.0));
        assetList.add(new Asset("BNB", "BINANCE:BNBUSDT", 0.0, 0.0));
        assetList.add(new Asset("Solana", "BINANCE:SOLUSDT", 0.0, 0.0));
        assetList.add(new Asset("XRP", "BINANCE:XRPUSDT", 0.0, 0.0));
        assetList.add(new Asset("Cardano", "BINANCE:ADAUSDT", 0.0, 0.0));
        assetList.add(new Asset("Avax", "BINANCE:AVAXUSDT", 0.0, 0.0));
        assetList.add(new Asset("Dogecoin", "BINANCE:DOGEUSDT", 0.0, 0.0));
        assetList.add(new Asset("Gram Altın", "FX:XAUUSD", 2350.0, 0.0));
        assetList.add(new Asset("Dolar/TL", "FX:USDTRY", 32.45, 0.0));
        assetList.add(new Asset("Euro/TL", "FX:EURTRY", 35.10, 0.0));
        assetList.add(new Asset("Nvidia", "NASDAQ:NVDA", 0.0, 0.0));
        assetList.add(new Asset("Apple", "NASDAQ:AAPL", 0.0, 0.0));
        assetList.add(new Asset("Tesla", "NASDAQ:TSLA", 0.0, 0.0));
    }
}
