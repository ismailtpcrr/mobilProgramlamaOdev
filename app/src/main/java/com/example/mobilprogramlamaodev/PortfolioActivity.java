package com.example.mobilprogramlamaodev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PortfolioActivity extends AppCompatActivity {

    private TextView txtTotalBalance;
    private Button btnAddBalance;
    private RecyclerView recyclerHistory;
    private DataManager dataManager;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        dataManager = new DataManager(this);
        txtTotalBalance = findViewById(R.id.txtTotalBalance);
        btnAddBalance = findViewById(R.id.btnAddBalance);
        recyclerHistory = findViewById(R.id.recyclerAssets);

        updateUI();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_portfolio);
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
            if (id == R.id.nav_active_trades) {
                startActivity(new Intent(this, ActiveTradesActivity.class));
                return true;
            }
            if (id == R.id.nav_portfolio) return true;
            return false;
        });

        btnAddBalance.setOnClickListener(v -> showAddBalanceDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        txtTotalBalance.setText("$" + String.format("%.2f", dataManager.getBalance()));
        JSONArray historyArray = dataManager.getHistory();
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(historyArray);
        recyclerHistory.setAdapter(adapter);
    }

    private void showAddBalanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sanal Bakiye Ekle");
        final EditText input = new EditText(this);
        input.setHint("Miktar (Örn: 5000)");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("EKLE", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                dataManager.saveBalance(dataManager.getBalance() + Double.parseDouble(amountStr));
                updateUI();
                Toast.makeText(this, "Bakiye eklendi!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İPTAL", null);
        builder.show();
    }
}