package com.literalog.literalog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

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
    private Pane content;

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
            content.getChildren().setAll((Node) secondaryLoader.load());
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
    private void onWishlistButtonClick() throws IOException {
        FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource("Wishlist.fxml"));
        content.getChildren().setAll((Node) secondaryLoader.load());
    }

    @FXML
    private void onDashboardButtonClick() throws IOException {
        FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        content.getChildren().setAll((Node) secondaryLoader.load());
    }

    @FXML
    private void onBookmarksButtonClick() throws IOException {
        FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource("Bookmarks.fxml"));
        content.getChildren().setAll((Node) secondaryLoader.load());
    }

    @FXML
    private void onLoginButtonClick() throws IOException {
        FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        content.getChildren().setAll((Node) secondaryLoader.load());
    }

    @FXML
    private void onCollectionButtonClick() throws IOException {
        FXMLLoader secondaryLoader = new FXMLLoader(getClass().getResource("Collection.fxml"));
        content.getChildren().setAll((Node) secondaryLoader.load());
    }
}