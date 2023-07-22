package hr.java.projektnizadatak.presentation.views;

import hr.java.projektnizadatak.entities.ChangeLog;
import hr.java.projektnizadatak.entities.EventLogEntry;
import hr.java.projektnizadatak.entities.User;
import hr.java.projektnizadatak.entities.UserRole;
import hr.java.projektnizadatak.presentation.Application;
import hr.java.projektnizadatak.presentation.ApplicationScreen;
import hr.java.projektnizadatak.presentation.FXUtil;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManageUsersView {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final String USER_FILE = "data/users.txt";

    private static final String LOGS_FILE = "data/events.log";
    private static final String CHANGE_LOG_FILE = "data/changes.dat";

    private Map<String, String> userList = new HashMap<>();
    private ObservableList<ChangeLog> changeLogs = FXCollections.observableArrayList();

    @FXML
    private TableView<User> userTableView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    @FXML
    private TableView<ChangeLog> logTableView;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<ChangeLog, String> action;
    @FXML
    private TableColumn<ChangeLog, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<ChangeLog, String> deletedUserColumn;
    @FXML
    private TableColumn<ChangeLog, String> deleterUserColumn;

    @FXML
    private TableView<EventLogEntry> eventLogTable;
    @FXML
    private TableColumn<EventLogEntry, String> messageColumn;

    @FXML
    private void initialize() {
        loadData();


        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("passwordHash"));
        action.setCellValueFactory(new PropertyValueFactory<>("action"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        deletedUserColumn.setCellValueFactory(new PropertyValueFactory<>("deleted"));
        deleterUserColumn.setCellValueFactory(new PropertyValueFactory<>("deleter"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));

        userTableView.getColumns().clear();

        userTableView.getColumns().addAll(usernameColumn, passwordColumn);

        setUserTableData();
        loadEventLogs();

        refreshButton.disableProperty().bind(Bindings.isEmpty(eventLogTable.getItems()));

        deleteButton.disableProperty().bind(userTableView.getSelectionModel().selectedItemProperty().isNull());

        loadChangeLogs();
        logTableView.setItems(changeLogs);

        action.setCellValueFactory(new PropertyValueFactory<>("action"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        deletedUserColumn.setCellValueFactory(new PropertyValueFactory<>("deletedUser"));
        deleterUserColumn.setCellValueFactory(new PropertyValueFactory<>("deleterUser"));
    }

    @FXML
    private void logout() {
        Application.switchToScreen(ApplicationScreen.Login);
        FXUtil.showAlert(Alert.AlertType.INFORMATION, "Logged out", "Successfully logged out. You may safely exit the application.");
        String m = "Admin logged out";
        logger.info(m);
    }

    @FXML
    private void deleteSelectedUser() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {

            User loggedInUser = Application.getUserManager().getLoggedInUser();

            if (selectedUser.getUsername().equals(loggedInUser.getUsername())) {

                FXUtil.showAlert(Alert.AlertType.INFORMATION, "Delete user", "Cannot remove the currently logged-in user.");
                String m = "Admin tried to delete themselves";
                logger.error(m);
            } else {
                Alert confirmationAlert = FXUtil.createAlert(
                        Alert.AlertType.CONFIRMATION,
                        "Delete User",
                        "Are you sure you want to delete the selected user?",
                        "User deleted"
                );

                Optional<ButtonType> result = confirmationAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = LocalDateTime.now().format(formatter);


                    addChangeLog("User deleted", formattedDate, selectedUser, loggedInUser);

                    userList.remove(selectedUser.getUsername());
                    setUserTableData();

                    FXUtil.showAlert(Alert.AlertType.INFORMATION, "User Deleted", "User successfully deleted");
                    String m = "Admin deleted user";
                    logger.info(m);
                }
            }
        }
    }



    @FXML
    private void saveUserData() {
        ObservableList<User> users = userTableView.getItems();

        File file = new File(USER_FILE);
        if (file.exists()) {
            file.delete();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (User user : users) {
                String line = user.getUsername() + ":" + user.getPasswordHash() + ":" + getUserRoleString(user);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FXUtil.showAlert(
                Alert.AlertType.INFORMATION,
                "Changes Saved",
                "User changes saved."
        );
        String m = "Admin saved changes";
        logger.error(m);
    }

    private String getUserRoleString(User user) {
        if (user.getUsername().equalsIgnoreCase("admin")) {
            return "ADMIN";
        } else {

            switch (user.getUserRole()) {
                case ADMIN:
                    return "ADMIN";
                case USER:
                    return "USER";
                default:
                    return "";
            }
        }
    }


    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String username = parts[0];
                    String passwordHash = parts[1];
                    UserRole userRole = UserRole.valueOf(parts[2]);
                    userList.put(username, passwordHash);
                }
            }
        } catch (IOException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load user data");
            String m = "Failed to load user data for admin";
            logger.error(m);
        }
    }

    private void setUserTableData() {
        ObservableList<User> observableUserList = FXCollections.observableArrayList();
        for (Map.Entry<String, String> entry : userList.entrySet()) {
            User user = new User(entry.getKey(), entry.getValue(), UserRole.USER);
            observableUserList.add(user);
        }
        userTableView.setItems(observableUserList);
    }

    private void addChangeLog(String action, String dateTime, User deletedUser, User deleterUser) {

        String deletedUsername = deletedUser.getUsername();
        String deleterUsername = deleterUser.getUsername();


        ChangeLog changeLog = new ChangeLog(action, dateTime, deletedUsername, deleterUsername, deletedUser.getUserRole() == UserRole.ADMIN ? UserRole.ADMIN : UserRole.USER);
        changeLogs.add(changeLog);
        saveChangeLogs();
    }

    @FXML
    private void refreshEventLogs() {

        eventLogTable.getItems().clear();

        List<EventLogEntry> eventLogs = readEventLogsFromFile();

        eventLogTable.getItems().addAll(eventLogs);
    }


    private void loadChangeLogs() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(CHANGE_LOG_FILE))) {
            Object object;
            while ((object = inputStream.readObject()) != null) {
                if (object instanceof ChangeLog) {
                    ChangeLog logEntry = (ChangeLog) object;
                    changeLogs.add(logEntry);
                }
            }
        } catch (EOFException e) {

        } catch (IOException | ClassNotFoundException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Error loading", "Error while loading changes");
            String m = "Error while loading changes";
            logger.error(m);
        }
    }


    private void saveChangeLogs() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(CHANGE_LOG_FILE))) {
            for (ChangeLog logEntry : changeLogs) {
                outputStream.writeObject(logEntry);
            }
        } catch (IOException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Save Error", "Failed to save Change log");
            String m = "Failed to save user data admin changed";
            logger.error(m);
        }
    }

    private void loadEventLogs() {
        List<EventLogEntry> eventLogs = readEventLogsFromFile();

        eventLogTable.getItems().addAll(eventLogs);

        refreshButton.setDisable(false);
    }

    private List<EventLogEntry> readEventLogsFromFile() {
        List<EventLogEntry> eventLogs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                EventLogEntry logEntry = parseEventLogEntry(line);
                if (logEntry != null) {
                    eventLogs.add(logEntry);
                }
            }
        } catch (IOException e) {
            FXUtil.showAlert(Alert.AlertType.ERROR, "Error loading", "Error loading events");
            String m = "Error reading from event file";
            logger.error(m);
        }

        return eventLogs;
    }

    private EventLogEntry parseEventLogEntry(String logEntry) {
        return new EventLogEntry(logEntry);
    }

}