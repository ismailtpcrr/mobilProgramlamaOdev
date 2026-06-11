package com.example.mobilprogramlamaodev;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;

public class ActiveTradesActivity extends AppCompatActivity {

    private RecyclerView recyclerActiveTrades;
    private DataManager dataManager;
    private PortfolioAdapter adapter;
    private final Handler plHandler = new Handler();
    private final Runnable plRunnable = new Runnable() {
        @Override public void run() {
            // Adapter varsa fiyatları yeniden çek (scroll pozisyonunu bozmadan)
            if (adapter != null) adapter.notifyDataSetChanged();
            plHandler.postDelayed(this, 10000); // 10 saniyede bir P&L güncelle
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_trades);

        dataManager = new DataManager(this);
        recyclerActiveTrades = findViewById(R.id.recyclerActiveTrades);
        recyclerActiveTrades.setLayoutManager(new LinearLayoutManager(this));

        updateUI();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_active_trades);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_market) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            if (id == R.id.nav_trade) {
                startActivity(new Intent(this, TradeActivity.class));
                return true;
            }
            if (id == R.id.nav_active_trades) return true;
            if (id == R.id.nav_portfolio) {
                startActivity(new Intent(this, PortfolioActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        plHandler.post(plRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        plHandler.removeCallbacks(plRunnable);
    }

    public void updateUI() {
        JSONArray portfolioArray = dataManager.getPortfolio();
        adapter = new PortfolioAdapter(portfolioArray, this, true);
        recyclerActiveTrades.setAdapter(adapter);
    }
}