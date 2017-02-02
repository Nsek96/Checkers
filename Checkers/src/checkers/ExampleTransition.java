package checkers;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ExampleTransition extends Application {

    private Ellipse ellipse;
     public Object src;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        Rectangle rectangle = new Rectangle(500, 300, 100, 100);
        rectangle.setFill(Color.GRAY);
        // rectangle.setOnMousePressed(this); 
        for (int i = 1; i < 8; i++) {
            ellipse = new Ellipse(20 + 20 * i, 50, 10, 10);
            ellipse.setFill(Color.BLUE);
            ellipse.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    src = event.getSource();
                }
               
            });
            root.getChildren().addAll(ellipse);
        }
        rectangle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(2000), (Node) src);
                tt.setToX(event.getSceneX());
                tt.setToY(event.getSceneY());
                tt.play();
            }
        });
        root.getChildren().add(rectangle);
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /* @Override
    public void handle(MouseEvent t) {

        if (t.getSource().equals(ellipse)) {
            System.out.println(t.getSource());
        } else {
            TranslateTransition tt = new TranslateTransition(Duration.millis(2000), ellipse);            
            tt.setToX(t.getSceneX());
            tt.setToY(t.getSceneY());
            tt.play();
        }
    }*/
}
