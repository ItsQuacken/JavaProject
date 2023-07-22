package hr.java.projektnizadatak.presentation;

import hr.java.projektnizadatak.data.UsersFileStore;
import hr.java.projektnizadatak.entities.UserManager;
import hr.java.projektnizadatak.shared.exceptions.FxmlLoadingException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Stack;

@SuppressWarnings("FieldMayBeFinal")
public class Application extends javafx.application.Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	private static Stage stage;
	private static Stack<ApplicationScreen> screenStack;
	private static UserManager userManager = new UserManager(new UsersFileStore());

	public static void main(String[] args) {
		launch();
	}

	public static void switchToScreen(ApplicationScreen screen) {
		screenStack.pop();
		screenStack.push(screen);
		setScreen(screenStack.peek());
	}

	public static void pushScreen(ApplicationScreen screen) {
		screenStack.push(screen);
		setScreen(screenStack.peek());
	}

	private static void setScreen(ApplicationScreen screen) {

		try {
			var fxmlPath = screen.getFxmlPath();
			var window = (Parent) FXMLLoader.load(Objects.requireNonNull(Application.class.getResource(fxmlPath)));
			var scene = new Scene(window);
			stage.setScene(scene);
			stage.setTitle(screen.getTitle());
			stage.show();
		} catch (IOException e) {
			String m = "Loading FXML for screen: " + screen;
			logger.error(m);

			throw new FxmlLoadingException(m, e);
		}
	}

	public static UserManager getUserManager() {
		return userManager;
	}
	@Override
	public void start(Stage stage) {
		Application.stage = stage;
		Application.screenStack = new Stack<>();

		pushScreen(ApplicationScreen.Login);
	}
}
