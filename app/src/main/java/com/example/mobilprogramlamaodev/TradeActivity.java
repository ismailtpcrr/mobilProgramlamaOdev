package com.example.mobilprogramlamaodev;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TradeActivity extends AppCompatActivity {

    private WebView tradingView;
    private TextView txtAssetName, txtCurrentPrice;
    private EditText editAmount, editTP, editSL;
    private Button btnBuy, btnSell;
    private DataManager dataManager;
    private double currentAssetPrice;
    private String assetSymbol;
    private final Handler priceHandler = new Handler();
    private final Runnable priceRunnable = new Runnable() {
        @Override public void run() {
            startPriceUpdates();
            priceHandler.postDelayed(this, 5000); // 5 saniyede bir güncelle
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        dataManager = new DataManager(this);

        tradingView = findViewById(R.id.tradingView);
        txtAssetName = findViewById(R.id.txtAssetName);
        txtCurrentPrice = findViewById(R.id.txtCurrentPrice);
        editAmount = findViewById(R.id.editAmount);
        editTP = findViewById(R.id.editTP);
        editSL = findViewById(R.id.editSL);
        btnBuy = findViewById(R.id.btnBuy);
        btnSell = findViewById(R.id.btnSell);

        // Son açılan varlığı hatırla veya yeni geleni kaydet
        assetSymbol = getIntent().getStringExtra("ASSET_SYMBOL");
        if (assetSymbol == null) {
            assetSymbol = dataManager.getLastSymbol();
        } else {
            dataManager.saveLastSymbol(assetSymbol);
        }
        
        String assetName = getIntent().getStringExtra("ASSET_NAME");
        if (assetName == null) assetName = assetSymbol.contains(":") ? assetSymbol.split(":")[1] : assetSymbol;

        txtAssetName.setText(assetName + " (" + assetSymbol + ")");
        
        setupWebView(assetSymbol);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_trade);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_market) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            if (id == R.id.nav_trade) return true;
            if (id == R.id.nav_active_trades) {
                startActivity(new Intent(this, ActiveTradesActivity.class));
                return true;
            }
            if (id == R.id.nav_portfolio) {
                startActivity(new Intent(this, PortfolioActivity.class));
                return true;
            }
            return false;
        });

        btnBuy.setOnClickListener(v -> executeTrade("BUY"));
        btnSell.setOnClickListener(v -> executeTrade("SELL"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        priceHandler.post(priceRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        priceHandler.removeCallbacks(priceRunnable);
    }

    private void executeTrade(String type) {
        String amountStr = editAmount.getText().toString();
        if (amountStr.isEmpty() || currentAssetPrice <= 0) {
            Toast.makeText(this, "Geçersiz miktar veya fiyat bekleniyor!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        double totalCost = amount * currentAssetPrice;

        if (dataManager.getBalance() >= totalCost) {
            dataManager.saveBalance(dataManager.getBalance() - totalCost);
            double tp = editTP.getText().toString().isEmpty() ? 0 : Double.parseDouble(editTP.getText().toString());
            double sl = editSL.getText().toString().isEmpty() ? 0 : Double.parseDouble(editSL.getText().toString());
            dataManager.buyAsset(assetSymbol, amount, currentAssetPrice, tp, sl, type);
            Toast.makeText(this, type.equals("BUY") ? "Long Pozisyon Açıldı!" : "Short Pozisyon Açıldı!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Yetersiz Bakiye!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPriceUpdates() {
        PriceService.getInstance().fetchPrice(assetSymbol, (price, change) -> {
            runOnUiThread(() -> {
                currentAssetPrice = price;
                String currency = assetSymbol.contains("TRY") ? "₺" : "$";
                txtCurrentPrice.setText(currency + String.format("%.2f", currentAssetPrice));
            });
        });
    }

    private void setupWebView(String symbol) {
        WebSettings webSettings = tradingView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        
        tradingView.setWebViewClient(new WebViewClient());
        
        String html = "<!DOCTYPE html><html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head><body style=\"margin:0;padding:0;background-color:#121212;\">" +
                "<div class=\"tradingview-widget-container\" style=\"height:100vh;width:100vw;\">" +
                "  <div id=\"tradingview_widget\" style=\"height:100%;width:100%;\"></div>" +
                "  <script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>" +
                "  <script type=\"text/javascript\">" +
                "  new TradingView.widget({" +
                "    \"autosize\": true," +
                "    \"symbol\": \"" + symbol + "\"," +
                "    \"interval\": \"D\"," +
                "    \"timezone\": \"Etc/UTC\"," +
                "    \"theme\": \"dark\"," +
                "    \"style\": \"1\"," +
                "    \"locale\": \"tr\"," +
                "    \"toolbar_bg\": \"#121212\"," +
                "    \"enable_publishing\": false," +
                "    \"hide_top_toolbar\": true," +
                "    \"save_image\": false," +
                "    \"container_id\": \"tradingview_widget\"" +
                "  });" +
                "  </script>" +
                "</div>" +
                "</body></html>";
        
        tradingView.loadDataWithBaseURL("https://www.tradingview.com", html, "text/html", "UTF-8", null);
    }
}