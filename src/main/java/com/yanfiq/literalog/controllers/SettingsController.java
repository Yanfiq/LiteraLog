package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.Main;
import com.yanfiq.literalog.config.ConfigManager;
import com.yanfiq.literalog.utils.DatabaseUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SettingsController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField serverNameField;
    @FXML
    private TextField instanceNameField;
    @FXML
    private TextField portField;
    @FXML
    private Label statusDB;
    @FXML
    private ComboBox<String> themeMenu = new ComboBox<>();
    @FXML
    private ComboBox<String> fontSizeMenu = new ComboBox<>();
    @FXML
    private Button applyButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private CheckBox autoconnectCheckbox;
    @FXML
    private ProgressIndicator connectionProgressIndicator;
    @FXML
    private Label connectabilityLabel;


    @FXML
    public void initialize(){
        statusDB.textProperty().bind(Bindings.when((SimpleBooleanProperty) DatabaseUtils.isConnected)
                .then("Connected")
                .otherwise("Not Connected"));
        loadSetting();
    }

    private void loadSetting(){
        themeMenu.getItems().addAll("Dark Mode", "Light Mode");
        themeMenu.setValue(ConfigManager.getTheme().equals("dark") ? "Dark Mode" : "Light Mode");
        fontSizeMenu.getItems().addAll("Small", "Medium", "Large");
        fontSizeMenu.setValue(ConfigManager.getFontSize().equals("small") ? "Small" : ConfigManager.getFontSize().equals("medium") ? "Medium" : "Large");

        serverNameField.setText(ConfigManager.getServerName());
        instanceNameField.setText(ConfigManager.getInstanceName());
        portField.setText(Integer.toString(ConfigManager.getPort()));
        usernameField.setText(ConfigManager.getUsername());
        passwordField.setText(ConfigManager.getPassword());

        autoconnectCheckbox.setSelected(ConfigManager.isAutoConnectEnabled());
    }
    @FXML
    private void onConnectButtonClick() {
        connectionProgressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        connectabilityLabel.setText("Establishing database connection");
        Task<Void> connectionTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Connection connection = null;
                    String dbUrl = "jdbc:sqlserver://" + serverNameField.getText() + "\\" + instanceNameField.getText() + ":" + portField.getText() + ";databaseName=LiteraLog;Encrypt=true;trustServerCertificate=true";

                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                    try {
                        connection = DriverManager.getConnection(dbUrl, usernameField.getText(), passwordField.getText());
                        updateUI(true);
                    } catch (SQLException e) {
                        updateUI(false);
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            return null;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    updateUI(false);
                }

                return null;
            }
        };

        connectionProgressIndicator.progressProperty().bind(connectionTask.progressProperty());

        connectionTask.setOnSucceeded(event -> {
            connectionProgressIndicator.progressProperty().unbind();
            connectionProgressIndicator.setProgress(1.0);
        });

        Thread thread = new Thread(connectionTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateUI(boolean isConnected) {
        Platform.runLater(() -> {
            connectabilityLabel.setText(isConnected ? "Connectable" : "Not Connectable");
            String progressBarStyle = isConnected ? "-fx-accent: rgb(108, 203, 95);" : "-fx-accent: rgb(255, 153, 164);";
            connectionProgressIndicator.setStyle(progressBarStyle);
        });
    }
    @FXML
    private void onApplyButtonClick(){
        String cssDir = "com/yanfiq/literalog/css/";
        Main.getPrimaryStage().getScene().getStylesheets().clear();
        Main.getPrimaryStage().getScene().getStylesheets().add( themeMenu.getValue().equals("Dark Mode") ? cssDir+"ContainerStyles-dark.css" : cssDir+"ContainerStyles-light.css");
        Main.getPrimaryStage().getScene().getStylesheets().add( themeMenu.getValue().equals("Dark Mode") ? cssDir+"ControlStyles-dark.css" : cssDir+"ControlStyles-light.css");

        DatabaseUtils.closeConnection();
        DatabaseUtils.openConnection(serverNameField.getText(), instanceNameField.getText(), portField.getText(), usernameField.getText(), passwordField.getText());
    }
    @FXML
    private void onSaveButtonClick(){
        ConfigManager.setServerName(serverNameField.getText());
        ConfigManager.setInstanceName(instanceNameField.getText());
        ConfigManager.setPort(Integer.parseInt(portField.getText()));
        ConfigManager.setUsername(usernameField.getText());
        ConfigManager.setPassword(passwordField.getText());
        ConfigManager.setAutoConnect(autoconnectCheckbox.isSelected());
        ConfigManager.setTheme(themeMenu.getValue());
    }
    @FXML
    private void onCancelButtonClick(){
        loadSetting();
    }
}
