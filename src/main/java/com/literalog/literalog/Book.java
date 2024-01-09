package com.literalog.literalog;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book {
    public StringProperty isbn = new SimpleStringProperty();
    public StringProperty title = new SimpleStringProperty();
    public StringProperty author = new SimpleStringProperty();
    public StringProperty publisher = new SimpleStringProperty();
    public IntegerProperty year = new SimpleIntegerProperty();
    public IntegerProperty price = new SimpleIntegerProperty();


    public Book(String isbn, String title, String author, String publisher, int year, int price) {
        this.isbn.set(isbn);
        this.title.set(title);
        this.author.set(author);
        this.publisher.set(publisher);
        this.year.set(year);
        this.price.set(price);
    }
}