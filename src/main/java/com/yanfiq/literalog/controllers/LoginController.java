package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.Main;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import com.yanfiq.literalog.utils.DialogUtils;
import com.yanfiq.literalog.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    private Stage setting = null;

    @FXML
    private void onLoginButtonClick(){
        ArrayList<User> users = DatabaseUtils.getUsersData();
        if(users == null) return;
        String _username = usernameField.getText();
        String _password = passwordField.getText();
        boolean usernameExist = false, passwordCorrect = false;
        for(User user : users){
            if(user.Username.get().equals(_username)){
                usernameExist = true;
                if(user.Password.get().equals(_password)){
                    passwordCorrect = true;
                    User.loggedInUser = user;
                    User.isLoggedIn.set(true);

                    if(setting != null)setting.close();

                    Scene scene = new Scene(FXMLUtils.loadFXML("Main.fxml"), 1280, 720);
                    scene.getStylesheets().add("com/yanfiq/literalog/css/ContainerStyles-dark.css");
                    scene.getStylesheets().add("com/yanfiq/literalog/css/ControlStyles-dark.css");
                    Main.getPrimaryStage().setScene(scene);
                }
            }
        }
        if(!usernameExist){
            DialogUtils.showAlert(Alert.AlertType.ERROR, "Username doesn't exist", "Kontol");
        }else if(!passwordCorrect){
            DialogUtils.showAlert(Alert.AlertType.ERROR, "Wrong password idiot", "Kontol");
        }
    }

    @FXML
    private void onRegisterButtonClick(){
        ArrayList<User> users = DatabaseUtils.getUsersData();
        String _username = usernameField.getText();
        String _password = passwordField.getText();
        boolean usernameExist = false;
        for(User user : users){
            if(user.Username.get().equals(_username)){
                usernameExist = true;
                // username already in use
                break;
            }
        }
        if(!usernameExist){
            DatabaseUtils.manipulateTable("INSERT INTO [USER]");
        }
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
        }else{
            setting.requestFocus();
        }
    }
}
