package hr.java.projekt.glavna.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginScreenController implements Initializable {
    private static final String USER_FILE = "data/users.txt";

    @FXML
    private Text sceneTitle;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    }


    @FXML
    private void resetPassword(ActionEvent event) {
        String username = usernameTextField.getText();
        resetPassword(username);
    }

    @FXML
    private void login(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password)) {
            loginStatus.setText("Login successful");
            loginStatus.setTextFill(Color.GREEN);

            // Launch the PriceFetchApp from the JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PriceFetch.fxml"));
                    Stage stage = (Stage) usernameTextField.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));
                    stage.setTitle("Price Fetch");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            loginStatus.setText("Login failed");
            loginStatus.setTextFill(Color.RED);
        }
    }

    @FXML
    private void createUser(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (!username.isEmpty() && !password.isEmpty()) {
            createUser(username, password);
            loginStatus.setText("User created successfully");
            loginStatus.setTextFill(Color.GREEN);
        } else {
            loginStatus.setText("Please enter a username and password");
            loginStatus.setTextFill(Color.RED);
        }
    }

    private boolean validateLogin(String username, String password) {
        try {
            String hashedPassword = hashPassword(password);

            // Read user credentials from the file
            List<String> lines = Files.readAllLines(Paths.get(USER_FILE));

            // Check if the given username and hashed password match any stored user
            for (String line : lines) {
                String[] userData = line.split(":");
                String storedUsername = userData[0];
                String storedPassword = userData[1];

                if (username.equals(storedUsername) && hashedPassword.equals(storedPassword)) {
                    return true;
                }
            }

            // If the username exists but the password doesn't match
            if (usernameExists(username)) {
                loginStatus.setText("Invalid password");
                loginStatus.setTextFill(Color.RED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void createUser(String username, String password) {
        try {
            // Check if the username already exists
            if (usernameExists(username)) {
                loginStatus.setText("Username already exists");
                loginStatus.setTextFill(Color.RED);
                return;
            }

            String hashedPassword = hashPassword(password);
            String userData = username + ":" + hashedPassword;

            // Append the new user to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true));
            writer.write(userData);
            writer.newLine();
            writer.close();

            loginStatus.setText("User created successfully");
            loginStatus.setTextFill(Color.GREEN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetPassword(String username) {
        try {
            // Read user credentials from the file
            List<String> lines = Files.readAllLines(Paths.get(USER_FILE));

            // Find the line with the user's username
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] userData = line.split(":");
                String storedUsername = userData[0];

                if (username.equals(storedUsername)) {
                    // Request a new password from the user
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Reset Password");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Enter a new password:");

                    Optional<String> newPassword = dialog.showAndWait();

                    if (newPassword.isPresent()) {
                        String hashedPassword = hashPassword(newPassword.get());

                        // Update the line with the new hashed password
                        String updatedLine = storedUsername + ":" + hashedPassword;
                        lines.set(i, updatedLine);

                        // Write the updated lines back to the file
                        Files.write(Paths.get(USER_FILE), lines);

                        // Here, you can implement the logic to send the new password to the user's email address
                        // For demonstration purposes, let's just display a message in the application
                        loginStatus.setText("Password reset successful");
                        loginStatus.setTextFill(Color.GREEN);
                    } else {
                        // If the user cancels the password reset dialog
                        loginStatus.setText("Password reset canceled");
                        loginStatus.setTextFill(Color.RED);
                    }

                    return;
                }
            }

            // If the username does not exist
            loginStatus.setText("Username not found");
            loginStatus.setTextFill(Color.RED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean usernameExists(String username) {
        try {
            // Read user credentials from the file
            List<String> lines = Files.readAllLines(Paths.get(USER_FILE));

            // Check if the given username exists in the file
            for (String line : lines) {
                String[] userData = line.split(":");
                String storedUsername = userData[0];

                if (username.equals(storedUsername)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}