package checkers;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        Label label=new Label("V 0.1");
        label.setLayoutX(50);
        label.setLayoutY(550);
        root.getChildren().addAll(img,label);

        MenuItem newGame = new MenuItem("НОВАЯ ИГРА");
        MenuItem exitGame = new MenuItem("Выйти из игры");
        SubMenu mainMenu = new SubMenu(
                newGame, exitGame
        );
        MenuItem NG1 = new MenuItem("ИГРОК VS ИГРОК");
        MenuItem NG2 = new MenuItem("ИГРОК VS КОМПЬЮТЕР");
        MenuItem NG3 = new MenuItem("НАЗАД");
        SubMenu newGameMenu = new SubMenu(
                NG1, NG2, NG3
        );
        MenuBox menuBox = new MenuBox(mainMenu);

        newGame.setOnMouseClicked(event -> menuBox.setSubMenu(newGameMenu));
        exitGame.setOnMouseClicked(event -> System.exit(0));
        NG3.setOnMouseClicked(event -> menuBox.setSubMenu(mainMenu));
        root.getChildren().addAll(menuBox);
        Scene scene = new Scene(root, 900, 600);
        FadeTransition ft = new FadeTransition(Duration.seconds(1), menuBox);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        menuBox.setVisible(true);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static class MenuItem extends StackPane {

        public MenuItem(String name) {
            Rectangle bg = new Rectangle(200, 20, Color.WHITE);
            bg.setOpacity(0.5);

            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg, text);
            FillTransition st = new FillTransition(Duration.seconds(0.5), bg);
            setOnMouseEntered(event -> {
                st.setFromValue(Color.DARKGRAY);
                st.setToValue(Color.DARKGOLDENROD);
                st.setCycleCount(Animation.INDEFINITE);
                st.setAutoReverse(true);
                st.play();
            });
            setOnMouseExited(event -> {
                st.stop();
                bg.setFill(Color.WHITE);
            });
        }
    }

    private static class MenuBox extends Pane {

        static SubMenu subMenu;

        public MenuBox(SubMenu subMenu) {
            MenuBox.subMenu = subMenu;

            setVisible(false);
            Rectangle bg = new Rectangle(900, 600, Color.LIGHTBLUE);
            bg.setOpacity(0.4);
            getChildren().addAll(bg, subMenu);
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
