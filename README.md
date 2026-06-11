# 📈 Sanal Borsa Uygulaması

Mobil Programlama dersi kapsamında geliştirilmiş, gerçek zamanlı piyasa verilerini kullanan bir **sanal (paper) trading** Android uygulamasıdır. Kullanıcılar gerçek para riske atmadan kripto para, döviz, altın ve hisse senedi alım-satım işlemi yapabilir.

---

## 🚀 Özellikler

- **Gerçek Zamanlı Fiyatlar** — Binance API üzerinden canlı kripto fiyatları; altın, döviz ve hisse senetleri için simüle fiyatlar
- **TradingView Grafik** — Her varlık için interaktif TradingView grafiği
- **Long / Short Pozisyon** — AL (Long) ve SAT (Short) işlemleri
- **TP / SL Desteği** — Kâr Al ve Zarar Durdur seviyesi belirleme
- **Aktif İşlem Takibi** — Açık pozisyonlarda anlık kâr/zarar hesabı
- **İşlem Geçmişi** — Kapatılan pozisyonların detaylı geçmişi
- **Favori Sistemi** — Varlıkları favorileme, favoriler listenin üstünde gösterilir
- **Sanal Bakiye** — Başlangıçta $10.000 sanal bakiye, dilediğin kadar ekleyebilirsin
- **Arama** — Piyasalar ekranında isim veya sembol ile anlık arama

---

## 📱 Ekranlar

| Ekran | Açıklama |
|-------|----------|
| **Piyasalar** | Tüm varlıkların listesi, arama ve favori butonu |
| **Grafik / İşlem** | TradingView grafiği, AL/SAT butonları, TP/SL girişi |
| **Aktif İşlemler** | Açık pozisyonlar, anlık P&L, pozisyon kapatma |
| **Cüzdan** | Bakiye, bakiye ekleme, geçmiş işlemler |

---

## 🛠️ Teknik Detaylar

### Platform & Dil
- **Platform:** Android (min SDK 24, target SDK 35)
- **Dil:** Java

### Mimari & Kütüphaneler
- `OkHttp` — Binance REST API HTTP istekleri
- `WebView` — TradingView grafik widget entegrasyonu
- `SharedPreferences` — Yerel veri saklama (bakiye, portföy, geçmiş, favoriler)
- `RecyclerView` — Liste görünümleri
- `Material Design` — BottomNavigationView, CardView, AlertDialog

### API Entegrasyonları
- **Binance REST API** (`https://api.binance.com/api/v3/ticker/24hr`) — Kripto para fiyat ve 24s değişim verisi
- **TradingView Widget** — Gerçek zamanlı interaktif grafik (WebView üzerinden)

### Veri Yönetimi (SharedPreferences)
| Anahtar | İçerik |
|---------|--------|
| `virtual_balance` | Kullanıcının sanal bakiyesi |
| `user_portfolio` | Açık pozisyonlar (JSON Array) |
| `trade_history` | Kapatılan işlemlerin geçmişi (JSON Array) |
| `user_favorites` | Favori varlık sembolleri (JSON Array) |

### Desteklenen Varlıklar
**Kripto:** Bitcoin, Ethereum, BNB, Solana, XRP, Cardano, Avax, Dogecoin  
**Döviz / Emtia:** Gram Altın (XAU/USD), Dolar/TL, Euro/TL  
**Hisse:** Nvidia, Apple, Tesla

---

## 📁 Proje Yapısı

```
app/src/main/java/com/example/mobilprogramlamaodev/
│
├── MainActivity.java          # Piyasalar ekranı, varlık listesi, arama
├── TradeActivity.java         # Grafik ve işlem ekranı
├── ActiveTradesActivity.java  # Aktif pozisyonlar ekranı
├── PortfolioActivity.java     # Cüzdan ve geçmiş işlemler ekranı
│
├── Asset.java                 # Varlık veri modeli
├── DataManager.java           # SharedPreferences ile veri yönetimi
├── PriceService.java          # Binance API ile fiyat çekme servisi
│
├── MarketAdapter.java         # Piyasalar RecyclerView adaptörü
├── PortfolioAdapter.java      # Aktif işlemler RecyclerView adaptörü
└── HistoryAdapter.java        # Geçmiş işlemler RecyclerView adaptörü
```

---

## ⚙️ Kurulum

1. Projeyi klonlayın:
   ```bash
   git clone <repo-url>
   ```
2. Android Studio'da açın
3. `local.properties` dosyasında Android SDK yolunun doğru olduğunu kontrol edin
4. Emülatör veya fiziksel cihazda çalıştırın (`Run > Run 'app'`)

> **Not:** Uygulama internet erişimi gerektirir (`INTERNET` izni `AndroidManifest.xml`'de tanımlıdır).

---

## 📋 Proje Gereksinimleri Karşılama

| Gereksinim | Durum | Açıklama |
|-----------|-------|----------|
| En az 3 Aktivite | ✅ | 4 aktivite mevcut |
| Yerel Veri Tabanı | ✅ | SharedPreferences ile tam veri yönetimi |
| API Entegrasyonu | ✅ | Binance REST API + TradingView Widget |
| Modern UX/UI | ✅ | Dark theme, Material Design bileşenleri |
| Kod Okunabilirliği | ✅ | Her sınıf ve metod açıklamalarla belgelenmiş |

---

## 👨‍💻 Geliştirici

**İsmail Topuçar**  
Mobil Programlama Dersi — Dönem Sonu Projesi  
Teslim Tarihi: 12.06.2026
