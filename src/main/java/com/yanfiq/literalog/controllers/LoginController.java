package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.Main;
import com.yanfiq.literalog.config.ConfigManager;
import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import com.yanfiq.literalog.utils.DialogUtils;
import com.yanfiq.literalog.utils.FXMLUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.xml.crypto.Data;

public class LoginController {
    @FXML
    private GridPane SignInPane;
    @FXML
    private GridPane SignUpPane;
    @FXML
    private Hyperlink SignInHyperlink;
    @FXML
    private Hyperlink SignUpHyperlink;
    @FXML
    private TextField signin_usernameField;
    @FXML
    private PasswordField signin_passwordField;
    @FXML
    private TextField signup_usernameField;
    @FXML
    private PasswordField signup_passwordField;
    @FXML
    private CheckBox rememberMe;
    @FXML
    private Button signUpButton;
    @FXML
    private Button signInButton;
    private Stage setting = null;
    @FXML
    public void initialize(){
        SignInPane.setVisible(true);
        SignUpPane.setVisible(false);

        bindDisableState(signin_usernameField);
        bindDisableState(signin_passwordField);
        bindDisableState(signup_usernameField);
        bindDisableState(signup_passwordField);
        bindDisableState(signInButton);
        bindDisableState(signUpButton);

        SignInHyperlink.setOnAction(event -> {
            SignInPane.setVisible(true);
            SignUpPane.setVisible(false);
        });
        SignUpHyperlink.setOnAction(event -> {
            SignInPane.setVisible(false);
            SignUpPane.setVisible(true);
        });

        if(!DatabaseUtils.isConnected.get() && !DatabaseUtils.isConnecting.get()){
            DialogUtils.showAlert(Alert.AlertType.WARNING, "Database not connected", "connection to the database has not been established", "In order to be able to sign-in, first connect the application to the database in the settings");
        }
        if(ConfigManager.isRememberMe()){
            rememberMe.setSelected(true);
            signin_usernameField.setText(ConfigManager.getLastLoginUsername());
        }
    }

    private void bindDisableState(TextField field){
        field.disableProperty().bind(Bindings.when(DatabaseUtils.isConnected)
                .then(false)
                .otherwise(true));
    }
    private void bindDisableState(PasswordField field){
        field.disableProperty().bind(Bindings.when(DatabaseUtils.isConnected)
                .then(false)
                .otherwise(true));
    }
    private void bindDisableState(Button button){
        button.disableProperty().bind(Bindings.when(DatabaseUtils.isConnected)
                .then(false)
                .otherwise(true));
    }

    @FXML
    private void onSignInButtonClick(){
        String _username = signin_usernameField.getText();
        String _password = signin_passwordField.getText();
        User user_found = DatabaseUtils.getUserData(_username, _password);
        if(user_found==null){
            if(!DatabaseUtils.isUsernameAvailable(_username)){
                DialogUtils.showAlert(Alert.AlertType.ERROR, "ERROR", "Username doesn't exist", null);
            }else if(!DatabaseUtils.isPasswordCorrect(_username, _password)){
                DialogUtils.showAlert(Alert.AlertType.ERROR, "ERROR", "Wrong password", null);
            }
        }else{
            if(setting != null)setting.close();

            if(rememberMe.isSelected()){
                ConfigManager.setLastLoginUsername(_username);
            }
            ConfigManager.setRememberMe(rememberMe.isSelected());

            User.loggedInUser = user_found;
            Scene scene = new Scene(FXMLUtils.loadFXML("Main.fxml"), 1280, 720);
            scene.getStylesheets().add("com/yanfiq/literalog/css/ContainerStyles-dark.css");
            scene.getStylesheets().add("com/yanfiq/literalog/css/ControlStyles-dark.css");
            Main.getPrimaryStage().setScene(scene);
        }
    }

    @FXML
    private void onSignUpButtonClick(){
//        ArrayList<User> users = DatabaseUtils.getUsersData();
//        String _username = signin_usernameField.getText();
//        String _password = signin_passwordField.getText();
//        boolean usernameExist = false;
//        for(User user : users){
//            if(user.Username.get().equals(_username)){
//                usernameExist = true;
//                // username already in use
//                break;
//            }
//        }
//        if(!usernameExist){
////            DatabaseUtils.manipulateTable("INSERT INTO [USER]");
//        }
    }

    @FXML
    private void onSettingsButtonClick(){
        if(setting==null){
            setting = new Stage();
            Scene scene = new Scene(FXMLUtils.loadFXML("Settings.fxml"), 1100, 720);
            scene.getStylesheets().add("com/yanfiq/literalog/css/ContainerStyles-dark.css");
            scene.getStylesheets().add("com/yanfiq/literalog/css/ControlStyles-dark.css");
            setting.setScene(scene);
            setting.show();
            setting.setOnCloseRequest(event -> {
                setting = null;
            });
        }else{
            setting.requestFocus();
        }
    }
}
