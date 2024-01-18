package com.yanfiq.literalog.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {
    public SimpleStringProperty Username = new SimpleStringProperty(null);
    public SimpleStringProperty Password = new SimpleStringProperty(null);
    public static SimpleStringProperty loggedInUser = new SimpleStringProperty(null);

    public static SimpleBooleanProperty isLoggedIn = new SimpleBooleanProperty(false);

    public User(String _username, String _password){
        this.Username.set(_username);
        this.Password.set(_password);
    }
}
