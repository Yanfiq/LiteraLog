package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static javafx.scene.paint.Color.rgb;

public class DashboardController {
    @FXML
    private GridPane loginPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
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
        loginPane.visibleProperty().bind(Bindings.when(User.isLoggedIn)
                .then(false)
                .otherwise(true));
        dashboardView.visibleProperty().bind(User.isLoggedIn);

        if(!loginPane.isVisible()){
            ArrayList<Book> container_collection = DatabaseUtils.getBooksData("SELECT * " +
                    "FROM [BOOKS] B, [COLLECTION] C " +
                    "WHERE B.ISBN = C.ISBN " +
                    "AND C.Username = '" + User.loggedInUser.Username.get() + "';");
            ArrayList<Book> container_wishlist = DatabaseUtils.getBooksData("SELECT * " +
                    "FROM [BOOKS] B, [WISHLIST] W " +
                    "WHERE B.ISBN = W.ISBN " +
                    "AND W.Username = '" + User.loggedInUser.Username.get() + "';");

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

    @FXML
    private void onLoginButtonClick(){
        ArrayList<User> users = DatabaseUtils.getUsersData();
        String _username = usernameField.getText();
        String _password = passwordField.getText();
        for(User user : users){
            if(user.Username.get().equals(_username) && user.Password.get().equals(_password)){
                User.loggedInUser = user;
                User.isLoggedIn.set(true);

//                ArrayList<Book> container_collection = DatabaseUtils.getBooksData("SELECT * " +
//                        "FROM [BOOKS] B, [COLLECTION] C " +
//                        "WHERE B.ISBN = C.ISBN " +
//                        "AND C.Username = '" + User.loggedInUser.Username.get() + "';");
//                ArrayList<Book> container_wishlist = DatabaseUtils.getBooksData("SELECT * " +
//                        "FROM [BOOKS] B, [WISHLIST] W " +
//                        "WHERE B.ISBN = W.ISBN " +
//                        "AND W.Username = '" + User.loggedInUser.Username.get() + "';");
//
//                welcomeText.setText("Welcome to your heaven, " + User.loggedInUser.Username.get());
//                stats_totalBookCollection.setText(Integer.toString(container_collection.size()));
//                stats_totalBooksOnWishlist.setText(Integer.toString(container_wishlist.size()));
//                stats_totalPagesRead.setText(Integer.toString(User.loggedInUser.totalPagesRead.get()));
//
//                //date
//                LocalDateTime fromDateTime = User.loggedInUser.accountCreated.get();
//                LocalDateTime toDateTime = LocalDateTime.now();
//                stats_totalTimeSpentAsANerd.setText(Integer.toString((int) ChronoUnit.SECONDS.between(fromDateTime, toDateTime)));
                return;
            }
        }
    }
}
