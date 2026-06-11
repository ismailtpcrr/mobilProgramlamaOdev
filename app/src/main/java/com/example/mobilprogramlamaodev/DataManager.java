package com.example.mobilprogramlamaodev;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String PREF_NAME = "CryptoTraderPrefs";
    private static final String KEY_BALANCE = "virtual_balance";
    private static final String KEY_PORTFOLIO = "user_portfolio";
    private static final String KEY_HISTORY = "trade_history";
    private static final String KEY_LAST_SYMBOL = "last_symbol";
    private static final String KEY_FAVORITES = "user_favorites";

    private SharedPreferences sharedPreferences;

    public DataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public double getBalance() {
        return Double.longBitsToDouble(sharedPreferences.getLong(KEY_BALANCE, Double.doubleToLongBits(10000.0)));
    }

    public void saveBalance(double balance) {
        sharedPreferences.edit().putLong(KEY_BALANCE, Double.doubleToLongBits(balance)).apply();
    }

    public void saveLastSymbol(String symbol) {
        sharedPreferences.edit().putString(KEY_LAST_SYMBOL, symbol).apply();
    }

    public String getLastSymbol() {
        return sharedPreferences.getString(KEY_LAST_SYMBOL, "BINANCE:BTCUSDT");
    }

    public void buyAsset(String symbol, double amount, double buyPrice, double tp, double sl, String type) {
        try {
            JSONArray portfolio = getPortfolio();
            JSONObject newItem = new JSONObject();
            newItem.put("id", System.currentTimeMillis());
            newItem.put("symbol", symbol);
            newItem.put("amount", amount);
            newItem.put("buyPrice", buyPrice);
            newItem.put("tp", tp);
            newItem.put("sl", sl);
            newItem.put("type", type);
            portfolio.put(newItem);
            sharedPreferences.edit().putString(KEY_PORTFOLIO, portfolio.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public boolean closePositionById(long id, double currentPrice) {
        JSONArray portfolio = getPortfolio();
        JSONArray newList = new JSONArray();
        boolean found = false;

        for (int i = 0; i < portfolio.length(); i++) {
            try {
                JSONObject item = portfolio.getJSONObject(i);
                long itemId = item.optLong("id", -1);
                if (itemId == id) {
                    double amount = item.optDouble("amount", 0);
                    double buyPrice = item.optDouble("buyPrice", 0);
                    String type = item.optString("type", "BUY");
                    String symbol = item.optString("symbol", "");

                    double profit = type.equals("SELL")
                            ? (buyPrice - currentPrice) * amount
                            : (currentPrice - buyPrice) * amount;
                    double percent = buyPrice > 0 ? (profit / (buyPrice * amount)) * 100 : 0;

                    saveBalance(getBalance() + (amount * buyPrice) + profit);
                    addTradeToHistory(symbol, type, buyPrice, currentPrice, amount, profit, percent);
                    found = true;
                } else {
                    newList.put(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (found) {
            sharedPreferences.edit().putString(KEY_PORTFOLIO, newList.toString()).apply();
            return true;
        }
        return false;
    }

    private void addTradeToHistory(String symbol, String type, double entry, double exit, double amount, double profit, double percent) {
        try {
            JSONArray history = getHistory();
            JSONObject entryObj = new JSONObject();
            entryObj.put("symbol", symbol);
            entryObj.put("type", type);
            entryObj.put("entryPrice", entry);
            entryObj.put("exitPrice", exit);
            entryObj.put("amount", amount);
            entryObj.put("profit", profit);
            entryObj.put("percent", percent);
            entryObj.put("timestamp", System.currentTimeMillis());
            
            JSONArray newHistory = new JSONArray();
            newHistory.put(entryObj);
            for (int i = 0; i < history.length(); i++) newHistory.put(history.get(i));
            
            sharedPreferences.edit().putString(KEY_HISTORY, newHistory.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public JSONArray getPortfolio() {
        try {
            JSONArray arr = new JSONArray(sharedPreferences.getString(KEY_PORTFOLIO, "[]"));
            boolean migrated = false;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                if (!item.has("id") || item.optLong("id", 0) == 0) {
                    item.put("id", System.currentTimeMillis() + i);
                    migrated = true;
                }
            }
            if (migrated) {
                sharedPreferences.edit().putString(KEY_PORTFOLIO, arr.toString()).apply();
            }
            return arr;
        } catch (JSONException e) { return new JSONArray(); }
    }

    public JSONArray getHistory() {
        try { return new JSONArray(sharedPreferences.getString(KEY_HISTORY, "[]")); }
        catch (JSONException e) { return new JSONArray(); }
    }

    public void addFavorite(String symbol) {
        try {
            JSONArray favs = new JSONArray(sharedPreferences.getString(KEY_FAVORITES, "[]"));
            for (int i = 0; i < favs.length(); i++) {
                if (favs.getString(i).equals(symbol)) return;
            }
            favs.put(symbol);
            sharedPreferences.edit().putString(KEY_FAVORITES, favs.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void removeFavorite(String symbol) {
        try {
            JSONArray favs = new JSONArray(sharedPreferences.getString(KEY_FAVORITES, "[]"));
            JSONArray newList = new JSONArray();
            for (int i = 0; i < favs.length(); i++) {
                if (!favs.getString(i).equals(symbol)) newList.put(favs.get(i));
            }
            sharedPreferences.edit().putString(KEY_FAVORITES, newList.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public List<String> getFavorites() {
        List<String> list = new ArrayList<>();
        try {
            JSONArray favs = new JSONArray(sharedPreferences.getString(KEY_FAVORITES, "[]"));
            for (int i = 0; i < favs.length(); i++) list.add(favs.getString(i));
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    public boolean isFavorite(String symbol) {
        return getFavorites().contains(symbol);
    }
}
