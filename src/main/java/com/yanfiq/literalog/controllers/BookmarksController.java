package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
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
        isbnColumn.prefWidthProperty().bind(bookmarksTable.widthProperty().divide(7));
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        titleColumn.prefWidthProperty().bind(bookmarksTable.widthProperty().divide(7));
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().author);
        authorColumn.prefWidthProperty().bind(bookmarksTable.widthProperty().divide(7));
        lastPageReadColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().lastPage.get()).asObject());
        lastPageReadColumn.prefWidthProperty().bind(bookmarksTable.widthProperty().divide(7));
        lastPageReadColumn.setCellFactory(column -> {
            return new TableCell<Book, Integer>() {
                private final Spinner<Integer> spinner = new Spinner<>();

                {
                    spinner.setEditable(true);
                    spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                        Book book = getTableView().getItems().get(getIndex());
                        LocalDateTime localDateTime = LocalDateTime.now();
                        java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(localDateTime);
                        book.lastPage.set(newValue);
                        int lastTotalPage = User.loggedInUser.totalPagesRead.get();
                        User.loggedInUser.totalPagesRead.set((lastTotalPage-oldValue)+newValue);
//                        DatabaseUtils.manipulateTable("UPDATE [BOOKMARKS] SET [LastPage] = "+ newValue +" WHERE [ISBN] = " + book.isbn.get());
//                        DatabaseUtils.manipulateTable("UPDATE [USER] SET [TotalPagesRead] = "+((lastTotalPage-oldValue)+newValue)+";");
//                        DatabaseUtils.manipulateTable("UPDATE [BOOKMARKS] SET [LastTimeRead] = '" + sqlTimestamp + "' WHERE [ISBN] = "+book.isbn.get());
                    });
                }

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        Book book = getTableView().getItems().get(getIndex());
                        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, book.totalPage.get(), book.lastPage.get()));

                        setGraphic(spinner);
                    }
                }
            };
        });
        lastTimeReadColumn.setCellValueFactory(cellData -> cellData.getValue().lastTimeRead);
        lastTimeReadColumn.prefWidthProperty().bind(bookmarksTable.widthProperty().divide(7));
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
                        progressBar.prefWidthProperty().bind(progressColumn.prefWidthProperty());
                        setGraphic(progressBar);
                    }
                }
            };
        });
        progressColumn.prefWidthProperty().bind(bookmarksTable.widthProperty().divide(7));

        actionColumn.setCellFactory(param -> {
            final Button removeButton = new Button("Remove");
            final Button unreadButton = new Button("Unread");
            final HBox actionButton = new HBox();
            actionButton.getChildren().addAll(unreadButton, removeButton);
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
                Book book = bookmarksTable.getItems().get(cell.getIndex());
                DatabaseUtils.deleteBook("BOOKMARK", book);
                DatabaseUtils.deleteBook("COLLECTION", book);
                bookmarksTable.getItems().remove(book);
            });
            unreadButton.setOnAction(event -> {
                Book book = bookmarksTable.getItems().get(cell.getIndex());
                book.lastPage.set(0);
                DatabaseUtils.deleteBook("BOOKMARK", book);
                bookmarksTable.getItems().remove(book);
            });
            return cell;
        });
        actionColumn.prefWidthProperty().bind(bookmarksTable.widthProperty().divide(7));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ArrayList<Book> container = DatabaseUtils.getBooks("BOOKMARK", newValue);
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
        ArrayList<Book> container = DatabaseUtils.getBooks("BOOKMARKS");
        if(container != null){
            for(Book book : container){
                ObservableList<Book> bookList = bookmarksTable.getItems();
                bookList.add(book);
                bookmarksTable.setItems(bookList);
            }
        }
    }
}
