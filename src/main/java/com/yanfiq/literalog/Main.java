package com.yanfiq.literalog;

import com.yanfiq.literalog.utils.DatabaseUtils;
import com.yanfiq.literalog.utils.FXMLUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(FXMLUtils.loadFXML("Main.fxml"), 1280, 720);
        stage.setTitle("LiteraLog");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DatabaseUtils.closeConnection();
    }
}