package com.yanfiq.literalog.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;
public class FXMLUtils {
    public static Parent loadFXML(String fxmlFileName) {
        try {
            // Get the URL of the FXML file
            URL fxmlUrl = FXMLUtils.class.getResource("/com/yanfiq/literalog/fxml/" + fxmlFileName);

            if (fxmlUrl != null) {
                // Load the FXML file
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                return loader.load();
            } else {
                System.err.println("FXML file not found: " + fxmlFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}