package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.utils.FXMLUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    private final Map<Button, String> buttonFXMLMap = new HashMap<>();

    @FXML
    public void initialize() {
        initializeButton(dashboardButton, "Dashboard.fxml");
        initializeButton(wishlistButton, "Wishlist.fxml");
        initializeButton(bookmarksButton, "Bookmarks.fxml");
        initializeButton(collectionButton, "Collection.fxml");
        initializeButton(loginButton, "Login.fxml");

        try {
            loadPage("Login.fxml");
            setButtonChosen(loginButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeButton(Button button, String fxmlFileName) {
        button.setOnMouseEntered(this::onMouseEntered);
        button.setOnMouseExited(this::onMouseExited);
        button.setUserData(new SimpleBooleanProperty(false));
        buttonFXMLMap.put(button, fxmlFileName);

        // Bind the button style
        Label buttonClickSign = getButtonClickSign(button);
        buttonClickSign.styleProperty().bind(Bindings.when((SimpleBooleanProperty) button.getUserData())
                .then("-fx-background-color: rgb(76, 194, 255); -fx-background-radius: 4;")
                .otherwise("-fx-background-color: transparent; -fx-background-radius: 4;"));
    }

    private Label getButtonClickSign(Button button) {
        switch (button.getId()) {
            case "dashboardButton":
                return dashboardButtonClickSign;
            case "wishlistButton":
                return wishlistButtonClickSign;
            case "bookmarksButton":
                return bookmarksButtonClickSign;
            case "collectionButton":
                return collectionButtonClickSign;
            case "loginButton":
                return loginButtonClickSign;
            default:
                throw new IllegalArgumentException("Unknown button ID: " + button.getId());
        }
    }

    private void onMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();
        updateButtonStyle(button, "rgba(255, 255, 255, 0.1);");
    }

    private void onMouseExited(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (!((SimpleBooleanProperty) button.getUserData()).get()) {
            updateButtonStyle(button, "transparent");
        }
    }

    private void updateButtonStyle(Button button, String backgroundColor) {
        String updatedStyle = "-fx-background-color: " + backgroundColor + ";";
        button.setStyle(updatedStyle);
    }

    @FXML
    private void loadPage(String fxmlFileName) throws IOException {
        Node node = (Node) FXMLUtils.loadFXML(fxmlFileName);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        content.getChildren().setAll(node);
    }

    @FXML
    private void onButtonClick(Button button) throws IOException {
        loadPage(buttonFXMLMap.get(button));
        setButtonChosen(button);
    }

    private void setButtonChosen(Button chosenButton) {
        buttonFXMLMap.keySet().forEach(button -> {
            boolean isChosen = button == chosenButton;
            ((SimpleBooleanProperty) button.getUserData()).set(isChosen);
            updateButtonStyle(button, isChosen ? "rgba(255, 255, 255, 0.1);" : "transparent");
        });
    }
    @FXML
    private void onDashboardButtonClick() throws IOException {
        onButtonClick(dashboardButton);
    }

    @FXML
    private void onCollectionButtonClick() throws IOException {
        onButtonClick(collectionButton);
    }

    @FXML
    private void onWishlistButtonClick() throws IOException {
        onButtonClick(wishlistButton);
    }

    @FXML
    private void onBookmarksButtonClick() throws IOException {
        onButtonClick(bookmarksButton);
    }

    @FXML
    private void onLoginButtonClick() throws IOException {
        onButtonClick(loginButton);
    }
}