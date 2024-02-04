package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.Main;
import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import com.yanfiq.literalog.utils.DialogUtils;
import com.yanfiq.literalog.utils.FXMLUtils;
import javafx.animation.KeyValue;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class DashboardController {
    @FXML
    private GridPane dashboardView;
    @FXML
    private Label welcomeText;
    @FXML
    private Label stats_totalBookCollection;
    @FXML
    private Label stats_totalPagesRead;
    @FXML
    private Label stats_totalBooksOnWishlist;
    @FXML
    private Label stats_totalTimeSpentAsANerd;
    private Timeline timeline;

    @FXML
    public void initialize(){
        ArrayList<Book> container_collection = DatabaseUtils.getBooks("COLLECTION");
        ArrayList<Book> container_wishlist = DatabaseUtils.getBooks("WISHLIST");

        welcomeText.setText("Welcome to your heaven, " + User.loggedInUser.Username.get());
        stats_totalBookCollection.setText(Integer.toString(container_collection.size()));
        stats_totalBooksOnWishlist.setText(Integer.toString(container_wishlist.size()));
        stats_totalPagesRead.setText(Integer.toString(User.loggedInUser.totalPagesRead.get()));

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateTotalTime())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        //date
        LocalDateTime fromDateTime = User.loggedInUser.accountCreated.get();
        LocalDateTime toDateTime = LocalDateTime.now();
        stats_totalTimeSpentAsANerd.setText(Integer.toString((int) ChronoUnit.SECONDS.between(fromDateTime, toDateTime))+" Seconds");
    }

    private void updateTotalTime() {
        LocalDateTime fromDateTime = User.loggedInUser.accountCreated.get();
        LocalDateTime toDateTime = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(fromDateTime, toDateTime);

        // Update your label with the new total time
        stats_totalTimeSpentAsANerd.setText(Long.toString(seconds)+" Seconds");
    }

    @FXML
    private void onSignOutButtonClick(){
        timeline.stop();
        User.loggedInUser = null;

        Scene scene = new Scene(FXMLUtils.loadFXML("Login.fxml"), 1280, 720);
        scene.getStylesheets().add("com/yanfiq/literalog/css/ContainerStyles-dark.css");
        scene.getStylesheets().add("com/yanfiq/literalog/css/ControlStyles-dark.css");
        Main.getPrimaryStage().setScene(scene);
    }
}
