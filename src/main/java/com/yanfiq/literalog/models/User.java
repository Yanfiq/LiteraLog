package com.yanfiq.literalog.models;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class User {
    public SimpleStringProperty Username = new SimpleStringProperty(null);
    public SimpleStringProperty Password = new SimpleStringProperty(null);
    public SimpleIntegerProperty totalPagesRead = new SimpleIntegerProperty(0);
    public SimpleObjectProperty<LocalDateTime> accountCreated = new SimpleObjectProperty<>();
    public static User loggedInUser = new User(null, null, null, 0);

    public User(String _username, String _password, LocalDateTime _accountCreated, int _totalPagesRead){
        this.Username.set(_username);
        this.Password.set(_password);
        this.totalPagesRead.set(_totalPagesRead);
        this.accountCreated.set(_accountCreated);
    }
}
