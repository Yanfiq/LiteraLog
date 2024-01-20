module com.yanfiq.literalog {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.yanfiq.literalog to javafx.fxml;
    exports com.yanfiq.literalog;
    exports com.yanfiq.literalog.utils;
    opens com.yanfiq.literalog.utils to javafx.fxml;
    exports com.yanfiq.literalog.models;
    opens com.yanfiq.literalog.models to javafx.fxml;
    exports com.yanfiq.literalog.controllers;
    opens com.yanfiq.literalog.controllers to javafx.fxml;
}