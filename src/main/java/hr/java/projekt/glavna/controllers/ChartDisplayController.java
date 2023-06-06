package hr.java.projekt.glavna.controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;

public class ChartDisplayController {
    private static final String CMC_URL = "https://coinmarketcap.com/currencies/";
    private String loggedInUser;

    @FXML
    private BorderPane root;

    @FXML
    private WebView webView;

    public ChartDisplayController(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @FXML
    private void initialize() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setTitle("Chart Display - Logged in as " + loggedInUser);

        WebEngine webEngine = webView.getEngine();

        // Load the website
        webEngine.load(CMC_URL);

        // Enable WebGL after the site is fully loaded
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                enableWebGL(webEngine);
            }
        });
    }

    private void enableWebGL(WebEngine webEngine) {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("WebGLRenderingContext", null);
        webEngine.executeScript("window.WebGLRenderingContext = document.createElement('canvas').getContext('webgl');");
    }

    @FXML
    private void logoutButtonClicked() {
        Stage stage = (Stage) root.getScene().getWindow();
        switchToLoginScreen(stage);
    }

    @FXML
    private void switchToPricesButtonClicked() {
        Stage stage = (Stage) root.getScene().getWindow();
        switchToPriceFetchApp(stage);
    }

    private void switchToPriceFetchApp(Stage primaryStage) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/price-fetch.fxml"));
            PriceFetchController priceFetchController = new PriceFetchController();
            loader.setController(priceFetchController);

            try {
                Scene scene = new Scene(loader.load());
                primaryStage.setScene(scene);
                primaryStage.setTitle("Prices");
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void switchToLoginScreen(Stage primaryStage) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/login-screen.fxml"));
            LoginScreenController loginScreenController = new LoginScreenController();
            loader.setController(loginScreenController);

            try {
                Scene scene = new Scene(loader.load());
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login");
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
