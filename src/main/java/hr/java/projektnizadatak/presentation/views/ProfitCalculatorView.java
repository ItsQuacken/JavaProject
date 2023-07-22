package hr.java.projektnizadatak.presentation.views;

import hr.java.projektnizadatak.data.ConnectionProvider;
import hr.java.projektnizadatak.entities.CalculatePosition;
import hr.java.projektnizadatak.entities.CalculateProfit;
import hr.java.projektnizadatak.presentation.Application;
import hr.java.projektnizadatak.presentation.ApplicationScreen;
import hr.java.projektnizadatak.presentation.FXUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfitCalculatorView <T , U> {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @FXML
    private ChoiceBox<T> cryptoChoiceBox;

    @FXML
    private ChoiceBox<T> cryptoChoiceBoxProfit;

    @FXML
    private TextField amountTextField;

    @FXML
    private TextField amountTextFieldProfit;

    @FXML
    private TextField entryPriceTextField;

    @FXML
    private Label priceLabel;

    @FXML
    private Label profitLabel;

    private Connection connection;

    public ProfitCalculatorView() {
        try {
            connection = ConnectionProvider.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        if (connection != null) {
            List<T> cryptoList = getCryptoNamesFromDatabase();
            ObservableList<T> observableCryptoList = FXCollections.observableArrayList(cryptoList);
            cryptoChoiceBox.setItems(observableCryptoList);
            cryptoChoiceBoxProfit.setItems(observableCryptoList);
        } else {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Not connected", "Database not connected");
            String m = "Database not connected";
            logger.error(m);
        }
    }

    @FXML
    private void calculatePosition() {
        T selectedCrypto = cryptoChoiceBox.getValue();
        String amountText = amountTextField.getText();

        if (amountText.isEmpty() || selectedCrypto==null) {
            priceLabel.setText("Please enter valid values.");
            priceLabel.setStyle("-fx-text-fill: black;");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            CalculatePosition<T, U> calculatePosition = new CalculatePosition<>();
            double totalPrice = calculatePosition.calculate(selectedCrypto, amount, this);

            priceLabel.setText(String.format("Your current position is worth: %.2f €", totalPrice));
        } catch (NumberFormatException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Error calculating", "Make sure you entered valid data");
        }
    }

    @FXML
    private void calculateProfit() {
        T selectedCrypto = cryptoChoiceBoxProfit.getValue();
        String amountText = amountTextFieldProfit.getText();
        String entryPriceText = entryPriceTextField.getText();

        if (amountText.isEmpty() || entryPriceText.isEmpty() || selectedCrypto==null) {
            profitLabel.setText("Please enter valid values.");
            profitLabel.setStyle("-fx-text-fill: black;");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            CalculateProfit<T, U> calculateProfit = new CalculateProfit<>();
            double profit = calculateProfit.calculate(selectedCrypto, amount, entryPriceText, this);
            double profitPercentage = (profit / (Double.parseDouble(entryPriceText) * amount)) * 100;

            if (profit >= 0) {
                profitLabel.setText(String.format("Your current profits are: %.2f € (%.2f%%)", profit, profitPercentage));
                profitLabel.setStyle("-fx-text-fill: green;");
            } else {
                profitLabel.setText(String.format("Your current profits are: %.2f € (%.2f%%)", profit, profitPercentage));
                profitLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            profitLabel.setText("Please enter valid numeric values.");
            profitLabel.setStyle("-fx-text-fill: black;");
        }
    }



    private List<T> getCryptoNamesFromDatabase() {
        List<T> cryptoList = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM CRYPTO");
            while (resultSet.next()) {
                T cryptoName = (T) resultSet.getString("name");
                cryptoList.add(cryptoName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cryptoList;
    }

    public double getPriceFromDatabase(T crypto) {
        double price = 0.0;

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT price FROM CRYPTO WHERE name = ?");
            statement.setString(1, crypto.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                price = resultSet.getDouble("price");
            }
        } catch (SQLException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Error pulling data", "Error while pulling data from database");
            String m = "Error while pulling data from database";
            logger.error(m);
        }

        return price;
    }

    @FXML
    private void logout() {
        Application.switchToScreen(ApplicationScreen.Login);
        FXUtil.showAlert(Alert.AlertType.CONFIRMATION, "Successfully logged out", "Thanks for using our application.");
    }

    @FXML
    private void switchToPrice() {
        Application.switchToScreen(ApplicationScreen.PriceData);
    }
}