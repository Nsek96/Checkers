package checkers;

import java.util.Vector;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CheckersWidow extends Application {

    private static Scene scene;
    private static Pane root;
    private static Image whiteking;
    private static Image blackking;
    private static Image white;
    private static Image black;
    private static Image blackboard;
    private static Image whiteboard;
    private static Image border;
    private static Image fon; 
    public static ImageView img;

   public CheckersWidow(Stage stage) throws Exception {
        start(stage);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        root = new Pane();  
        whiteking = new Image(getClass().getResourceAsStream("whiteking.png"));
        blackking = new Image(getClass().getResourceAsStream("blackking.png"));
        white = new Image(getClass().getResourceAsStream("white.png"));
        black = new Image(getClass().getResourceAsStream("black.png"));
        blackboard = new Image(getClass().getResourceAsStream("blackboard.png"));
        whiteboard = new Image(getClass().getResourceAsStream("whiteboard.png"));
        border = new Image(getClass().getResourceAsStream("border.png"));
        fon=new Image(getClass().getResourceAsStream("fon.png"));
        img = new ImageView(fon);
        img.setFitWidth(900);
        img.setFitHeight(600);

        CheckersCanvas board = new CheckersCanvas();
        board.newgamebtn.setLayoutX(750);
        board.newgamebtn.setLayoutY(200);
        board.resignbtn.setLayoutX(750);
        board.resignbtn.setLayoutY(230);
        board.message.setLayoutX(200);
        board.message.setLayoutY(550);
        scene = new Scene(root, 900, 600);
        root.getChildren().addAll(board.newgamebtn, board.resignbtn, board.message);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
   

    

    private static class CheckersCanvas implements EventHandler<MouseEvent> {

        Button newgamebtn = new Button("Новая игра");
        Button resignbtn = new Button("Сдаться");
        Label message = new Label();
        static CheckersData board;
        private static Canvas canvas;
        static boolean gameInProgress;
        int currentPlayer;
        int col, row;
        private static GraphicsContext gc;
        static int selectedRow, selectedCol;
        static CheckersMove[] legalMoves;

        public CheckersCanvas() {
            newgamebtn.setOnMouseClicked(this);
            resignbtn.setOnMouseClicked(this);
            board = new CheckersData();
            doNewGame();
            canvas = new Canvas(600, 600);
            canvas.setLayoutX(200);
            canvas.setLayoutY(100);
            gc = canvas.getGraphicsContext2D();
            paint(gc);
            canvas.setOnMousePressed(this);
            root.getChildren().addAll(img,canvas);
            
        }

        @Override
        public void handle(MouseEvent t) {
            Object src = t.getSource();
            if (src == newgamebtn) {
                doNewGame();
                repaintContext(gc);
            } else if (src == resignbtn) {
                doResign();
            } else if (gameInProgress == false) {
                message.setText("Click \"New Game\" to start a new game.");
            } else {
                int col = (int) ((t.getX() - 2) / 50);
                int row = (int) ((t.getY() - 2) / 50);
                if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                    doClickSquare(row, col);
                }
            }
        }

        private void paint(GraphicsContext g) {           
            g.drawImage(border, 0, 0, 430, 430);
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) {
                        g.drawImage(whiteboard, 16 + col * 50, 15 + row * 50, 50, 50);
                    } else {
                        g.drawImage(blackboard, 16 + col * 50, 15 + row * 50, 50, 50);
                    }
                    switch (board.pieceAt(row, col)) {
                        case CheckersData.RED:
                            g.drawImage(white, 18 + col * 50, 18 + row * 50, 46, 46);
                            break;
                        case CheckersData.BLACK:
                            g.drawImage(black, 18 + col * 50, 18 + row * 50, 46, 46);
                            break;
                        case CheckersData.RED_KING:
                            g.drawImage(whiteking, 18 + col * 50, 17 + row * 50, 46, 46);
                            break;
                        case CheckersData.BLACK_KING:
                            g.drawImage(blackking, 18 + col * 50, 17 + row * 50, 46, 46);
                            break;
                    }
                }
            }
            if (gameInProgress) {
                g.setStroke(Color.CYAN);
                for (int i = 0; i < legalMoves.length; i++) {
                    g.strokeRect(16 + legalMoves[i].fromCol * 50, 16 + legalMoves[i].fromRow * 50, 49, 49);
                    g.setFill(Color.RED);
                }
                /* Если выбран кусок для перемещения (т.е. если selectedRow> = 0), то
              нарисовать белую рамку 2-х пикселей вокруг этой части и рисовать зеленые границы
              вокруг eacj площади, что эта часть может быть перемещен.*/
                if (selectedRow >= 0) {
                    g.setStroke(Color.WHITE);
                    g.strokeRect(16 + selectedCol * 50, 16 + selectedRow * 50, 50, 50);
                    g.strokeRect(17 + selectedCol * 50, 17 + selectedRow * 50, 47, 47);
                    g.setStroke(Color.web("#DAA520"));
                    for (int i = 0; i < legalMoves.length; i++) {
                        if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow) {
                            g.strokeOval(16 + legalMoves[i].toCol * 50, 16 + legalMoves[i].toRow * 50, 49, 49);

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
            currentPlayer = CheckersData.RED;//RED перемещается в первую очередь.
            legalMoves = board.getLegalMoves(CheckersData.RED); // Получите возможные шаги Красных.
            selectedRow = -1;  // RED еще не выбрал кусок двигаться.
            message.setText("Red:  Make your move.");
            gameInProgress = true;
            newgamebtn.setDisable(true);
            resignbtn.setDisable(false);
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
            /* Это вызывается mousePressed (), когда игрок нажимает на
          квадрат в указанной строке и цв. Уже было проверено
          что игра, на самом деле, в процессе.*/
            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersData.RED) {
                        message.setText("RED:  Make your move.");
                    } else {
                        message.setText("BLACK:  Make your move.");
                    }
                    repaintContext(gc);
                    return;
                }
            }
            /* Если ни одна часть не была выбрана, чтобы быть перемещены, пользователь должен сначала
          выберите кусок. Показать сообщение об ошибке и возврата. */
            if (selectedRow < 0) {
                message.setText("Click the piece you want to move.");
                return;
            }
            /* Если пользователь нажал на площади, где выбранный кусок может быть перемещен на законных основаниях, а затем сделать шаг и вернуться. */
            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                        && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
                    doMakeMove(legalMoves[i]);
                    return;
                }
            }
            /*показать ошибку если пользователь не нажёл на нужный год */
            message.setText("Click the square you want to move to.");
        }

        private void doMakeMove(CheckersMove move) {
            /*   текущий игрок выбрал указанный шаг. Сделайте этот шаг*/
            board.makeMove(move);
            /* Если этот шаг был прыжок, вполне возможно, что у игрока есть еще один
          Прыжок. Проверьте наличие правовых прыжков, начиная с площади, что игрок
          только что переехал. Если есть какие-либо, игрок должен прыгать. Такой же
          игрок продолжает двигаться.
             */
            if (move.isJump()) {
                legalMoves = board.getLegalJumpsFrom(currentPlayer, move.toRow, move.toCol);
                if (legalMoves != null) {
                    if (currentPlayer == CheckersData.RED) {
                        message.setText("RED:  You must continue jumping.");
                    } else {
                        message.setText("BLACK:  You must continue jumping.");
                    }
                    selectedRow = move.toRow;  // Так как только одна часть может быть перемещен, выберите его.
                    selectedCol = move.toCol;
                    repaintContext(gc);
                    return;
                }
            }
            /* Очередь текущего игрока закончился, так что перейти к другому игроку.
          Получить юридические шаги этого игрока. Если игрок не имеет никаких юридических шагов,
          то игра заканчивается. */

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
            /* Установите selectedRow = -1, чтобы записать, что игрок еще не выбран
           кусок, чтобы переместить. */
            selectedRow = -1;
            /* В качестве вежливости по отношению к пользователю, если все юридические ходы используют один и тот же кусок, а затем
          выбрать ту часть автоматически, поэтому использование не нужно будет нажать на него
          чтобы выбрать его. */
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

            //TranslateTransition tt = new TranslateTransition(Duration.millis(2000), gc.getCanvas());
            // tt.setToX(selectedRow);
            // tt.setToY(selectedCol);
            // tt.play();
            repaintContext(gc);

        }

        private void repaintContext(GraphicsContext g) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            paint(g);
        }

    }

    private static class CheckersData {

        /*  Объект этого класса содержит данные об игре в шашки.
      Он знает, какая часть находится на каждом Sqaure шахматной доски.
      Обратите внимание, что красные ходы "вверх" на борту (т.е. номер строки уменьшается)
     а черные ходы "вниз" на борту (номер строки увеличивается).
     Методы предоставляются для возврата списки доступных юридических ходов.*/
 /*  Следующие константы представляют возможное содержание квадрата
        на борту. Константы красный и черный также представляют игроков
        в игре.
         */
        public static final int EMPTY = 0,
                RED = 1,
                RED_KING = 2,
                BLACK = 3,
                BLACK_KING = 4;
        private int[][] board;//доска [r] [c] содержимое строки г, столбец с.

        public CheckersData() {
            // Конструктор. Создать доску и установить его для новой игры.
            board = new int[8][8];
            setUpGame();
        }

        public void setUpGame() {
            /*  Настройка платы с шашками в положении для начала
          из игры. Обратите внимание, что шашки могут быть найдены только на площадях
          которые удовлетворяют row% 2 == Col% 2. В начале игры,
         все такие квадраты в первых трех строках содержат черные квадраты
         и все такие квадраты в последних трех строках содержат красные квадраты.*/
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
            //Возвращает содержимое квадрата в указанной строки и столбца.  
            return board[row][col];
        }

        public void setPieceAt(int row, int col, int piece) {
            /* Установить содержимое квадрата в указанной строки и столбца.
         часть должна быть одной из констант EMPTY, красный, черный, RED_KING,
          BLACK_KING.*/
            board[row][col] = piece;

        }

        public void makeMove(CheckersMove move) {
            /* Сделать указанный шаг. Предполагается, что движение
          не равен нулю, и что этот шаг представляет законно.*/
            makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }

        public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
            /*  Сделайте переход от (fromRow, fromCol) к (toRow, токола). это
          Предполагается, что этот шаг является законным. Если этот шаг является прыжок, то
          прыгнули часть удаляется с доски. Если кусок движется
          последняя строка на стороне соперника, доски,
          шт становится королем.*/
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = EMPTY;
            if (fromRow - toRow == 2 || fromRow - toRow == -2) {
                // Этот шаг является прыжок. Удалите прыгнули кусок от доски.
                int jumpRow = (fromRow + toRow) / 2;//Ряд Вскочившую части.
                int jumpCol = (fromCol + toCol) / 2; // Колонна выпрыгнул кусок.
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
            /* Возвращает массив, содержащий все юридические CheckersMoves
          для игрока на, определенное текущей плате. Если игроку
          не имеет никаких юридических шагов, нуль возвращается. Значение игрока
          должен быть одним из постоянных красного или черного цвета; если нет, то нуль
          возвращается. Если возвращаемое значение не равно нулю, он состоит
          полностью скачкообразных движений или полностью регулярных движений, так как
         если игрок может прыгать, только прыжки юридические шаги.*/
            if (player != RED && player != BLACK) {
                return null;
            }

            int playerKing;//Константа, представляющая король, принадлежащий игроку.
            if (player == RED) {
                playerKing = RED_KING;
            } else {
                playerKing = BLACK_KING;
            }

            Vector moves = new Vector();// Ходы будут храниться в этом векторе.

            /*  Во-первых, проверьте наличие любых возможных скачков. Посмотрите на каждый квадрат на доске.
           Если эта клетка содержит одну из частей игрока, посмотреть на возможный
           прыгать в каждом из четырех направлений от этой площади. Если там есть
           правовой скачок в этом направлении, поместите его в вектор движется.
             */
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
            /*  Если были обнаружены какие-либо шаги прыжок, то пользователь должен прыгать, поэтому мы не делаем
           добавить любые регулярные движения. Тем не менее, если не было найдено никаких скачков, проверить
           любые юридические шаги regualar. Посмотрите на каждый квадрат на доске.
           Если эта клетка содержит одну из частей игрока, посмотреть на возможный
           двигаться в каждом из четырех направлений от этой площади. Если там есть
           юридическое движение в этом направлении, поместите его в вектор движется.
             */
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
            /* Если никакие правовые шаги не были найдены, возвращают нуль. В противном случае, создать
          массив достаточно велик, чтобы вместить все юридические шаги, скопируйте
          юридические перемещается из вектора в массив, и возвращает массив. */
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
            /* Возвращает список юридических прыжков, что указанный игрок может
         сделать начиная с указанной строки и столбца. Если нет такого
         скачки возможны, нуль возвращается. Логика аналогична
          к логике getLegalMoves () метод.*/
            if (player != RED && player != BLACK) {
                return null;
            }
            int playerKing;// Константа, представляющая король, принадлежащий игроку.
            if (player == RED) {
                playerKing = RED_KING;
            } else {
                playerKing = BLACK_KING;
            }
            Vector moves = new Vector(); // Правовые скачки будут храниться в этом векторе.
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
            /*  Это называется двумя предыдущими методами, чтобы проверить, является ли
          игрок может легально перейти от (R1, c1) до (r3, C3). Предполагается
         что игрок имеет часть на (r1, c1), что (г3, с3) является позиция
          то есть 2 строки и 2 колонки далеких от (R1, c1) и что
          (R 2, с2) есть квадрат между (r1, c1) и (r3, c3).*/
            if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8) {
                return false; // (R3, c3) находится с доски.
            }
            if (board[r3][c3] != EMPTY) {
                return false;// (R3, c3) уже содержит кусок.
            }
            if (player == RED) {
                if (board[r1][c1] == RED && r3 > r1) {
                    return false; // Регулярное красный кусок может двигаться только вверх.
                }
                if (board[r2][c2] != BLACK && board[r2][c2] != BLACK_KING) {
                    return false; // Там нет черного кусок, чтобы прыгать.
                }
                return true;// Скачок является законным.
            } else {
                if (board[r1][c1] == BLACK && r3 < r1) {
                    return false;//Регулярное черная часть может двигаться только вниз.
                }
                if (board[r2][c2] != RED && board[r2][c2] != RED_KING) {
                    return false;// Там нет красный кусок, чтобы прыгать.
                }
                return true;//Скачок является законным.
            }

        }

        private boolean canMove(int player, int r1, int c1, int r2, int c2) {
            /*   Это называется по getLegalMoves () метод, чтобы определить, является ли
         игрок может легально перейти от (r1, c1) по (r2, с2). это
         Предполагается, что (r1, r2) содержит один из фигуры игрока и
          что (г2, с2) является соседняя квадрат.*/
            if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8) {
                return false; // (R2, c2) находится с доски.
            }
            if (board[r2][c2] != EMPTY) {
                return false;//(R2, c2) уже содержит кусок.
            }
            if (player == RED) {
                if (board[r1][c1] == RED && r2 > r1) {
                    return false;// Регулярное красный кусок может двигаться только вниз.
                }
                return true; // Этот шаг является законным.
            } else {
                if (board[r1][c1] == BLACK && r2 < r1) {
                    return false; //Регулярное черный кусок может двигаться только вверх.
                }
                return true;// Этот шаг является законным.
            }

        }
    }

    private static class CheckersMove {
        /*  Объект CheckersMove представляет собой шаг в игре в шашки.
      Он содержит строку и столбец куска, который должен быть перемещен
      и строка и столбец квадрата, к которому он должен быть перемещен.
      (Этот класс не дает никаких гарантий, что этот шаг является законным.)*/
        
        int fromRow, fromCol;  // Положение части для перемещения.
        int toRow, toCol;      // Площадь она должна перейти.

        public CheckersMove(int r1, int c1, int r2, int c2) {
            // Конструктор. Просто установите значения переменных экземпляра.
            fromRow = r1;
            fromCol = c1;
            toRow = r2;
            toCol = c2;
        }

        boolean isJump() {
            /*  Проверьте этот шаг является ли скачок. Предполагается, что
         этот шаг является законным. В прыжке, кусок движется два
         строк. (В обычном движении, он движется только по одной строке.)*/
            return (fromRow - toRow == 2 || fromRow - toRow == -2);
        }
    }
}
