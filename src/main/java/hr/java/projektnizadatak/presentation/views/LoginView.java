package hr.java.projektnizadatak.presentation.views;

import hr.java.projektnizadatak.data.ConnectionProvider;
import hr.java.projektnizadatak.entities.User;
import hr.java.projektnizadatak.entities.UserManager;
import hr.java.projektnizadatak.entities.UserRole;
import hr.java.projektnizadatak.presentation.Application;
import hr.java.projektnizadatak.presentation.ApplicationScreen;
import hr.java.projektnizadatak.presentation.FXUtil;
import hr.java.projektnizadatak.shared.exceptions.InvalidUsernameException;
import hr.java.projektnizadatak.shared.exceptions.UsernameTakenException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoginView {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	@FXML
	private TextField usernameInput;

	@FXML
	private PasswordField passwordInput;

	@FXML
	private void login() {
		UserManager userManager = Application.getUserManager();
		String username = usernameInput.getText();
		String password = passwordInput.getText();

		if (userManager.tryLoginUser(username, password)) {
			User loggedInUser = userManager.getLoggedInUser();
			UserRole userRole = loggedInUser.getUserRole();

			if (userRole == UserRole.ADMIN) {
				Application.switchToScreen(ApplicationScreen.AdminPanel);
			} else {
				Application.switchToScreen(ApplicationScreen.PriceData);
			}
		} else {
			FXUtil.showAlert(Alert.AlertType.ERROR, "Couldn't log in", "Username or Password entered doesn't match our records.");
			String m = "User failed login";
			logger.error(m);
		}
	}

	@FXML
	private void createUser() {
		String username = usernameInput.getText();
		String password = passwordInput.getText();

		if (username.isEmpty() || password.isEmpty()) {
			FXUtil.showAlert(Alert.AlertType.ERROR, "Missing information", "Please enter both username and password.");
			return;
		}

		try {
			var user = Application.getUserManager().createUser(username, password);

			String content = String.format("User %s successfully created!", user.getUsername());
			FXUtil.showAlert(Alert.AlertType.INFORMATION, "User created", content);

			storeUserInDatabase(user);
		} catch (InvalidUsernameException e) {
			FXUtil.showAlert(Alert.AlertType.ERROR, "Invalid username", "Given username length is invalid or it contains invalid characters: " + username);
			String m = "User tried to create an account using invalid characters";
			logger.error(m);
		} catch (UsernameTakenException e) {
			FXUtil.showAlert(Alert.AlertType.ERROR, "Username taken", "This username is already taken: " + username);
			String m = "User tried to create an account using an already existing username";
			logger.error(m);
		}
	}


	private void storeUserInDatabase(User user) {
		try (Connection connection = ConnectionProvider.getConnection()) {
			connection.setAutoCommit(false);

			String query = "INSERT INTO PERSONS (username) VALUES (?)";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, user.getUsername());
				statement.executeUpdate();
			} catch (SQLException e) {
				connection.rollback();
				FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to store user in the database: " + e.getMessage());
				String errorMessage = "Failed to store user to database: " + e.getMessage();
				logger.error(errorMessage, e); // Log the error message and the exception
			}

			connection.commit();
		} catch (SQLException e) {
			FXUtil.showAlert(Alert.AlertType.ERROR, "Database error", "Failed to establish a database connection: " + e.getMessage());
			String errorMessage = "Error while trying to connect to the database: " + e.getMessage();
			logger.error(errorMessage, e);
		}
	}

}
