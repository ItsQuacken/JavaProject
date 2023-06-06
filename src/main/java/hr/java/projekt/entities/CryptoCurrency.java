package hr.java.projekt.entities;

public class CryptoCurrency {
    private String cryptoName;
    private float price;
    private int marketCap;
    private int volume;

    public CryptoCurrency(String cryptoName, float price, int marketCap, int volume) {
        this.cryptoName = cryptoName;
        this.price = price;
        this.marketCap = marketCap;
        this.volume = volume;
    }

    // Getter methods for the properties
    public String getCryptoName() {
        return cryptoName;
    }

    public float getPrice() {
        return price;
    }

    public int getMarketCap() {
        return marketCap;
    }

    public int getVolume() {
        return volume;
    }
}
