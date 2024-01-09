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
    }
    @FXML
    private void addWishlist(){
        Book book = new Book(isbnInput.getText(), titleInput.getText(), authorInput.getText(), publisherInput.getText(), Integer.parseInt(yearInput.getText()), Integer.parseInt(priceInput.getText()));
        ObservableList<Book> bookList = wishlistTable.getItems();
        bookList.add(book);
        wishlistTable.setItems(bookList);
    }
}
