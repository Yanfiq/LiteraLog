package com.yanfiq.literalog.models;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Book {
    public StringProperty isbn = new SimpleStringProperty();
    public StringProperty title = new SimpleStringProperty();
    public StringProperty author = new SimpleStringProperty();
    public IntegerProperty totalPage = new SimpleIntegerProperty();
    public StringProperty publisher = new SimpleStringProperty();
    public IntegerProperty year = new SimpleIntegerProperty();
    public IntegerProperty price = new SimpleIntegerProperty();
    public IntegerProperty lastPage = new SimpleIntegerProperty();
    public DoubleProperty progress = new SimpleDoubleProperty(0);
    public ObjectProperty<LocalDateTime> lastTimeRead = new SimpleObjectProperty<>();

    public Book(String isbn, String title, String author, int totalPage, String publisher, int year, int price) {
        this.isbn.set(isbn);
        this.title.set(title);
        this.author.set(author);
        this.totalPage.set(totalPage);
        this.publisher.set(publisher);
        this.year.set(year);
        this.price.set(price);
        this.lastPage.set(0);
        this.lastTimeRead.set(LocalDateTime.now());

        progress.bind(Bindings.createDoubleBinding(
                () -> this.lastPage.doubleValue()/this.totalPage.doubleValue(),
                this.lastPage,
                this.totalPage
        ));
    }
}