package checkers;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ExampleTransition extends Application implements EventHandler<MouseEvent> {

    private Ellipse ellipse;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        Rectangle rectangle = new Rectangle(500, 300, 100, 100);
        rectangle.setFill(Color.GRAY);
        rectangle.setOnMousePressed(this);
        ellipse = new Ellipse(50, 50, 50, 50);
        ellipse.setFill(Color.BLUE);
        ellipse.setOnMousePressed(this);
        root.getChildren().addAll(ellipse, rectangle);
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void handle(MouseEvent t) {

        if (t.getSource().equals(ellipse)) {
            System.out.println(t.getSource());
        } else {
            TranslateTransition tt = new TranslateTransition(Duration.millis(2000), ellipse);            
            tt.setToX(t.getSceneX());
            tt.setToY(t.getSceneY());
            tt.play();
        }
    }
}
