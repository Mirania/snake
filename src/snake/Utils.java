package snake;

import java.util.Random;

public class Utils {

    /**
     * max and min both inclusive
     */
    public static int random(int min, int max) {
        if (min >= max) throw new IllegalArgumentException("max must be greater than min");
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    public enum GameStatus {
        WIN, LOSS, PLAYING;
    }

    public static class Point {
        public int row;
        public int col;

        public Point(int r, int c) {
            row = r;
            col = c;
        }

        public boolean equals(Point p) {
            return p.row==row && p.col==col;
        }

        @Override
        public String toString() {
            return "(row:"+row+", col:"+col+")";
        }
    }
}
