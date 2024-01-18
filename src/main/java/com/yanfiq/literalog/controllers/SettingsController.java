package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.utils.DatabaseUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    public void initialize(){
        statusDB.textProperty().bind(Bindings.when((SimpleBooleanProperty) DatabaseUtils.isConnected)
                .then("Connected")
                .otherwise("Not Connected"));

        themeMenu.getItems().addAll("Dark Mode", "Light Mode");
        themeMenu.setValue("Dark Mode");

        fontSizeMenu.getItems().addAll("Small", "Medium", "Large");
        fontSizeMenu.setValue("Small");
    }
    @FXML
    private void onConnectButtonClick(){
        if(!DatabaseUtils.isConnected.get()){
            DatabaseUtils.openConnection(serverNameField.getText(), instanceNameField.getText(), portField.getText(), usernameField.getText(), passwordField.getText());
        }
    }
}
