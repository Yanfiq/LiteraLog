package com.literalog.literalog;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.converter.IntegerStringConverter;

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
        lastPageReadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        lastPageReadColumn.setOnEditCommit(event -> {
            Book book = event.getTableView().getItems().get(event.getTablePosition().getRow());
            book.lastPage.set(event.getNewValue());
            book.lastTimeRead.set(LocalDateTime.now());
            LocalDateTime localDateTime = LocalDateTime.now();
            java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(localDateTime);

            AccessDB.manipulateTable("UPDATE [BOOKMARKS] SET [LastPage] = "+event.getNewValue()+" WHERE [ISBN] = "+book.isbn.get());
            AccessDB.manipulateTable("UPDATE [BOOKMARKS] SET [LastTimeRead] = '"+sqlTimestamp.toString()+"' WHERE [ISBN] = "+book.isbn.get());
        });
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
            value.bind(cellData.getValue().progress);
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
                book.lastPage.set(0);
                AccessDB.manipulateTable("DELETE FROM [BOOKMARKS] WHERE [ISBN] = "+book.isbn.get());
                bookmarksTable.getItems().remove(book);
            });
            return cell;
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ArrayList<Book> container = AccessDB.getData("SELECT * " +
                    "FROM [BOOKS] A, [BOOKMARKS] B " +
                    "WHERE A.ISBN = B.ISBN " +
                    "AND " +
                    "((A.ISBN LIKE '%" +newValue+"%') OR "+
                    "(A.Title LIKE '%" +newValue+"%') OR "+
                    "(A.Author LIKE '%" +newValue+"%'));");
            ObservableList<Book> bookList = bookmarksTable.getItems();
            bookList.clear();
            if(container != null){
                for(Book book : container){
                    bookList.add(book);
                }
            }
            bookmarksTable.setItems(bookList);
        });

        //get data from database
        ArrayList<Book> container = AccessDB.getData("SELECT * FROM [BOOKS] A, [BOOKMARKS] B WHERE A.ISBN = B.ISBN;");
        if(container != null){
            for(Book book : container){
                ObservableList<Book> bookList = bookmarksTable.getItems();
                bookList.add(book);
                bookmarksTable.setItems(bookList);
            }
        }
    }
}
