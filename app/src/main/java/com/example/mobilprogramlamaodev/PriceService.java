package com.example.mobilprogramlamaodev;

import android.os.Handler;
import android.os.Looper;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PriceService {
    private static final String TICKER_URL = "https://api.binance.com/api/v3/ticker/24hr";
    private final OkHttpClient client = new OkHttpClient();
    private static PriceService instance;
    
    private final Map<String, Double> priceCache = new HashMap<>();
    private final Map<String, Double> changeCache = new HashMap<>();
    private final Map<String, Long> lastUpdate = new HashMap<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface PriceCallback {
        void onDataReceived(double price, double change);
    }

    public static PriceService getInstance() {
        if (instance == null) instance = new PriceService();
        return instance;
    }

    public void fetchPrice(String symbol, PriceCallback callback) {
        if (lastUpdate.containsKey(symbol) && (System.currentTimeMillis() - lastUpdate.get(symbol) < 1000)) {
            callback.onDataReceived(priceCache.get(symbol), changeCache.get(symbol));
            return;
        }

        // Binance API sadece kripto destekler; FX ve hisse için simüle fiyat kullan
        if (symbol.startsWith("FX:") || symbol.startsWith("NASDAQ:")) {
            double price = getSimulatedFXPrice(symbol);
            double change = (Math.random() * 2) - 1; // -1% ile +1% arası rastgele değişim
            priceCache.put(symbol, price);
            changeCache.put(symbol, change);
            lastUpdate.put(symbol, System.currentTimeMillis());
            mainHandler.post(() -> callback.onDataReceived(price, change));
            return;
        }

        String cleanSymbol = symbol.contains(":") ? symbol.split(":")[1] : symbol;

        Request request = new Request.Builder()
                .url(TICKER_URL + "?symbol=" + cleanSymbol)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onDataReceived(0.0, 0.0));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        double price = json.getDouble("lastPrice");
                        double change = json.getDouble("priceChangePercent");
                        
                        priceCache.put(symbol, price);
                        changeCache.put(symbol, change);
                        lastUpdate.put(symbol, System.currentTimeMillis());
                        
                        mainHandler.post(() -> callback.onDataReceived(price, change));
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onDataReceived(0.0, 0.0));
                    }
                } else {
                    mainHandler.post(() -> callback.onDataReceived(0.0, 0.0));
                }
            }
        });
    }

    private double getSimulatedFXPrice(String symbol) {
        if (symbol.contains("XAUUSD")) return 2350.0 + (Math.random() * 5);
        if (symbol.contains("USDTRY")) return 32.45 + (Math.random() * 0.1);
        if (symbol.contains("EURTRY")) return 35.10 + (Math.random() * 0.1);
        if (symbol.contains("NVDA"))   return 900.0  + (Math.random() * 5);
        if (symbol.contains("AAPL"))   return 185.0  + (Math.random() * 2);
        if (symbol.contains("TSLA"))   return 175.0  + (Math.random() * 3);
        return 1.0;
    }
}