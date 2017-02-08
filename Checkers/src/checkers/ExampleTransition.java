package checkers;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
        Image black = new Image(getClass().getResourceAsStream("black.png"));
        //SpriteAnimation
        Rectangle rectangle = new Rectangle(500, 300, 100, 100);
        rectangle.setFill(Color.GRAY);
        Canvas canvas = new Canvas(200,200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        for (int i = 1; i < 8; i++) {
            gc.drawImage(black, 20 + 20 * i, 50, 20, 20);
            // ellipse = new Ellipse(20 + 20 * i, 50, 10, 10);
            //ellipse.setFill(Color.BLUE);
           
            canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                   gc.moveTo(event.getX(), event.getY());
                   
                }
            }
            );
            //ellipse.setOnMousePressed(new EventHandler<MouseEvent>() {
            //  @Override
            // public void handle(MouseEvent event) {
            //    src = event.getSource();
            // }
            //});

        }
        root.getChildren().addAll(canvas);
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
}
