package hr.java.projekt.glavna;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController extends Application {
    private static Stage appStage;

    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(StartController.class.getResource("views/login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Vježba");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
