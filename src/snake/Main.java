package snake;

public class Main {

    public static void main(String[] args) {
        new GUI(20, 20, 20)
                .startingSnakeSize(4)
                .maxApples(6)
                .maxSnakeSize(100).start();
    }
}
