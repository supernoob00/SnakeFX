module com.somerdin.snake {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.somerdin.snake to javafx.fxml;
    exports com.somerdin.snake;
}