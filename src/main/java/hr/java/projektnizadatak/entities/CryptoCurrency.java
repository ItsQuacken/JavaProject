package hr.java.projektnizadatak.entities;

public class CryptoCurrency {
    private String cryptoName;
    private float price;
    private long marketCap;
    private long volume;

    public CryptoCurrency(String cryptoName, float price, long marketCap, long volume) {
        this.cryptoName = cryptoName;
        this.price = price;
        this.marketCap = marketCap;
        this.volume = volume;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public float getPrice() {
        return price;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public long getVolume() {
        return volume;
    }
}
