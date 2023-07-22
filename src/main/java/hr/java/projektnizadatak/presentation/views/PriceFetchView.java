package hr.java.projektnizadatak.presentation.views;

import hr.java.projektnizadatak.data.ConnectionProvider;
import hr.java.projektnizadatak.entities.CryptoCurrency;
import hr.java.projektnizadatak.presentation.Application;
import hr.java.projektnizadatak.presentation.ApplicationScreen;
import hr.java.projektnizadatak.presentation.FXUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PriceFetchView implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @FXML
    private TableView<CryptoCurrency> tableView;

    @FXML
    private TableView<CryptoCurrency> watchlistTableView;

    @FXML
    private TextField cryptoInput;

    private ObservableList<CryptoCurrency> priceEntries;
    private ObservableList<CryptoCurrency> watchlist;

    private Connection databaseConnection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        priceEntries = FXCollections.observableArrayList();
        watchlist = FXCollections.observableArrayList();

        initializeTableView();
        initializeWatchTableView();

        Object lock = new Object();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            try {
                synchronized (lock) {
                    fetchAndDisplayPrices();
                    fetchAndDisplayWatchPrices();
                    lock.notifyAll();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void connectToDatabase() {
        try {
            databaseConnection = ConnectionProvider.getConnection();
        } catch (SQLException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Failed to connect to the database", "Connection failed");
            String m = "Error while trying to connect to the database";
            logger.error(m);
        }
    }

    @FXML
    private void addCryptoToWatchlist() {
        String cryptoName = cryptoInput.getText();
        if (!cryptoName.isEmpty()) {
            addCryptoToWatchlist(cryptoName);
            cryptoInput.clear();
        }
    }


    @FXML
    private void removeCryptoFromWatchlist() {
        CryptoCurrency selectedCrypto = watchlistTableView.getSelectionModel().getSelectedItem();
        if (selectedCrypto != null) {
            removeCryptoFromWatchlist(selectedCrypto);
        }
        try{
            priceEntries.remove(selectedCrypto);
            deleteCryptoDataFromDatabase(selectedCrypto.getCryptoName());
            tableView.getSelectionModel().clearSelection();
        }
        catch(Exception e){
            FXUtil.showAlert(Alert.AlertType.ERROR, "Error while deleting", "Error while deleting selected cryptocurrency");
        }
    }
    @FXML
    private void logout() {
        Application.switchToScreen(ApplicationScreen.Login);
        FXUtil.showAlert(Alert.AlertType.CONFIRMATION, "Succesfully logged out", "Thanks for using our application.");
        String m = "User logged out";
        logger.info(m);
    }
    @FXML
    private void chart() {
        Application.switchToScreen(ApplicationScreen.ChartScreen);
    }
    @FXML
    private void switchToCalcalculator() {
        Application.switchToScreen(ApplicationScreen.ProfitCalculator);
    }

    private void fetchAndDisplayWatchPrices() throws IOException {

        String username = Application.getUserManager().getLoggedInUser().getUsername();
        List<String> cryptoNames = getCryptoNamesFromDatabase(username);
        watchlist.clear();
        for (String cryptoName : cryptoNames) {
            try {
                JSONObject namedata = getJSONfromURL("https://api.coingecko.com/api/v3/coins/" + cryptoName + "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false");
                String name = namedata.getString("name");

                JSONObject data = getJSONfromURL("https://api.coingecko.com/api/v3/simple/price?ids=" + cryptoName + "&vs_currencies=eur&include_market_cap=true&include_24hr_vol=true&precision=2");
                JSONObject pricePoint = data.getJSONObject(cryptoName);

                float price = (float) pricePoint.getDouble("eur");
                long marketCap = pricePoint.getLong("eur_market_cap");
                long volume = pricePoint.getLong("eur_24h_vol");

                CryptoCurrency priceEntry = new CryptoCurrency(name, price, marketCap, volume);
                watchlist.add(priceEntry);
            } catch (JSONException e) {
                FXUtil.showAlert(Alert.AlertType.ERROR, "Rate Limit Reached", "Rate limit reached try again after 1 minute 1");
                String m = "API reached limit and cannot fetch data";
                logger.error(m);
            }
        }

        watchlistTableView.setItems(watchlist);
    }
    private void fetchAndDisplayPrices() throws IOException {
        String[] cryptoNames = {"bitcoin", "ethereum", "binancecoin"};
        priceEntries.clear();
        for (String cryptoName : cryptoNames) {
            try {
                JSONObject namedata = getJSONfromURL("https://api.coingecko.com/api/v3/coins/" + cryptoName + "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false");
                String name = namedata.getString("name");

                JSONObject data = getJSONfromURL("https://api.coingecko.com/api/v3/simple/price?ids=" + cryptoName + "&vs_currencies=eur&include_market_cap=true&include_24hr_vol=true&precision=2");
                JSONObject pricePoint = data.getJSONObject(cryptoName);

                float price = (float) pricePoint.getDouble("eur");
                long marketCap = pricePoint.getLong("eur_market_cap");
                long volume = pricePoint.getLong("eur_24h_vol");

                CryptoCurrency priceEntry = new CryptoCurrency(name, price, marketCap, volume);
                priceEntries.add(priceEntry);

                saveCryptoDataToDatabase(name, price, marketCap, volume);
            } catch (JSONException e) {
                FXUtil.showAlert(Alert.AlertType.ERROR, "Rate Limit Reached", "Rate limit reached try again after 1 minute 2");
                String m = "API reached limit and cannot fetch data";
                logger.error(m);
            }
        }

        tableView.setItems(priceEntries);
    }

    private void initializeTableView() {
        connectToDatabase();
        TableColumn<CryptoCurrency, String> cryptoColumn = new TableColumn<>("Cryptocurrency");
        cryptoColumn.setCellValueFactory(new PropertyValueFactory<>("cryptoName"));

        TableColumn<CryptoCurrency, Float> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<CryptoCurrency, Integer> marketCapColumn = new TableColumn<>("Market Cap");
        marketCapColumn.setCellValueFactory(new PropertyValueFactory<>("marketCap"));

        TableColumn<CryptoCurrency, Integer> volumeColumn = new TableColumn<>("Volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));


        tableView.getColumns().addAll(cryptoColumn, priceColumn, marketCapColumn, volumeColumn);
    }

    private void initializeWatchTableView() {


        TableColumn<CryptoCurrency, String> cryptoColumnWatch = new TableColumn<>("Cryptocurrency");
        cryptoColumnWatch.setCellValueFactory(new PropertyValueFactory<>("cryptoName"));

        TableColumn<CryptoCurrency, Float> priceColumnWatch = new TableColumn<>("Price");
        priceColumnWatch.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<CryptoCurrency, Integer> marketCapColumnWatch = new TableColumn<>("Market Cap");
        marketCapColumnWatch.setCellValueFactory(new PropertyValueFactory<>("marketCap"));

        TableColumn<CryptoCurrency, Integer> volumeColumnWatch = new TableColumn<>("Volume");
        volumeColumnWatch.setCellValueFactory(new PropertyValueFactory<>("volume"));


        watchlistTableView.getColumns().addAll(cryptoColumnWatch, priceColumnWatch, marketCapColumnWatch, volumeColumnWatch);
    }

    private void addCryptoToWatchlist(String cryptoName) {
        try {
            JSONObject namedataWatchlist = getJSONfromURL("https://api.coingecko.com/api/v3/coins/" + cryptoName + "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false");
            String name = namedataWatchlist.getString("name");

            JSONObject dataWatchlist = getJSONfromURL("https://api.coingecko.com/api/v3/simple/price?ids=" + cryptoName + "&vs_currencies=eur&include_market_cap=true&include_24hr_vol=true&precision=2");
            JSONObject pricePointWatchlist = dataWatchlist.getJSONObject(cryptoName);

            float price = (float) pricePointWatchlist.getDouble("eur");
            long marketCap = pricePointWatchlist.getLong("eur_market_cap");
            long volume = pricePointWatchlist.getLong("eur_24h_vol");

            CryptoCurrency watchlistEntry = new CryptoCurrency(name, price, marketCap, volume);

            String username = Application.getUserManager().getLoggedInUser().getUsername();

            watchlist.add(watchlistEntry);
            storeCryptoNameInDatabase(username, cryptoName);
            saveCryptoDataToDatabase(name, price, marketCap, volume);
        } catch (JSONException | IOException e) {

            FXUtil.showAlert(Alert.AlertType.ERROR, "Rate Limit Reached", "Rate limit reached try again after 1 minute 3");
            String m = "API reached limit and cannot fetch data";
            logger.error(m);
        }
        watchlistTableView.setItems(watchlist);
    }

    private void removeCryptoFromWatchlist(CryptoCurrency crypto) {

       try{
           watchlist.remove(crypto);
       }
       catch (Exception e){
           FXUtil.showAlert(Alert.AlertType.ERROR, "Rate Limit Reached", "Rate limit reached try again after 1 minute");
       }
    }
    private void deleteCryptoDataFromDatabase(String cryptoName) {
        String username = Application.getUserManager().getLoggedInUser().getUsername();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM USER_CRYPTO WHERE CRYPTO_NAME = ? AND USERNAME = ?")) {

            statement.setString(1, cryptoName.toLowerCase());
            statement.setString(2, username);
            statement.executeUpdate();

        } catch (SQLException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to execute database query");
            String m = "Error while trying to connect to the database";
            logger.error(m);
        }
    }


    private JSONObject getJSONfromURL(String urlString) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn;
        InputStreamReader in = null;
        try {
            URL url = new URL(urlString);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new IOException("Error while fetching JSON data", e);
        }

        return new JSONObject(sb.toString());
    }
    private void saveCryptoDataToDatabase(String name, float price, long marketCap, long volume) {
        try {
            databaseConnection.setAutoCommit(false);

            String selectQuery = "SELECT COUNT(*) FROM CRYPTO WHERE Name = ?";
            PreparedStatement selectStatement = databaseConnection.prepareStatement(selectQuery);
            selectStatement.setString(1, name);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int rowCount = resultSet.getInt(1);
            selectStatement.close();

            if (rowCount > 0) {
                String updateQuery = "UPDATE CRYPTO SET Price = ?, MarketCap = ?, Volume = ? WHERE Name = ?";
                PreparedStatement updateStatement = databaseConnection.prepareStatement(updateQuery);
                updateStatement.setFloat(1, price);
                updateStatement.setLong(2, marketCap);
                updateStatement.setLong(3, volume);
                updateStatement.setString(4, name);
                int rowsAffected = updateStatement.executeUpdate();
                updateStatement.close();

                if (rowsAffected > 0) {

                    databaseConnection.commit();
                } else {

                    databaseConnection.rollback();
                    FXUtil.showAlert(Alert.AlertType.ERROR, "Failed to save cryptocurrency to database", "Failed to save data");
                    String m = "Failed to save cryptocurrency to database";
                    logger.error(m);
                }
            } else {

                String insertQuery = "INSERT INTO CRYPTO (Name, Price, MarketCap, Volume) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = databaseConnection.prepareStatement(insertQuery);
                insertStatement.setString(1, name);
                insertStatement.setFloat(2, price);
                insertStatement.setLong(3, marketCap);
                insertStatement.setLong(4, volume);
                int rowsAffected = insertStatement.executeUpdate();
                insertStatement.close();

                if (rowsAffected > 0) {

                    databaseConnection.commit();
                } else {

                    databaseConnection.rollback();
                    FXUtil.showAlert(Alert.AlertType.ERROR, "Failed to save cryptocurrency to database", "Failed to save data");
                    String m = "Failed to save cryptocurrency to database";
                    logger.error(m);
                }
            }
        } catch (SQLException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Rate limit reached", "Failed to save cryptocurrency to database. Try again later");
            String m = "API reached limit and cannot store fetched data to database";
            logger.error(m);
        } finally {
            try {

                databaseConnection.setAutoCommit(true);
            } catch (SQLException e) {
                FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to execute database query");
                String m = "Error while trying to connect to the database";
                logger.error(m);
            }
        }
    }
    private void storeCryptoNameInDatabase(String username, String cryptoName) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            connection.setAutoCommit(false);

            String query = "INSERT INTO user_crypto (username, crypto_name) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, cryptoName);
                statement.executeUpdate();
            } catch (SQLException e) {
                connection.rollback();
                FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to execute database query");
                String m = "Error while trying to connect to the database";
                logger.error(m);
            }

            connection.commit();
        } catch (SQLException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to establish a database connection");
            String m = "Error while trying to connect to the database";
            logger.error(m);
        }
    }


    private List<String> getCryptoNamesFromDatabase(String username) {
        List<String> cryptoNames = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection()) {
            String query = "SELECT crypto_name FROM user_crypto WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String cryptoName = resultSet.getString("crypto_name");
                        cryptoNames.add(cryptoName);
                    }
                }
            } catch (SQLException e) {
                FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to execute database query");
                String m = "SQL query failed and cannot save data to database";
                logger.error(m);
            }
        } catch (SQLException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to establish a database connection");
            String m = "Error while trying to connect to the data base";
            logger.error(m);
        }

        return cryptoNames;
    }
}
