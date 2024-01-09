module com.literalog.literalog {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.literalog.literalog to javafx.fxml;
    exports com.literalog.literalog;
}