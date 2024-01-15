package com.literalog.literalog;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainController {

    @FXML
    private Button dashboardButton;
    @FXML
    private Label dashboardButtonClickSign;
    @FXML
    private Button wishlistButton;
    @FXML
    private Label wishlistButtonClickSign;
    @FXML
    private Button bookmarksButton;
    @FXML
    private Label bookmarksButtonClickSign;
    @FXML
    private Button collectionButton;
    @FXML
    private Label collectionButtonClickSign;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginButtonClickSign;
    @FXML
    private AnchorPane content;

    String buttonChosenStyle = "-fx-background-color: rgb(42, 96, 131); -fx-background-radius: 4; -fx-alignment: center-left;";
    String buttonNotChosenStyle = "-fx-background-color: transparent; -fx-background-radius: 4; -fx-alignment: center-left;";

    @FXML
    public void initialize() {
        dashboardButton.setOnMouseEntered(this::onMouseEntered);
        wishlistButton.setOnMouseEntered(this::onMouseEntered);
        loginButton.setOnMouseEntered(this::onMouseEntered);
        bookmarksButton.setOnMouseEntered(this::onMouseEntered);
        collectionButton.setOnMouseEntered(this::onMouseEntered);

        dashboardButton.setOnMouseExited(this::onMouseExited);
        wishlistButton.setOnMouseExited(this::onMouseExited);
        loginButton.setOnMouseExited(this::onMouseExited);
        bookmarksButton.setOnMouseExited(this::onMouseExited);
        collectionButton.setOnMouseExited(this::onMouseExited);

        dashboardButton.setUserData(new SimpleBooleanProperty(false));
        wishlistButton.setUserData(new SimpleBooleanProperty(false));
        loginButton.setUserData(new SimpleBooleanProperty(true));
        bookmarksButton.setUserData(new SimpleBooleanProperty(false));
        collectionButton.setUserData(new SimpleBooleanProperty(false));

        dashboardButtonClickSign.styleProperty().bind((ObservableValue<? extends String>) Bindings.when((SimpleBooleanProperty) dashboardButton.getUserData())
                .then("-fx-background-color: rgb(76, 194, 255);")
                .otherwise("-fx-background-color: transparent;"));
        wishlistButtonClickSign.styleProperty().bind((ObservableValue<? extends String>) Bindings.when((SimpleBooleanProperty) wishlistButton.getUserData())
                .then("-fx-background-color: rgb(76, 194, 255);")
                .otherwise("-fx-background-color: transparent;"));
        loginButtonClickSign.styleProperty().bind((ObservableValue<? extends String>) Bindings.when((SimpleBooleanProperty) loginButton.getUserData())
                .then("-fx-background-color: rgb(76, 194, 255);")
                .otherwise("-fx-background-color: transparent;"));
        bookmarksButtonClickSign.styleProperty().bind((ObservableValue<? extends String>) Bindings.when((SimpleBooleanProperty) bookmarksButton.getUserData())
                .then("-fx-background-color: rgb(76, 194, 255);")
                .otherwise("-fx-background-color: transparent;"));
        collectionButtonClickSign.styleProperty().bind((ObservableValue<? extends String>) Bindings.when((SimpleBooleanProperty) collectionButton.getUserData())
                .then("-fx-background-color: rgb(76, 194, 255);")
                .otherwise("-fx-background-color: transparent;"));



        try{
            loadPage("Login.fxml");
            loginButton.setStyle(buttonChosenStyle);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void onMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();

        // Retrieve the current style
        String currentStyle = button.getStyle();

        // Modify only the background color
        String updatedStyle = "-fx-background-color: rgb(42, 96, 131);";

        // Combine the updated background color with the existing style
        if (!currentStyle.contains("-fx-background-color")) {
            updatedStyle = currentStyle + updatedStyle;
        } else {
            updatedStyle = currentStyle.replaceAll("-fx-background-color:([^;]*);", updatedStyle);
        }

        // Apply the updated style to the button
        button.setStyle(updatedStyle);
    }

    private void onMouseExited(MouseEvent event) {
        Button button = (Button) event.getSource();

        if(((SimpleBooleanProperty) button.getUserData()).get()) return;

        // Retrieve the current style
        String currentStyle = button.getStyle();

        // Modify only the background color to transparent
        String updatedStyle = currentStyle.replaceAll("-fx-background-color:([^;]*);", "-fx-background-color: transparent;");

        // Apply the updated style to the button
        button.setStyle(updatedStyle);
    }

    @FXML
    private void loadPage(String fxmlFileName) throws IOException {
        FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
        Node object = secondaryLoader.load();
        content.getChildren().setAll(object);
        AnchorPane.setLeftAnchor(object, 0.0);
        AnchorPane.setRightAnchor(object, 0.0);
        AnchorPane.setTopAnchor(object, 0.0);
        AnchorPane.setBottomAnchor(object, 0.0);
    }

    @FXML
    private void onDashboardButtonClick() throws IOException {
        loadPage("Dashboard.fxml");

        ((SimpleBooleanProperty) dashboardButton.getUserData()).set(true); dashboardButton.setStyle(buttonChosenStyle);
        ((SimpleBooleanProperty) collectionButton.getUserData()).set(false); collectionButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) wishlistButton.getUserData()).set(false); wishlistButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) bookmarksButton.getUserData()).set(false); bookmarksButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) loginButton.getUserData()).set(false); loginButton.setStyle(buttonNotChosenStyle);
    }

    @FXML
    private void onCollectionButtonClick() throws IOException {
        loadPage("Collection.fxml");

        ((SimpleBooleanProperty) dashboardButton.getUserData()).set(false); dashboardButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) collectionButton.getUserData()).set(true); collectionButton.setStyle(buttonChosenStyle);
        ((SimpleBooleanProperty) wishlistButton.getUserData()).set(false); wishlistButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) bookmarksButton.getUserData()).set(false); bookmarksButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) loginButton.getUserData()).set(false); loginButton.setStyle(buttonNotChosenStyle);
    }

    @FXML
    private void onWishlistButtonClick() throws IOException {
        loadPage("Wishlist.fxml");

        ((SimpleBooleanProperty) dashboardButton.getUserData()).set(false); dashboardButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) collectionButton.getUserData()).set(false); collectionButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) wishlistButton.getUserData()).set(true); wishlistButton.setStyle(buttonChosenStyle);
        ((SimpleBooleanProperty) bookmarksButton.getUserData()).set(false); bookmarksButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) loginButton.getUserData()).set(false); loginButton.setStyle(buttonNotChosenStyle);
    }

    @FXML
    private void onBookmarksButtonClick() throws IOException {
        loadPage("Bookmarks.fxml");

        ((SimpleBooleanProperty) dashboardButton.getUserData()).set(false); dashboardButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) collectionButton.getUserData()).set(false); collectionButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) wishlistButton.getUserData()).set(false); wishlistButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) bookmarksButton.getUserData()).set(true); bookmarksButton.setStyle(buttonChosenStyle);
        ((SimpleBooleanProperty) loginButton.getUserData()).set(false); loginButton.setStyle(buttonNotChosenStyle);
    }

    @FXML
    private void onLoginButtonClick() throws IOException {
        loadPage("Login.fxml");

        ((SimpleBooleanProperty) dashboardButton.getUserData()).set(false); dashboardButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) collectionButton.getUserData()).set(false); collectionButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) wishlistButton.getUserData()).set(false); wishlistButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) bookmarksButton.getUserData()).set(false); bookmarksButton.setStyle(buttonNotChosenStyle);
        ((SimpleBooleanProperty) loginButton.getUserData()).set(true); loginButton.setStyle(buttonChosenStyle);
    }
}