module com.somerdin.snake {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.somerdin.snake to javafx.fxml;
    exports com.somerdin.snake;
    exports com.somerdin.snake.Point;
    opens com.somerdin.snake.Point to javafx.fxml;
    exports com.somerdin.snake.Resource;
    opens com.somerdin.snake.Resource to javafx.fxml;
}