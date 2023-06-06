package hr.java.projekt.glavna.controllers;

import hr.java.projekt.entities.CryptoCurrency;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PriceFetchController implements Initializable {
    @FXML
    private TableView<CryptoCurrency> tableView;

    @FXML
    private TableView<CryptoCurrency> watchlistTableView;

    @FXML
    private TextField cryptoInput;

    private ObservableList<CryptoCurrency> priceEntries;
    private ObservableList<CryptoCurrency> watchlist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        priceEntries = FXCollections.observableArrayList();
        watchlist = FXCollections.observableArrayList();

        // Initialize the table view with columns
        initializeTableView();
        initializeWatchTableView();

        // Load data and display in the table view
        fetchAndDisplayPrices();
    }

    // Add button event handler
    @FXML
    private void addCryptoToWatchlist() {
        String cryptoName = cryptoInput.getText();
        if (!cryptoName.isEmpty()) {
            addCryptoToWatchlist(cryptoName);
            cryptoInput.clear();
        }
    }

    // Remove button event handler
    @FXML
    private void removeCryptoFromWatchlist() {
        CryptoCurrency selectedCrypto = watchlistTableView.getSelectionModel().getSelectedItem();
        if (selectedCrypto != null) {
            removeCryptoFromWatchlist(selectedCrypto);
        }
    }

    private void fetchAndDisplayPrices() {
        // Fetch and display prices for the logged-in user from the desired data source
        // Replace the dummy data with the actual implementation using the getJSONfromURL method

        // Dummy data for demonstration purposes
        String[] cryptoNames = {"bitcoin", "ethereum", "binancecoin", "ripple", "cardano"};

        for (String cryptoName : cryptoNames) {
            try {
                JSONObject data = getJSONfromURL("https://api.coingecko.com/api/v3/simple/price?ids=" + cryptoName + "&vs_currencies=eur&include_market_cap=true&include_24hr_vol=true&include_24hr_change=true&include_last_updated_at=true&precision=full");
                JSONObject pricePoint = data.getJSONObject(cryptoName);

                float price = (float) pricePoint.getDouble("eur");
                int marketCap = pricePoint.getInt("eur_market_cap");
                int volume = pricePoint.getInt("eur_24h_vol");

                JSONObject namedata = getJSONfromURL("https://api.coingecko.com/api/v3/coins/" + cryptoName + "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false");
                String name = namedata.getString("name");

                // Create a PriceEntry object with the fetched data
                CryptoCurrency priceEntry = new CryptoCurrency(name, price, marketCap, volume);
                Thread.sleep(250);
                priceEntries.add(priceEntry);
            } catch (JSONException | InterruptedException e) {
                Logger.getLogger(PriceFetchController.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        // Set the items for the table view
        tableView.setItems(priceEntries);
    }

    private void initializeTableView() {
        // Create columns for the table view
        TableColumn<CryptoCurrency, String> cryptoColumn = new TableColumn<>("Cryptocurrency");
        cryptoColumn.setCellValueFactory(new PropertyValueFactory<>("cryptoName"));

        TableColumn<CryptoCurrency, Float> priceColumn = new TableColumn<>("Price (EUR)");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<CryptoCurrency, Integer> marketCapColumn = new TableColumn<>("Market Cap (EUR)");
        marketCapColumn.setCellValueFactory(new PropertyValueFactory<>("marketCap"));

        TableColumn<CryptoCurrency, Integer> volumeColumn = new TableColumn<>("Volume (EUR)");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

        // Add the columns to the table view
        tableView.getColumns().addAll(cryptoColumn, priceColumn, marketCapColumn, volumeColumn);
    }

    private void initializeWatchTableView() {
        // Create columns for the table view
        TableColumn<CryptoCurrency, String> cryptoColumnWatch = new TableColumn<>("Cryptocurrency");
        cryptoColumnWatch.setCellValueFactory(new PropertyValueFactory<>("cryptoName"));

        TableColumn<CryptoCurrency, Float> priceColumnWatch = new TableColumn<>("Price (EUR)");
        priceColumnWatch.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<CryptoCurrency, Integer> marketCapColumnWatch = new TableColumn<>("Market Cap (EUR)");
        marketCapColumnWatch.setCellValueFactory(new PropertyValueFactory<>("marketCap"));

        TableColumn<CryptoCurrency, Integer> volumeColumnWatch = new TableColumn<>("Volume (EUR)");
        volumeColumnWatch.setCellValueFactory(new PropertyValueFactory<>("volume"));

        // Add the columns to the table view
        watchlistTableView.getColumns().addAll(cryptoColumnWatch, priceColumnWatch, marketCapColumnWatch, volumeColumnWatch);
    }

    private void addCryptoToWatchlist(String cryptoName) {
        try {
            JSONObject dataWatchlist = getJSONfromURL("https://api.coingecko.com/api/v3/simple/price?ids=" + cryptoName + "&vs_currencies=eur&include_market_cap=true&include_24hr_vol=true&include_24hr_change=true&include_last_updated_at=true&precision=full");
            JSONObject pricePointWatchlist = dataWatchlist.getJSONObject(cryptoName);

            float price = (float) pricePointWatchlist.getDouble("eur");
            int marketCap = pricePointWatchlist.getInt("eur_market_cap");
            int volume = pricePointWatchlist.getInt("eur_24h_vol");

            JSONObject namedataWatchlist = getJSONfromURL("https://api.coingecko.com/api/v3/coins/" + cryptoName + "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false");
            String name = namedataWatchlist.getString("name");

            // Create a PriceEntry object with the fetched data
            CryptoCurrency watchlistEntry = new CryptoCurrency(name, price, marketCap, volume);
            watchlist.add(watchlistEntry);
        } catch (JSONException e) {
            Logger.getLogger(PriceFetchController.class.getName()).log(Level.SEVERE, null, e);
        }
        watchlistTableView.setItems(watchlist);
    }

    private void removeCryptoFromWatchlist(CryptoCurrency selectedCrypto) {
        String cryptoName = selectedCrypto.getCryptoName();

        // Remove the selected crypto from the watchlist
        watchlist.remove(selectedCrypto);

        // Remove the corresponding PriceEntry objects from the priceEntries list
        priceEntries.removeIf(entry -> entry.getCryptoName().equals(cryptoName));

        // Update the watchlistTableView to reflect the changes
        watchlistTableView.refresh();
    }

    public static JSONObject getJSONfromURL(String URL) throws JSONException {
        try {
            URLConnection uc;
            URL url = new URL(URL);
            uc = url.openConnection();
            uc.setConnectTimeout(10000);
            uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            uc.connect();

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(uc.getInputStream(),
                            Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            String jsonText = sb.toString();

            return new JSONObject(jsonText);
        } catch (IOException ex) {
            Logger.getLogger(PriceFetchController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
