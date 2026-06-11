package com.example.mobilprogramlamaodev;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    private JSONArray portfolioData;
    private Context context;
    private boolean isActiveTrades;

    public PortfolioAdapter(JSONArray portfolioData, Context context, boolean isActiveTrades) {
        this.portfolioData = portfolioData;
        this.context = context;
        this.isActiveTrades = isActiveTrades;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Buton görünürlüğünü EN BAŞTA ayarla — aşağıdaki okumalar exception atsa bile buton doğru kalır
        if (isActiveTrades) {
            holder.btnPortClose.setVisibility(View.VISIBLE);
            holder.btnPortClose.setEnabled(true);
            holder.btnPortClose.setText("POZİSYONU KAPAT");
        } else {
            holder.btnPortClose.setVisibility(View.GONE);
        }

        try {
            JSONObject item = portfolioData.getJSONObject(position);
            // opt* metodları exception atmaz; eksik/hatalı field için varsayılan döner
            final long id = item.optLong("id", position);
            final String symbol = item.optString("symbol", "");
            final double buyPrice = item.optDouble("buyPrice", 0);
            final double amount = item.optDouble("amount", 0);
            final String type = item.optString("type", "BUY");
            final String currency = symbol.contains("TRY") ? "₺" : "$";

            holder.txtSymbol.setText(symbol + (type.equals("SELL") ? " (SHORT)" : " (LONG)"));
            holder.txtAmount.setText("Miktar: " + String.format("%.4f", amount));
            holder.txtBuyPrice.setText("Giriş: " + currency + String.format("%.2f", buyPrice));

            double tp = item.optDouble("tp", 0);
            double sl = item.optDouble("sl", 0);
            String tpStr = tp > 0 ? String.format("%.2f", tp) : "-";
            String slStr = sl > 0 ? String.format("%.2f", sl) : "-";
            holder.txtTarget.setText("TP: " + currency + tpStr + " / SL: " + currency + slStr);

            if (isActiveTrades) {
                PriceService.getInstance().fetchPrice(symbol, (currentPrice, change) -> {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).runOnUiThread(() -> {
                            try {
                                double profit = type.equals("SELL") ? (buyPrice - currentPrice) * amount : (currentPrice - buyPrice) * amount;
                                double plPercent = buyPrice > 0 ? (profit / (buyPrice * amount)) * 100 : 0;
                                double currentValue = (buyPrice * amount) + profit;
                                holder.txtCurrentValue.setText("Değer: " + currency + String.format("%.2f", currentValue));
                                String sign = profit >= 0 ? "+" : "";
                                holder.txtPL.setText(String.format("%s%s%.2f (%+.2f%%)", sign, currency, profit, plPercent));
                                holder.txtPL.setTextColor(profit >= 0 ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));
                            } catch (Exception e) { e.printStackTrace(); }
                        });
                    }
                });

                holder.btnPortClose.setOnClickListener(v -> {
                    double positionValue = buyPrice * amount;
                    if (positionValue >= 1000) {
                        new AlertDialog.Builder(context)
                                .setTitle("Pozisyonu Kapat?")
                                .setMessage(String.format("Değeri %.2f %s olan bu pozisyonu kapatmak istediğinizden emin misiniz?", positionValue, currency))
                                .setPositiveButton("EVET, KAPAT", (dialog, which) -> executeCloseProcess(holder, symbol, id, buyPrice))
                                .setNegativeButton("İPTAL", null)
                                .show();
                    } else {
                        executeCloseProcess(holder, symbol, id, buyPrice);
                    }
                });
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, TradeActivity.class);
                intent.putExtra("ASSET_SYMBOL", symbol);
                context.startActivity(intent);
            });

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void executeCloseProcess(ViewHolder holder, String symbol, long id, double buyPrice) {
        holder.btnPortClose.setEnabled(false);
        holder.btnPortClose.setText("Kapatılıyor...");

        PriceService.getInstance().fetchPrice(symbol, (lastPrice, change) -> {
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).runOnUiThread(() -> {
                    // Fiyat gelmezse (0) giriş fiyatından zararsız kapat
                    double closePrice = lastPrice > 0 ? lastPrice : buyPrice;
                    DataManager dm = new DataManager(context);
                    boolean success = dm.closePositionById(id, closePrice);
                    if (success) {
                        Toast.makeText(context, "Pozisyon Başarıyla Kapatıldı!", Toast.LENGTH_SHORT).show();
                        if (context instanceof ActiveTradesActivity) {
                            ((ActiveTradesActivity) context).updateUI();
                        }
                    } else {
                        holder.btnPortClose.setEnabled(true);
                        holder.btnPortClose.setText("POZİSYONU KAPAT");
                        Toast.makeText(context, "Hata: Pozisyon kapatılamadı!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() { return portfolioData.length(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSymbol, txtAmount, txtBuyPrice, txtTarget, txtPL, txtCurrentValue;
        Button btnPortClose;
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
