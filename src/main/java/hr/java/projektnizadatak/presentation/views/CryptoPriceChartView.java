package hr.java.projektnizadatak.presentation.views;

import hr.java.projektnizadatak.presentation.Application;
import hr.java.projektnizadatak.presentation.ApplicationScreen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

public class CryptoPriceChartView implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final String API_ENDPOINT = "https://api.coingecko.com/api/v3/coins";
    private static final String CURRENCY = "eur";
    private static final int CHART_DAYS = 30;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    @FXML
    private TextField cryptoNameInput;

    @FXML
    private Label lowestPriceLabel;

    @FXML
    private Label highestPriceLabel;

    @FXML
    private Label cryptoNameLabel;

    @FXML
    private Label currentPriceLabel;

    private ObservableList<String> cryptoNames;

    @FXML
    private void logout() {
        Application.switchToScreen(ApplicationScreen.Login);
    }

    @FXML
    private void switchToPrice() {
        Application.switchToScreen(ApplicationScreen.PriceData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCryptoNames();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
    }

    private void initializeCryptoNames() {
        cryptoNames = FXCollections.observableArrayList("Bitcoin", "Ethereum", "Binance Coin");
    }

    @FXML
    private void createChart() {
        String cryptoName = cryptoNameInput.getText().trim();
        if (!cryptoName.isEmpty()) {
            updateChart(cryptoName);
        }
    }

    private void updateChart(String cryptoName) {
        lineChart.getData().clear();

        String apiUrl = API_ENDPOINT + "/" + cryptoName.toLowerCase(Locale.ENGLISH) + "/market_chart?vs_currency=" + CURRENCY + "&days=" + CHART_DAYS;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(cryptoName + " price");

        try {
            JSONArray prices = getPricesFromAPI(apiUrl);

            double lowestPrice = Float.MAX_VALUE;
            double highestPrice = Float.MIN_VALUE;
            double currentPrice = 0.0;
            for (int i = 0; i < prices.length(); i++) {
                JSONArray pricePoint = prices.getJSONArray(i);
                double price = pricePoint.getDouble(1);
                if (price < lowestPrice) {
                    lowestPrice = price;
                }
                if (price > highestPrice) {
                    highestPrice = price;
                }
                if (i == prices.length() - 1) {
                    currentPrice = price;
                }
            }


            lowestPriceLabel.setText(String.format("Lowest Price: %.2f €", lowestPrice));
            highestPriceLabel.setText(String.format("Highest Price: %.2f €", highestPrice));
            currentPriceLabel.setText(String.format("Current Price: %.2f €", currentPrice));
            cryptoNameLabel.setText(String.format("Crypto Name: %s", capitalizeFirstLetter(cryptoName)));


            for (int i = 0; i < prices.length(); i++) {
                JSONArray pricePoint = prices.getJSONArray(i);
                long timestamp = pricePoint.getLong(0);
                double price = pricePoint.getDouble(1);
                LocalDateTime dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();

                series.getData().add(new XYChart.Data<>(String.valueOf(i), price));
            }


            double lowerBound = Math.floor(lowestPrice / 2.0);
            double upperBound = Math.ceil(series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).max().orElse(0) * 1.2);

            double tickUnit = Math.ceil((upperBound - lowerBound) / 10.0);

            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);

        } catch (IOException | JSONException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to fetch cryptocurrency data. Please try again later.");
            alert.showAndWait();
            String m = "Error while trying to display data onto chart: " + e.getMessage();
            logger.error(m);
        }

        lineChart.setCreateSymbols(false);

        lineChart.getData().add(series);
    }
    private String capitalizeFirstLetter(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private JSONArray getPricesFromAPI(String apiUrl) throws IOException, JSONException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            String jsonResponse = convertStreamToString(inputStream);
            JSONObject json = new JSONObject(jsonResponse);
            return json.getJSONArray("prices");
        }

        String m = "Error while trying to connect to the database: ";
        logger.error(m);

        return new JSONArray();
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, bytesRead));
        }
        return stringBuilder.toString();
    }
}
