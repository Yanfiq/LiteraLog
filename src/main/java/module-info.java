module com.literalog.literalog {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.literalog.literalog to javafx.fxml;
    exports com.literalog.literalog;
}