package snake;

import snake.Utils.Direction;
import snake.Utils.GameStatus;
import snake.Utils.Point;

public class Board {

    // col increases going right
    // row increases going down

    private Cell[][] board;

    public int rows;
    public int cols;
    public GameStatus gameStatus;
    public Direction nextMove;
    public Direction lastMove; //cannot allow a 180º turn
    public boolean ateApple;
    public double winThreshold = 40; //win if snake occupies this % of the board

    public Point[] snake; //head to tail
    public int snakeSize = 2;
    public int maxSize = 100;

    public Point[] initialApples;
    public int maxApples = 4; //only needed to start game
    public Point nextApple; //replacement apple each time one is eaten

    public Board(int rows, int cols) {
        if (rows<1 || cols<1) throw new IllegalArgumentException("negative size");
        this.rows = rows;
        this.cols = cols;
        board = new Cell[rows][cols];
        ateApple = false;
        nextMove = Direction.RIGHT;
        lastMove = Direction.RIGHT;
        gameStatus = GameStatus.PLAYING;
    }

    /**
     * perform a move and update game state
     */
    public void update() {
        Point next = null;

        switch (nextMove) {
            case UP:
                if (snake[0].row==0) next = new Point(rows-1, snake[0].col);
                else next = new Point(snake[0].row-1, snake[0].col);
                lastMove = Direction.UP;
                break;

            case DOWN:
                if (snake[0].row==rows-1) next = new Point(0, snake[0].col);
                else next = new Point(snake[0].row+1, snake[0].col);
                lastMove = Direction.DOWN;
                break;

            case LEFT:
                if (snake[0].col==0) next = new Point(snake[0].row, cols-1);
                else next = new Point(snake[0].row, snake[0].col-1);
                lastMove = Direction.LEFT;
                break;

            case RIGHT:
                if (snake[0].col==cols-1) next = new Point(snake[0].row, 0);
                else next = new Point(snake[0].row, snake[0].col+1);
                lastMove = Direction.RIGHT;
                break;
        }

        ateApple = cell(next).isApple;
        if (ateApple) { nextApple = generateApple(); cell(next).isApple = false; }
        if (cell(next).isSnake && !next.equals(snake[snakeSize-1])) gameStatus = GameStatus.LOSS;
        else if (snakeSize>=winThreshold/100f*rows*cols || snakeSize==maxSize) gameStatus = GameStatus.WIN;
        else updateSnake(next);
    }

    /**
     * convert point to cell reference
     */
    private Cell cell(Point p) {
        return board[p.row][p.col];
    }

    /**
     * prepares the board
     */
    public void fill() {
        snake = new Point[maxSize];
        initialApples = new Point[maxApples+1];

        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                board[r][c] = new Cell();
            }
        }

        snake[0] = new Point(rows/2, cols/2);
        cell(snake[0]).isSnake = true;

        for (int i=1; i<snakeSize; i++) {
            snake[i] = new Point(snake[0].row, snake[0].col - i);
            cell(snake[i]).isSnake = true;
        }


        for (int i=0; i<maxApples; i++) {
            Point p = generateApple();
            cell(p).isApple = true;
            initialApples[i] = p;
        }
    }

    /**
     * finds a spot for a new apple and places it there
     */
    public Point generateApple() {
        int r, c;
        do {
            r = Utils.random(0, rows-1);
            c = Utils.random(0, rows-1);
        } while (board[r][c].isSnake || board[r][c].isApple);

        Point p = new Point(r,c);
        cell(p).isApple = true;
        return p;
    }

    /**
     * updates body, tail and growth of snake
     */
    private void updateSnake(Point newHead) {
        if (ateApple && snakeSize<maxSize) snakeSize++;
        else cell(snake[snakeSize-1]).isSnake = false;

        for (int i=snakeSize-1; i>0; i--) {
            snake[i] = snake[i-1];
        }
        snake[0] = newHead;
        cell(snake[0]).isSnake = true;
    }

    /**
     * game state cell
     */
    private class Cell {
        public boolean isSnake = false;
        public boolean isApple = false;
    }

}
