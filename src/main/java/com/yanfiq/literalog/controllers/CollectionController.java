package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class CollectionController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Book> collectionTable;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Integer> totalPageColumn;
    @FXML
    private TableColumn<Book, String> publisherColumn;
    @FXML
    private TableColumn<Book, Integer> yearColumn;
    @FXML
    private TableColumn<Book, Integer> priceColumn;
    @FXML
    private TableColumn<Book, Void> actionColumn;
    
    @FXML
    public void initialize() {
        // Initialize your table columns with property values from Book class
        isbnColumn.setCellValueFactory(cellData -> cellData.getValue().isbn);
        isbnColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));
        isbnColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        titleColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().author);
        authorColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));
        totalPageColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().totalPage.get()).asObject());
        totalPageColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));
        publisherColumn.setCellValueFactory(cellData -> cellData.getValue().publisher);
        publisherColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));
        yearColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().year.get()).asObject());
        yearColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));
        priceColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().price.get()).asObject());
        priceColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));

        actionColumn.setCellFactory(param -> {
            final Button removeButton = new Button("Remove");
            final Button readButton = new Button("Read");
            final HBox actionButton = new HBox();
            actionButton.getChildren().addAll(readButton, removeButton);
            actionButton.setSpacing(2);

            TableCell<Book, Void> cell = new TableCell<>() {
                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionButton);
                    }
                }
            };

            removeButton.setOnAction(event -> {
                Book book = collectionTable.getItems().get(cell.getIndex());
                DatabaseUtils.deleteBook("BOOKMARK", book);
                DatabaseUtils.deleteBook("COLLECTION", book);
                collectionTable.getItems().remove(book);
            });
            readButton.setOnAction(event -> {
                Book book = collectionTable.getItems().get(cell.getIndex());
                book.lastTimeRead.set(LocalDateTime.now());
                    DatabaseUtils.insertBook("BOOKMARKS", book);
            });
            return cell;
        });
        actionColumn.prefWidthProperty().bind(collectionTable.widthProperty().divide(8));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ArrayList<Book> container = DatabaseUtils.getBooks("COLLECTION", newValue);
            ObservableList<Book> bookList = collectionTable.getItems();
            bookList.clear();
            for(Book book : container){
                bookList.add(book);
            }
            collectionTable.setItems(bookList);
        });

        //get data from database
        ArrayList<Book> container = DatabaseUtils.getBooks("COLLECTION");
        if(container != null){
            for(Book book : container){
                ObservableList<Book> bookList = collectionTable.getItems();
                bookList.add(book);
                collectionTable.setItems(bookList);
            }
        }
    }
}
