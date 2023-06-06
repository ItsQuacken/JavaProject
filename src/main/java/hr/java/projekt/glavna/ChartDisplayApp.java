/*package hr.java.projekt.glavna;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class ChartDisplayApp extends Application {

    private static final String CMC_URL = "https://coinmarketcap.com/currencies/";
    private String loggedInUser;

    public ChartDisplayApp(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chart Display - Logged in as " + loggedInUser);

        // Create a WebView to display the website
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Load the website
        webEngine.load(CMC_URL);

        // Enable WebGL after the site is fully loaded
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                enableWebGL(webEngine);
            }
        });

        // Create the logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            // Perform logout actions or switch to the login screen
            switchToLoginScreen(primaryStage);
        });

        // Create the "Switch to Prices" button
        Button switchToPricesButton = new Button("Switch to Prices");
        switchToPricesButton.setOnAction(e -> {
            // Switch to the PriceFetchApp
            switchToPriceFetchApp(primaryStage);
        });

        // Create an HBox to hold the feature buttons
        HBox buttonBox = new HBox(logoutButton, switchToPricesButton);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10));

        // Create the layout using a BorderPane
        BorderPane root = new BorderPane();
        root.setBottom(buttonBox);
        root.setCenter(webView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void enableWebGL(WebEngine webEngine) {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("WebGLRenderingContext", null);
        webEngine.executeScript("window.WebGLRenderingContext = document.createElement('canvas').getContext('webgl');");
    }

    private void switchToPriceFetchApp(Stage primaryStage) {
        Platform.runLater(() -> {
            PriceFetchApp priceFetchApp = new PriceFetchApp(loggedInUser);
            try {
                priceFetchApp.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void switchToLoginScreen(Stage primaryStage) {
        Platform.runLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            try {
                loginScreen.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
*/