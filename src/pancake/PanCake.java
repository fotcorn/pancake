package pancake;

import java.util.Arrays;
import java.util.List;

public class PanCake {

    public static void main(String[] args) {
        List<Integer> input = Arrays.asList(2, 1, 3);
        if (!Utils.validateInput(input)) {
            throw new RuntimeException("Invalid input!");
        }
    }
}
