package com.literalog.literalog;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

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
    @FXML
    private GridPane gridLayout;
    public static boolean loginState;
    @FXML
    public void initialize(){
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(javafx.scene.layout.Priority.ALWAYS);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(javafx.scene.layout.Priority.ALWAYS);

        //gridLayout.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        //gridLayout.getRowConstraints().add(rowConstraints);
        //gridLayout.getColumnConstraints().add(columnConstraints);
    }
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
