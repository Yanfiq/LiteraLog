package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.utils.FXMLUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

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
    private Button settingsButton;
    @FXML
    private Label settingsButtonClickSign;
    @FXML
    private AnchorPane content;

    private final Map<Button, String> buttonFXMLMap = new HashMap<>();

    @FXML
    public void initialize() {
        initializeButton(dashboardButton, "Dashboard.fxml");
        initializeButton(wishlistButton, "Wishlist.fxml");
        initializeButton(bookmarksButton, "Bookmarks.fxml");
        initializeButton(collectionButton, "Collection.fxml");
        initializeButton(settingsButton, "Settings.fxml");

        try {
            loadPage("Login.fxml");
//            setButtonChosen(loginButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeButton(Button button, String fxmlFileName) {
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
            case "settingsButton":
                return settingsButtonClickSign;
            default:
                throw new IllegalArgumentException("Unknown button ID: " + button.getId());
        }
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
            button.getStyleClass().clear();
            button.getStyleClass().add(isChosen ? "button-selection-chosen" : "button-selection-not-chosen");
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
    private void onSettingsButtonClick() throws IOException {
        onButtonClick(settingsButton);
    }
}