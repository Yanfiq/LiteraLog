package com.literalog.literalog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;

public class MainController {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button wishlistButton;
    @FXML
    private Button bookmarksButton;
    @FXML
    private Button collectionButton;
    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane content;

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

        try {
            FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Node object = secondaryLoader.load();
            content.getChildren().setAll(object);
            //content.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            //object.
            AnchorPane.setLeftAnchor(object, 0.0);
            AnchorPane.setRightAnchor(object, 0.0);
            AnchorPane.setTopAnchor(object, 0.0);
            AnchorPane.setBottomAnchor(object, 0.0);
        } catch (Exception e){
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

        // Retrieve the current style
        String currentStyle = button.getStyle();

        // Modify only the background color to transparent
        String updatedStyle = currentStyle.replaceAll("-fx-background-color:([^;]*);", "-fx-background-color: transparent;");

        // Apply the updated style to the button
        button.setStyle(updatedStyle);
    }

    @FXML
    private void onButtonClick(String fxmlFileName) throws IOException {
        FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
        Node object = secondaryLoader.load();
        content.getChildren().setAll(object);
        AnchorPane.setLeftAnchor(object, 0.0);
        AnchorPane.setRightAnchor(object, 0.0);
        AnchorPane.setTopAnchor(object, 0.0);
        AnchorPane.setBottomAnchor(object, 0.0);
    }

    @FXML
    private void onWishlistButtonClick() throws IOException {
        onButtonClick("Wishlist.fxml");
    }

    @FXML
    private void onDashboardButtonClick() throws IOException {
        onButtonClick("Dashboard.fxml");
    }

    @FXML
    private void onBookmarksButtonClick() throws IOException {
        onButtonClick("Bookmarks.fxml");
    }

    @FXML
    private void onLoginButtonClick() throws IOException {
        onButtonClick("Login.fxml");
    }

    @FXML
    private void onCollectionButtonClick() throws IOException {
        onButtonClick("Collection.fxml");
    }
}