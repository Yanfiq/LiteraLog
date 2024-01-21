package com.yanfiq.literalog.controllers;

import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import com.yanfiq.literalog.utils.DatabaseUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class WishlistController {
    @FXML
    private TextField isbnInput;
    @FXML
    private TextField titleInput;
    @FXML
    private TextField authorInput;
    @FXML
    private TextField totalPageInput;
    @FXML
    private TextField publisherInput;
    @FXML
    private TextField yearInput;
    @FXML
    private TextField priceInput;
    @FXML
    private Button wishlistButton;

    @FXML
    private TableView<Book> wishlistTable;
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
//        wishlistTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        isbnColumn.setCellValueFactory(cellData -> cellData.getValue().isbn);
        isbnColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        titleColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().author);
        authorColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));
        totalPageColumn.setCellValueFactory(celldata -> new SimpleIntegerProperty(celldata.getValue().totalPage.get()).asObject());
        totalPageColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));
        publisherColumn.setCellValueFactory(cellData -> cellData.getValue().publisher);
        publisherColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));
        yearColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().year.get()).asObject());
        yearColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));
        priceColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().price.get()).asObject());
        priceColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));

        actionColumn.setCellFactory(param -> {
            final Button removeButton = new Button("Remove");
            final Button buyButton = new Button("Buy");
            final HBox actionbutton = new HBox();
            actionbutton.getChildren().addAll(buyButton, removeButton);
            actionbutton.setSpacing(2);

            TableCell<Book, Void> cell = new TableCell<>() {
                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionbutton);
                    }
                }
            };

            removeButton.setOnAction(event -> {
                Book book = wishlistTable.getItems().get(cell.getIndex());
                String isbn = book.isbn.get();
                wishlistTable.getItems().remove(book);

                DatabaseUtils.manipulateTable("DELETE FROM [WISHLIST] WHERE [ISBN] = " + isbn + " AND [Username] = '" + User.loggedInUser.Username.get() + "';");
                DatabaseUtils.manipulateTable("DELETE FROM [BOOKS] WHERE [ISBN] = " + isbn);
            });
            buyButton.setOnAction(event -> {
                Book book = wishlistTable.getItems().get(cell.getIndex());
                String isbn = book.isbn.get();
                wishlistTable.getItems().remove(book);
                DatabaseUtils.manipulateTable("DELETE FROM [WISHLIST] WHERE [ISBN] = "+isbn + " AND [Username] = '" + User.loggedInUser.Username.get() + "';");
                DatabaseUtils.manipulateTable("INSERT INTO [COLLECTION] VALUES ('" + User.loggedInUser.Username.get() + "', '" + isbn + "');");
            });
            return cell;
        });
        actionColumn.prefWidthProperty().bind(wishlistTable.widthProperty().divide(8));

        //get data from database
        ArrayList<Book> container = DatabaseUtils.getBooksData("SELECT * FROM [BOOKS] B, [WISHLIST] W WHERE B.ISBN = W.ISBN AND W.Username = '"+ User.loggedInUser.Username.get() + "';");
        if(container != null){
            for(Book book : container){
                ObservableList<Book> bookList = wishlistTable.getItems();
                bookList.add(book);
                wishlistTable.setItems(bookList);
            }
        }
    }
    @FXML
    private void addWishlist(){
        String isbn = isbnInput.getText();
        String title = titleInput.getText();
        String author = authorInput.getText();
        int totalPage = Integer.parseInt(totalPageInput.getText());
        String publisher = publisherInput.getText();
        int year = Integer.parseInt(yearInput.getText());
        int price = Integer.parseInt(priceInput.getText());
        Book book = new Book(isbn, title, author, totalPage, publisher, year, price);
        ObservableList<Book> bookList = wishlistTable.getItems();
        bookList.add(book);
        wishlistTable.setItems(bookList);

        if(DatabaseUtils.isConnected.get()){
            String query_book = "INSERT INTO [BOOKS] VALUES "+String.format("('%s', '%s' ,'%s' ,%d ,'%s' ,%d ,%d)", isbn, title, author, totalPage, publisher, year, price);
            String query_wishlist = "INSERT INTO [WISHLIST] VALUES "+isbn;
            DatabaseUtils.manipulateTable(query_book);
            DatabaseUtils.manipulateTable(query_wishlist);
        }
    }
}
