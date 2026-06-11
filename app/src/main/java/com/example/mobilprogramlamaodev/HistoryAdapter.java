package com.example.mobilprogramlamaodev;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private JSONArray historyData;

    public HistoryAdapter(JSONArray historyData) {
        this.historyData = historyData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_portfolio layout'unu kullanarak daha detaylı gösteriyoruz
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject item = historyData.getJSONObject(position);
            String symbol = item.getString("symbol");
            String type = item.optString("type", "BUY");
            double entry = item.getDouble("entryPrice");
            double exit = item.getDouble("exitPrice");
            double profit = item.getDouble("profit");
            double percent = item.getDouble("percent");
            long ts = item.optLong("timestamp", 0);

            String typeLabel = type.equals("SELL") ? "SHORT" : "LONG";
            holder.txtSymbol.setText(symbol + " (" + typeLabel + ")");
            holder.txtBuyPrice.setText("Giriş: " + String.format("%.2f", entry));
            holder.txtTarget.setText("Çıkış: " + String.format("%.2f", exit));
            
            String sign = profit >= 0 ? "+" : "";
            holder.txtPL.setText(String.format("%s%.2f (%+.2f%%)", sign, profit, percent));
            holder.txtPL.setTextColor(profit >= 0 ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

            if (ts > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                holder.txtCurrentValue.setText(sdf.format(new Date(ts)));
            }
            
            // Geçmişte "Kapat" butonu olmaz
            holder.btnPortClose.setVisibility(View.GONE);

        } catch (JSONException e) { e.printStackTrace(); }
    }

    @Override
    public int getItemCount() { return historyData.length(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSymbol, txtAmount, txtBuyPrice, txtTarget, txtPL, txtCurrentValue;
        View btnPortClose;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSymbol = itemView.findViewById(R.id.txtPortSymbol);
            txtAmount = itemView.findViewById(R.id.txtPortAmount);
            txtBuyPrice = itemView.findViewById(R.id.txtPortBuyPrice);
            txtTarget = itemView.findViewById(R.id.txtPortTarget);
            txtPL = itemView.findViewById(R.id.txtPortPL);
            txtCurrentValue = itemView.findViewById(R.id.txtPortCurrentValue);
            btnPortClose = itemView.findViewById(R.id.btnPortClose);
        }
    }
}
