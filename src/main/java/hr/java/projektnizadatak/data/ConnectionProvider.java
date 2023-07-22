package hr.java.projektnizadatak.data;

import hr.java.projektnizadatak.presentation.Application;
import hr.java.projektnizadatak.presentation.FXUtil;
import javafx.scene.control.Alert;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

	public class ConnectionProvider {
		private static final Logger logger = LoggerFactory.getLogger(Application.class);
		private static final String DB_PROPERTIES_FILE = "data/db.properties";

		public static Connection getConnection() throws SQLException {
			Properties properties = loadProperties();
			String url = properties.getProperty("url");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");

			return DriverManager.getConnection(url, username, password);
		}

		private static Properties loadProperties() {
			Properties properties = new Properties();

			try (FileInputStream fileInputStream = new FileInputStream(DB_PROPERTIES_FILE)) {
				properties.load(fileInputStream);
			} catch (IOException e) {
				FXUtil.showAlert(Alert.AlertType.ERROR, "Connection Error", "Unable to read db.properties info");
				String m = "Error while trying to connect to the database";
				logger.error(m);
			}

			return properties;
		}
	}