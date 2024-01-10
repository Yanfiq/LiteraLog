package com.literalog.literalog;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BookmarksController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Book> bookmarksTable;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Integer> lastPageReadColumn;
    @FXML
    private TableColumn<Book, LocalDateTime> lastTimeReadColumn;
    @FXML
    private TableColumn<Book, Double> progressColumn;
    @FXML
    private TableColumn<Book, Void> actionColumn;

    @FXML
    public void initialize(){
        // Initialize your table columns with property values from Book class
        isbnColumn.setCellValueFactory(cellData -> cellData.getValue().isbn);
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().author);
        lastPageReadColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().lastPage.get()).asObject());
        lastTimeReadColumn.setCellValueFactory(cellData -> cellData.getValue().lastTimeRead);
        lastTimeReadColumn.setCellFactory(column -> {
            TableCell<Book, LocalDateTime> cell = new TableCell<Book, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
            return cell;
        });

        progressColumn.setCellValueFactory(cellData -> {
            final DoubleProperty value = new SimpleDoubleProperty();
            value.bind(cellData.getValue().getProgress());
            return value.asObject();
        });
        progressColumn.setCellFactory(param -> {
            return new TableCell<Book, Double>() {
                private final ProgressBar progressBar = new ProgressBar();

                {
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        progressBar.setProgress(item);
                        setGraphic(progressBar);
                    }
                }
            };
        });

        actionColumn.setCellFactory(param -> {
            final Button removeButton = new Button("Remove");
            final Button unreadButton = new Button("Unread");
            final GridPane actionButton = new GridPane();
            actionButton.addColumn(0, unreadButton);
            actionButton.addColumn(1, removeButton);

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
                Book book = bookmarksTable.getItems().get(cell.getIndex());
                AccessDB.manipulateTable("DELETE FROM [BOOKMARKS] WHERE [ISBN] = "+book.isbn.get());
                AccessDB.manipulateTable("DELETE FROM [BOOKS] WHERE [ISBN] = "+book.isbn.get());
                bookmarksTable.getItems().remove(book);
            });
            unreadButton.setOnAction(event -> {
                Book book = bookmarksTable.getItems().get(cell.getIndex());
                AccessDB.manipulateTable("DELETE FROM [BOOKMARKS] WHERE [ISBN] = "+book.isbn.get());
            });
            return cell;
        });



//        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
//            ArrayList<Book> container = AccessDB.getData("SELECT * " +
//                    "FROM [BOOKS] " +
//                    "WHERE [ISBN] IN (SELECT [ISBN] FROM [BOOKMARKS]) " +
//                    "AND " +
//                    "(([ISBN] LIKE '%" +newValue+"%') OR "+
//                    "([Title] LIKE '%" +newValue+"%') OR "+
//                    "([Author] LIKE '%" +newValue+"%'));");
//            ObservableList<Book> bookList = bookmarksTable.getItems();
//            bookList.clear();
//            for(Book book : container){
//                bookList.add(book);
//            }
//            bookmarksTable.setItems(bookList);
//        });

        //get data from database
        ArrayList<Book> container = AccessDB.getData("SELECT * FROM [BOOKS] A, [BOOKMARKS] B WHERE A.ISBN = B.ISBN;");
        if(container != null){
            for(Book book : container){
                ObservableList<Book> bookList = bookmarksTable.getItems();
                bookList.add(book);
                bookmarksTable.setItems(bookList);
                System.out.println(book.lastPage);
            }
        }
    }
}
