package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class LoginController {
    @FXML
    private Button loginButton;
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
    private Label messageText;
    public static boolean loginState;
    @FXML
    public void initialize(){
    }
    @FXML
    private void onLoginButtonClick(){
        if(!loginState){
            if(DatabaseUtils.openConnection(serverNameField.getText(), instanceNameField.getText(), portField.getText(), usernameField.getText(), passwordField.getText())){
                loginState = true;
                messageText.setText("Connected to the database");
            }else{
                messageText.setText("Failed to connect to the database");
            }
        }
    }
}
