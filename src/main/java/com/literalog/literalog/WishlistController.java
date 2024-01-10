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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class WishlistController {
    @FXML
    private TextField isbnInput;
    @FXML
    private TextField titleInput;
    @FXML
    private TextField authorInput;
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
                wishlistTable.getItems().remove(book);
            });
            buyButton.setOnAction(event -> {
                //buy action
            });
            return cell;
        });

        if(AccessDB.getConnection() != null){
            try {
                Connection connection = AccessDB.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM [Wishlist]");

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    String ISBN = null, Title = null, Author = null, Publisher = null;
                    int Year = 0, Price = 0;

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object columnValue = resultSet.getObject(i);

                        switch (columnName){
                            case "ISBN": ISBN = columnValue.toString(); break;
                            case "Title": Title = columnValue.toString(); break;
                            case "Author": Author = columnValue.toString(); break;
                            case "Publisher": Publisher = columnValue.toString(); break;
                            case "Year": Year = Integer.parseInt(columnValue.toString()); break;
                            case "Price": Price = Integer.parseInt(columnValue.toString()); break;
                        }
                    }
                    Book book = new Book(ISBN, Title, Author, Publisher, Year, Price);
                    ObservableList<Book> bookList = wishlistTable.getItems();
                    bookList.add(book);
                    wishlistTable.setItems(bookList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void addWishlist(){
        Book book = new Book(isbnInput.getText(), titleInput.getText(), authorInput.getText(), publisherInput.getText(), Integer.parseInt(yearInput.getText()), Integer.parseInt(priceInput.getText()));
        ObservableList<Book> bookList = wishlistTable.getItems();
        bookList.add(book);
        wishlistTable.setItems(bookList);
    }
}
