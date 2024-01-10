package com.literalog.literalog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    private void onLoginButtonClick(){
        if(!loginState){
            if(AccessDB.openConnection(serverNameField.getText(), instanceNameField.getText(), portField.getText(), usernameField.getText(), passwordField.getText())){
                loginState = true;
                messageText.setText("Connected to the database");
            }else{
                messageText.setText("Failed to connect to the database");
            }
        }
    }
}
