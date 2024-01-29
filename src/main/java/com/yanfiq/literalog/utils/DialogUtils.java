package com.yanfiq.literalog.utils;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class DialogUtils {
    private DialogUtils(){}

    public static void showAlert(Alert.AlertType type, String textContent, String textHeader){
        Alert alert = new Alert(type, textContent);
        alert.setTitle(textHeader);
        alert.setHeaderText(textContent);
        //alert.setContentText("You can customize the appearance using CSS.");

        // Apply the custom CSS
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("com/yanfiq/literalog/css/dialog-dark.css");

        // Show the alert
        alert.showAndWait();
    }

    public static void showException(Exception ex){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText(null);
        alert.setContentText(ex.getMessage());

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Segoe UI Semibold';");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Segoe UI Semibold';");

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        expContent.setStyle("-fx-font-family: 'Segoe UI Semibold';");

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
