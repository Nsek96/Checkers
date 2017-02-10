package checkers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Checkers extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        Pane root = new Pane();       
        
        Image image = new Image(getClass().getResourceAsStream("menu.jpg"));
        ImageView img = new ImageView(image);
        img.setFitWidth(900);
        img.setFitHeight(600);
        Label label = new Label("V 0.1");
        label.setLayoutX(50);
        label.setLayoutY(550);
        //root.getChildren().addAll(img,label);

        MenuItem newGame = new MenuItem("НОВАЯ ИГРА");
        MenuItem exitGame = new MenuItem("Выйти из игры");
        final SubMenu mainMenu = new SubMenu(
                newGame, exitGame
        );
        MenuItem playersitem = new MenuItem("2 ИГРОКА");
        //MenuItem botsitem = new MenuItem("ИГРОК VS КОМПЬЮТЕР");
        MenuItem backitem = new MenuItem("НАЗАД");
        final SubMenu newGameMenu = new SubMenu(
                playersitem, /*botsitem,*/ backitem
        );
        final MenuBox menuBox = new MenuBox(mainMenu);
        newGame.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                menuBox.setSubMenu(newGameMenu);
            }
        });
        playersitem.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
               primaryStage.close();
                Stage stage=new Stage();               
                try {
                    new CheckersWidow(stage);
                } catch (Exception ex) {
                    Logger.getLogger(Checkers.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        //  newGame.setOnMouseClicked(event -> menuBox.setSubMenu(newGameMenu));
        exitGame.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                System.exit(0);
            }
        });
        // exitGame.setOnMouseClicked(event -> System.exit(0));
        backitem.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                menuBox.setSubMenu(mainMenu);
            }
        });
        //backitem.setOnMouseClicked(event -> menuBox.setSubMenu(mainMenu));
        root.getChildren().addAll(img, menuBox, label);
        Scene scene = new Scene(root);
        /* FadeTransition ft = new FadeTransition(Duration.seconds(1), menuBox);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();*/
        menuBox.setVisible(true);
        primaryStage.setTitle("Checkers");
        //primaryStage.setMaximized(true);
       
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static class MenuItem extends StackPane {

        public MenuItem(String name) {
            final Rectangle bg = new Rectangle(200, 20, Color.WHITE);
            bg.setOpacity(0.5);

            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg, text);
            final FillTransition st = new FillTransition(Duration.seconds(0.5), bg);
            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    st.setFromValue(Color.DARKGRAY);
                    st.setToValue(Color.DARKGOLDENROD);
                    st.setCycleCount(Animation.INDEFINITE);
                    st.setAutoReverse(true);
                    st.play();
                }
            });

            /*setOnMouseEntered(event -> {
                st.setFromValue(Color.DARKGRAY);
                st.setToValue(Color.DARKGOLDENROD);
                st.setCycleCount(Animation.INDEFINITE);
                st.setAutoReverse(true);
                st.play();
            });*/
            setOnMouseExited(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    st.stop();
                    bg.setFill(Color.WHITE);
                }
            });
            /*setOnMouseExited(event -> {
                st.stop();
                bg.setFill(Color.WHITE);
            });*/
        }
    }

    private static class MenuBox extends Pane {

        static SubMenu subMenu;

        public MenuBox(SubMenu subMenu) {
            MenuBox.subMenu = subMenu;

            setVisible(false);
            // Rectangle bg = new Rectangle(900, 600, Color.LIGHTBLUE);
            // bg.setOpacity(0.4);
            getChildren().addAll(/*bg,*/subMenu);
        }

        public void setSubMenu(SubMenu subMenu) {
            getChildren().remove(MenuBox.subMenu);
            MenuBox.subMenu = subMenu;
            getChildren().add(MenuBox.subMenu);
        }
    }

    private static class SubMenu extends VBox {

        public SubMenu(MenuItem... items) {
            setSpacing(15);
            setTranslateY(100);
            setTranslateX(50);
            for (MenuItem item : items) {
                getChildren().addAll(item);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
