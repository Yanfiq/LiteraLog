package com.literalog.literalog;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.security.auth.callback.Callback;
import java.sql.*;
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
        isbnColumn.setCellValueFactory(cellData -> cellData.getValue().isbn);
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().author);
        totalPageColumn.setCellValueFactory(celldata -> new SimpleIntegerProperty(celldata.getValue().totalPage.get()).asObject());
        publisherColumn.setCellValueFactory(cellData -> cellData.getValue().publisher);
        yearColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().year.get()).asObject());
        priceColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().price.get()).asObject());

        actionColumn.setCellFactory(param -> {
            final Button removeButton = new Button("Remove");
            final Button buyButton = new Button("Buy");
            final GridPane actionbutton = new GridPane();
            actionbutton.addColumn(0, buyButton);
            actionbutton.addColumn(1, removeButton);

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

                AccessDB.manipulateTable("DELETE FROM [WISHLIST] WHERE [ISBN] = "+isbn);
                AccessDB.manipulateTable("DELETE FROM [BOOKS] WHERE [ISBN] = "+isbn);
            });
            buyButton.setOnAction(event -> {
                Book book = wishlistTable.getItems().get(cell.getIndex());
                String isbn = book.isbn.get();
                wishlistTable.getItems().remove(book);
                AccessDB.manipulateTable("DELETE FROM [WISHLIST] WHERE [ISBN] = "+isbn);
            });
            return cell;
        });

        //get data from database
        ArrayList<Book> container = AccessDB.getData("SELECT * FROM [BOOKS] WHERE [ISBN] IN (SELECT [ISBN] FROM [WISHLIST])");
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

        if(AccessDB.getConnection() != null){
            String query_book = "INSERT INTO [BOOKS] VALUES "+String.format("('%s', '%s' ,'%s' ,%d ,'%s' ,%d ,%d)", isbn, title, author, totalPage, publisher, year, price);
            String query_wishlist = "INSERT INTO [WISHLIST] VALUES "+isbn;
            try {
                Connection connection = AccessDB.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query_book);
                preparedStatement.executeUpdate();
                preparedStatement = connection.prepareStatement(query_wishlist);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
