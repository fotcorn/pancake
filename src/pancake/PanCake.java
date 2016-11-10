package pancake;


public class PanCake {

    public static void main(String[] args) {
        int[] input = {2, 1, 3};
        if (!Utils.validateInput(input)) {
            throw new RuntimeException("Invalid input!");
        }
    }
}
