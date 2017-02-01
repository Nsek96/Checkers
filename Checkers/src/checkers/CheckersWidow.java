package checkers;

import java.util.Vector;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CheckersWidow extends Application {

    private static Scene scene;
    private static Pane root;
    private static Image image;
    public static Canvas canvas;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //extends StackPane
        root = new Pane();
        CheckersCanvas board = new CheckersCanvas();
        image = new Image(getClass().getResourceAsStream("king.jpg"));
        board.newgamebtn.setLayoutX(750);
        board.newgamebtn.setLayoutY(200);
        board.resignbtn.setLayoutX(750);
        board.resignbtn.setLayoutY(230);
        board.message.setLayoutX(200);
        board.message.setLayoutY(500);
        scene = new Scene(root, 900, 600);
        root.getChildren().addAll(board.newgamebtn, board.resignbtn, board.message);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class CheckersCanvas implements EventHandler<MouseEvent> {

        Button newgamebtn = new Button("Новая игра");
        Button resignbtn = new Button("Сдаться");
        Label message = new Label();
        CheckersData board;
        boolean gameInProgress;
        int currentPlayer;
        int col, row;
        int selectedRow, selectedCol;
        CheckersMove[] legalMoves;

        public CheckersCanvas() {

            board = new CheckersData();
            doNewGame();
            canvas = new Canvas(500, 500);
            GraphicsContext gc = canvas.getGraphicsContext2D();
           /* Rectangle rect = new Rectangle(100, 40, 100, 100);
            rect.setArcHeight(50);
            rect.setArcWidth(50);
            rect.setFill(Color.VIOLET);

            TranslateTransition tt = new TranslateTransition(Duration.millis(2000), rect);
            tt.setByX(200f);
            tt.setByY(100);
            tt.setCycleCount((int) 4f);
           // tt.setAutoReverse(true);

            tt.play();*/
            paint(gc);
            root.getChildren().add(canvas);
            canvas.setOnMousePressed(this);
            newgamebtn.setOnMouseClicked(this);
            resignbtn.setOnMouseClicked(this);

        }

        @Override
        public void handle(MouseEvent event) {
            Object src = event.getSource();
            if (src == newgamebtn) {
                doNewGame();
            } else if (src == resignbtn) {
                doResign();
            } else {
                System.out.println(src);
                col = (int) ((event.getSceneX() - 2) / 20);
                row = (int) ((event.getSceneY() - 2) / 20);
                if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                    //  System.out.println(row + "\n"+col);
                    doClickSquare(row, col);
                }
            }
        }

        private void paint(GraphicsContext g) {

            //  g.setFill(Color.BLACK);            
            // g.fillRect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
            // g.fillRect(1, 1, canvas.getWidth() - 3, canvas.getHeight() - 3);            
            /* Нарисуйте квадраты шахматной доски и шашек. */
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) {
                        g.setFill(Color.LIGHTGREY);
                    } else {
                        g.setFill(Color.GRAY);
                    }
                    g.fillRect(2 + col * 20, 2 + row * 20, 20, 20);
                    switch (board.pieceAt(row, col)) {
                        case CheckersData.RED:
                            g.setFill(Color.RED);
                            g.fillOval(4 + col * 20, 4 + row * 20, 16, 16);
                            break;
                        case CheckersData.BLACK:
                            g.setFill(Color.BLACK);
                            g.fillOval(4 + col * 20, 4 + row * 20, 16, 16);
                            break;
                        case CheckersData.RED_KING:
                            g.setFill(Color.RED);
                            g.fillOval(4 + col * 20, 4 + row * 20, 16, 16);
                            g.setFill(Color.WHITE);
                            //g.drawString("K", 7 + col * 20, 16 + row * 20);
                            break;
                        case CheckersData.BLACK_KING:
                            g.setFill(Color.BLACK);
                            g.fillOval(4 + col * 20, 4 + row * 20, 16, 16);
                            g.setFill(Color.WHITE);
                            // g.drawString("K", 7 + col * 20, 16 + row * 20);
                            break;
                    }
                }
            }

            /* Если игра продолжается, HILITE правовые шаги. Обратите внимание, что legalMoves
                       никогда не нуль в то время как игра продолжается. */
            if (gameInProgress) {
                // Во-первых, нарисуйте голубую рамку вокруг частей, которые могут быть перемещены.
                g.setFill(Color.CYAN);
                for (int i = 0; i < legalMoves.length; i++) {
                    g.strokeRect(2 + legalMoves[i].fromCol * 20, 2 + legalMoves[i].fromRow * 20, 19, 19);
                }
                /* Если выбран кусок для перемещения (т.е. если selectedRow> = 0), то
              нарисовать белую рамку 2-х пикселей вокруг этой части и рисовать зеленые границы
              вокруг eacj площади, что эта часть может быть перемещен.*/
                if (selectedRow >= 0) {
                    g.setFill(Color.WHITE);
                    g.strokeRect(2 + selectedCol * 20, 2 + selectedRow * 20, 19, 19);
                    g.strokeRect(3 + selectedCol * 20, 3 + selectedRow * 20, 17, 17);
                    g.setFill(Color.GREEN);
                    for (int i = 0; i < legalMoves.length; i++) {
                        if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow) {
                            g.strokeRect(2 + legalMoves[i].toCol * 20, 2 + legalMoves[i].toRow * 20, 19, 19);
                        }
                    }
                }
            }
        }

        private void doNewGame() {
            if (gameInProgress == true) {
                message.setText("Finish the current game first!");
                return;
            }
            board.setUpGame();
            currentPlayer = CheckersData.RED;
            legalMoves = board.getLegalMoves(CheckersData.RED);
            selectedRow = -1;
            message.setText("Red:  Make your move.");
            gameInProgress = true;
            newgamebtn.setDisable(true);
            resignbtn.setDisable(false);
            //repaint();
        }

        private void doResign() {
            if (gameInProgress == false) {
                message.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == CheckersData.RED) {
                gameOver("RED resigns.  BLACK wins.");
            } else {
                gameOver("BLACK resigns.  RED winds.");
            }
        }

        private void gameOver(String str) {
            message.setText(str);
            newgamebtn.setDisable(false);
            resignbtn.setDisable(true);
            gameInProgress = false;
        }

        private void doClickSquare(int row, int col) {
            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersData.RED) {
                        message.setText("RED:  Make your move.");
                    } else {
                        message.setText("BLACK:  Make your move.");
                    }

                    return;
                }
            }
            if (selectedRow < 0) {
                message.setText("Click the piece you want to move.");
                return;
            }
            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                        && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
                    doMakeMove(legalMoves[i]);
                    return;
                }
            }
            message.setText("Click the square you want to move to.");
        }

        private void doMakeMove(CheckersMove move) {
            board.makeMove(move);

            if (move.isJump()) {
                legalMoves = board.getLegalJumpsFrom(currentPlayer, move.toRow, move.toCol);
                if (legalMoves != null) {
                    if (currentPlayer == CheckersData.RED) {
                        message.setText("RED:  You must continue jumping.");
                    } else {
                        message.setText("BLACK:  You must continue jumping.");
                    }
                    selectedRow = move.toRow;  // Since only one piece can be moved, select it.
                    selectedCol = move.toCol;
                    // repaint();
                    return;
                }
            }

            if (currentPlayer == CheckersData.RED) {
                currentPlayer = CheckersData.BLACK;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null) {
                    gameOver("BLACK has no moves.  RED wins.");
                } else if (legalMoves[0].isJump()) {
                    message.setText("BLACK:  Make your move.  You must jump.");
                } else {
                    message.setText("BLACK:  Make your move.");
                }
            } else {
                currentPlayer = CheckersData.RED;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null) {
                    gameOver("RED has no moves.  BLACK wins.");
                } else if (legalMoves[0].isJump()) {
                    message.setText("RED:  Make your move.  You must jump.");
                } else {
                    message.setText("RED:  Make your move.");
                }
            }
            selectedRow = -1;
            if (legalMoves != null) {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++) {
                    if (legalMoves[i].fromRow != legalMoves[0].fromRow
                            || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                        sameStartSquare = false;
                        break;
                    }
                }
                if (sameStartSquare) {
                    selectedRow = legalMoves[0].fromRow;
                    selectedCol = legalMoves[0].fromCol;
                }
            }

            /* redCircle.setLayoutX(row);
            redCircle.setLayoutY(col);*/
            //  repaint();
        }

    }

    private static class CheckersData {

        public static final int EMPTY = 0,
                RED = 1,
                RED_KING = 2,
                BLACK = 3,
                BLACK_KING = 4;
        private int[][] board;

        public CheckersData() {

            board = new int[8][8];
            setUpGame();
        }

        public void setUpGame() {

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) {
                        if (row < 3) {
                            board[row][col] = BLACK;
                        } else if (row > 4) {
                            board[row][col] = RED;
                        } else {
                            board[row][col] = EMPTY;
                        }
                    } else {
                        board[row][col] = EMPTY;
                    }
                }
            }
        }

        public int pieceAt(int row, int col) {
            return board[row][col];
        }

        public void setPieceAt(int row, int col, int piece) {

            board[row][col] = piece;
        }

        public void makeMove(CheckersMove move) {

            makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }

        public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {

            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = EMPTY;
            if (fromRow - toRow == 2 || fromRow - toRow == -2) {
                int jumpRow = (fromRow + toRow) / 2;
                int jumpCol = (fromCol + toCol) / 2;
                board[jumpRow][jumpCol] = EMPTY;
            }
            if (toRow == 0 && board[toRow][toCol] == RED) {
                board[toRow][toCol] = RED_KING;
            }
            if (toRow == 7 && board[toRow][toCol] == BLACK) {
                board[toRow][toCol] = BLACK_KING;
            }
        }

        public CheckersMove[] getLegalMoves(int player) {

            if (player != RED && player != BLACK) {
                return null;
            }

            int playerKing;
            if (player == RED) {
                playerKing = RED_KING;
            } else {
                playerKing = BLACK_KING;
            }

            Vector moves = new Vector();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2)) {
                            moves.addElement(new CheckersMove(row, col, row + 2, col + 2));
                        }
                        if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2)) {
                            moves.addElement(new CheckersMove(row, col, row - 2, col + 2));
                        }
                        if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2)) {
                            moves.addElement(new CheckersMove(row, col, row + 2, col - 2));
                        }
                        if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2)) {
                            moves.addElement(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }
                }
            }

            if (moves.size() == 0) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (board[row][col] == player || board[row][col] == playerKing) {
                            if (canMove(player, row, col, row + 1, col + 1)) {
                                moves.addElement(new CheckersMove(row, col, row + 1, col + 1));
                            }
                            if (canMove(player, row, col, row - 1, col + 1)) {
                                moves.addElement(new CheckersMove(row, col, row - 1, col + 1));
                            }
                            if (canMove(player, row, col, row + 1, col - 1)) {
                                moves.addElement(new CheckersMove(row, col, row + 1, col - 1));
                            }
                            if (canMove(player, row, col, row - 1, col - 1)) {
                                moves.addElement(new CheckersMove(row, col, row - 1, col - 1));
                            }
                        }
                    }
                }
            }

            if (moves.size() == 0) {
                return null;
            } else {
                CheckersMove[] moveArray = new CheckersMove[moves.size()];
                for (int i = 0; i < moves.size(); i++) {
                    moveArray[i] = (CheckersMove) moves.elementAt(i);
                }
                return moveArray;
            }

        }  // end getLegalMoves

        public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {

            if (player != RED && player != BLACK) {
                return null;
            }
            int playerKing;
            if (player == RED) {
                playerKing = RED_KING;
            } else {
                playerKing = BLACK_KING;
            }
            Vector moves = new Vector();
            if (board[row][col] == player || board[row][col] == playerKing) {
                if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2)) {
                    moves.addElement(new CheckersMove(row, col, row + 2, col + 2));
                }
                if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2)) {
                    moves.addElement(new CheckersMove(row, col, row - 2, col + 2));
                }
                if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2)) {
                    moves.addElement(new CheckersMove(row, col, row + 2, col - 2));
                }
                if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2)) {
                    moves.addElement(new CheckersMove(row, col, row - 2, col - 2));
                }
            }
            if (moves.size() == 0) {
                return null;
            } else {
                CheckersMove[] moveArray = new CheckersMove[moves.size()];
                for (int i = 0; i < moves.size(); i++) {
                    moveArray[i] = (CheckersMove) moves.elementAt(i);
                }
                return moveArray;
            }
        }

        private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {

            if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8) {
                return false;
            }
            if (board[r3][c3] != EMPTY) {
                return false;
            }
            if (player == RED) {
                if (board[r1][c1] == RED && r3 > r1) {
                    return false;
                }
                if (board[r2][c2] != BLACK && board[r2][c2] != BLACK_KING) {
                    return false;
                }
                return true;
            } else {
                if (board[r1][c1] == BLACK && r3 < r1) {
                    return false;
                }
                if (board[r2][c2] != RED && board[r2][c2] != RED_KING) {
                    return false;
                }
                return true;
            }

        }

        private boolean canMove(int player, int r1, int c1, int r2, int c2) {

            if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8) {
                return false;
            }
            if (board[r2][c2] != EMPTY) {
                return false;
            }
            if (player == RED) {
                if (board[r1][c1] == RED && r2 > r1) {
                    return false;
                }
                return true;
            } else {
                if (board[r1][c1] == BLACK && r2 < r1) {
                    return false;
                }
                return true;
            }

        }
    }

    private static class CheckersMove {

        int fromRow, fromCol;  // Position of piece to be moved.
        int toRow, toCol;

        public CheckersMove(int r1, int c1, int r2, int c2) {
            fromRow = r1;
            fromCol = c1;
            toRow = r2;
            toCol = c2;
        }

        boolean isJump() {
            return (fromRow - toRow == 2 || fromRow - toRow == -2);
        }
    }

}
