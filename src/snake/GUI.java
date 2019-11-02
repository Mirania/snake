package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import snake.Utils.Direction;
import snake.Utils.GameStatus;
import snake.Utils.Point;

public class GUI {

    private Board board; //actual game state
    private JFrame frame;
    private JPanel panel;
    private DrawnCell[][] cells; //visual representation of the game
    private GridBagConstraints cn;
    private int gamewidth = 500;
    private int gameheight = 500;
    private int turnmilliseconds;

    public GUI(int rows, int cols, int framerate) {
        if (framerate<1) throw new IllegalArgumentException("framerate too low");
        board = new Board(rows, cols);
        turnmilliseconds = 1000/framerate;
        frame = new JFrame("Snake");
        frame.setSize(gamewidth+30, gameheight+50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KeyChecker());
        panel = new JPanel(new GridBagLayout());
        panel.setSize(gamewidth, gameheight);
        cells = new DrawnCell[board.rows][board.cols];
        cn = new GridBagConstraints();
        cn.fill = GridBagConstraints.HORIZONTAL;
    }

    /**
     * shows game window and starts gameplay
     */
    public void start() {
        if (board.snakeSize >= board.maxSize) throw new IllegalStateException("snake is not allowed to grow");

        fill();
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
        loop();
    }

    public GUI maxSnakeSize(int size) {
        if (size<1) throw new IllegalArgumentException("snake too short");
        board.maxSize = size;
        return this;
    }

    public GUI startingSnakeSize(int size) {
        if (size<1) throw new IllegalArgumentException("snake too short");
        board.snakeSize = size;
        return this;
    }

    public GUI maxApples(int amt) {
        if (amt<1) throw new IllegalArgumentException("not enough apples");
        board.maxApples = amt;
        return this;
    }

    /**
     * each frame of the gameplay
     */
    private void loop() {
        long last = new Date().getTime();
        long now;
        DrawnCell tail;

        while (true) {
            if (last+turnmilliseconds <= (now = new Date().getTime())) {
                last = now;

                if (board.gameStatus==GameStatus.LOSS) {
                    JOptionPane.showMessageDialog(frame,"Defeat. Final length was "+ board.snakeSize+".");
                    System.exit(0);
                }

                if (board.gameStatus==GameStatus.WIN) {
                    JOptionPane.showMessageDialog(frame,"Victory! Final length was "+ board.snakeSize+".");
                    System.exit(0);
                }

                tail = cell(board.snake[board.snakeSize-1]);
                board.update();
                if (board.ateApple) cell(board.nextApple).apple();
                else tail.empty();
                cell(board.snake[0]).head();
                cell(board.snake[1]).snake();
            }
        }
    }

    /**
     * prepares the GUI
     */
    private void fill() {
        for (int r = 0; r< board.rows; r++) {
            for (int c = 0; c< board.cols; c++) {
                DrawnCell bt = new DrawnCell();
                cn.gridx = c;
                cn.gridy = r;
                cells[r][c] = bt;
                panel.add(bt, cn);
            }
        }

        board.fill();

        cell(board.snake[0]).head();

        for (int i = 1; i< board.snakeSize; i++)
            cell(board.snake[i]).snake();

        for (int i = 0; i< board.maxApples; i++)
            cell(board.initialApples[i]).apple();
    }

    /**
     * set movement for next frame. do not allow a 180º turn
     */
    private class KeyChecker extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_UP:
                    board.nextMove = board.lastMove==Direction.DOWN ? Direction.DOWN : Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    board.nextMove = board.lastMove==Direction.UP ? Direction.UP : Direction.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    board.nextMove = board.lastMove==Direction.RIGHT ? Direction.RIGHT : Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    board.nextMove = board.lastMove==Direction.LEFT ? Direction.LEFT : Direction.RIGHT;
                    break;
            }
        }
    }

    /**
     * convert point to UI cell reference
     */
    private DrawnCell cell(Point p) {
        return cells[p.row][p.col];
    }

    /**
     * each cell of the JPanel
     */
    private class DrawnCell extends JLabel {

        public DrawnCell() {
            super();
            super.setBorder(BorderFactory.createLineBorder(Color.black));
            super.setOpaque(true);
        }

        public void empty() {
            super.setBackground(null);
        }

        public void apple() {
            super.setBackground(new Color(0x45, 0x8B, 0x00));
        }

        public void head() {
            super.setBackground(Color.orange);
        }

        public void snake() {
            super.setBackground(Color.red);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(gamewidth/ board.cols, gameheight/ board.rows);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    }
}
