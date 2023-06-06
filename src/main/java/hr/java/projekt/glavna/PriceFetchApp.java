/*package hr.java.projekt.glavna;

import hr.java.projekt.entities.CryptoCurrency;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PriceFetchApp extends Application {
    private String loggedInUser;
    private TableView<CryptoCurrency> tableView;
    private TableView<CryptoCurrency> watchlistTableView;
    private ObservableList<CryptoCurrency> priceEntries;
    private ObservableList<CryptoCurrency> watchlist;

    public PriceFetchApp(String loggedInUser) {
        this.loggedInUser = loggedInUser;
        priceEntries = FXCollections.observableArrayList();
        watchlist = FXCollections.observableArrayList();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Price Fetch - Logged in as " + loggedInUser);

        // Create the table view
        tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No data available"));

        // Initialize the table view with columns
        initializeTableView();

        // Create the watchlist table view
        watchlistTableView = new TableView<>();
        watchlistTableView.setPlaceholder(new Label("Watchlist is empty"));
        // Initialize the watchtable view with columns
        initializeWatchTableView();

        // Create the logout button
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(e -> {
            // Go back to the login screen
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.start(primaryStage);
        });

        // Create the chart button
        Button chartButton = new Button("Chart");
        chartButton.setOnAction(e -> {
            // Switch to the chart view
            ChartDisplayApp chartDisplayApp = new ChartDisplayApp(loggedInUser);
            chartDisplayApp.start(primaryStage);
        });

        // Create the add button
        Button addButton = new Button("Add");
        TextField cryptoInput = new TextField();
        HBox addBox = new HBox(cryptoInput, addButton);

        // Add button event handler
        addButton.setOnAction(e -> {
            String cryptoName = cryptoInput.getText();
            if (!cryptoName.isEmpty()) {
                addCryptoToWatchlist(cryptoName);
                cryptoInput.clear();
            }
        });
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> {
            CryptoCurrency selectedCrypto = watchlistTableView.getSelectionModel().getSelectedItem();
            if (selectedCrypto != null) {
                removeCryptoFromWatchlist(selectedCrypto);
            }
        });

        // Create a HBox to hold the remove button
        HBox removeBox = new HBox(removeButton);

        // Create a VBox to hold the watchlist components
        VBox watchlistBox = new VBox(10, watchlistTableView, addBox, removeBox);

        // Create labels for the table views
        Label topCryptosLabel = new Label("Top 5 Cryptocurrencies");
        Label watchlistLabel = new Label("My Watchlist");

        // Create a GridPane to hold the table views, labels, and input box
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.add(topCryptosLabel, 0, 0);
        gridPane.add(tableView, 0, 1);
        gridPane.add(watchlistLabel, 1, 0);
        gridPane.add(watchlistBox, 1, 1);

        // Load data and display in the table view
        fetchAndDisplayPrices();

        // Adjust column widths
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        watchlistTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set the preferred width of the table views
        tableView.setPrefWidth(500); // Adjust this value as needed
        watchlistTableView.setPrefWidth(500); // Adjust this value as needed

        // Create a VBox to hold the main content
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(gridPane, logoutButton, chartButton);

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchAndDisplayPrices() {
        // Fetch and display prices for the logged-in user from the desired data source
        // Replace the dummy data with the actual implementation using the getJSONfromURL method

        // Dummy data for demonstration purposes
        String[] cryptoNames = {"bitcoin", "ethereum", "binancecoin", "ripple", "cardano"};

        for (String cryptoName : cryptoNames) {
            try {
                JSONObject data = getJSONfromURL("https://api.coingecko.com/api/v3/simple/price?ids="+ cryptoName +"&vs_currencies=eur&include_market_cap=true&include_24hr_vol=true&include_24hr_change=true&include_last_updated_at=true&precision=full");
                JSONObject price_point = data.getJSONObject(cryptoName);

                float price = (float) price_point.getDouble("eur");
                int marketCap = price_point.getInt("eur_market_cap");
                int volume = price_point.getInt("eur_24h_vol");

                JSONObject namedata = getJSONfromURL("https://api.coingecko.com/api/v3/coins/" + cryptoName + "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false");
                String name = namedata.getString("name");

                // Create a PriceEntry object with the fetched data
                CryptoCurrency priceEntry = new CryptoCurrency(name, price, marketCap, volume);
                Thread.sleep(250);
                priceEntries.add(priceEntry);
            } catch (JSONException | InterruptedException e) {
                Logger.getLogger(PriceFetchApp.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(PriceFetchApp.class.getName()).log(Level.SEVERE, null, e);
        }
        watchlistTableView.setItems(watchlist);
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
            Logger.getLogger(PriceFetchApp.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}*/