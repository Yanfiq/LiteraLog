package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import com.yanfiq.literalog.utils.DialogUtils;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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

    @FXML
    public void initialize(){
        ArrayList<Book> container_collection = DatabaseUtils.getBooks("COLLECTION");
        ArrayList<Book> container_wishlist = DatabaseUtils.getBooks("WISHLIST");

        welcomeText.setText("Welcome to your heaven, " + User.loggedInUser.Username.get());
        stats_totalBookCollection.setText(Integer.toString(container_collection.size()));
        stats_totalBooksOnWishlist.setText(Integer.toString(container_wishlist.size()));
        stats_totalPagesRead.setText(Integer.toString(User.loggedInUser.totalPagesRead.get()));

        //date
        LocalDateTime fromDateTime = User.loggedInUser.accountCreated.get();
        LocalDateTime toDateTime = LocalDateTime.now();
        stats_totalTimeSpentAsANerd.setText(Integer.toString((int) ChronoUnit.SECONDS.between(fromDateTime, toDateTime)));
    }
}
