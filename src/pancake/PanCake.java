package pancake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PanCake {
    public static void main(String[] args) {
        List<Integer> input = Arrays.asList(2, 4, 1, 3);
        System.out.printf("Is input valid: %s", PanCake.validateInput(input));
    }

    public static boolean validateInput(List<Integer> input) {
        ArrayList<Integer> sortedInput = new ArrayList<>(input);
        Collections.sort(sortedInput);

        for (int i = 0; i < sortedInput.size(); i++) {
            if (sortedInput.get(i) != i + 1) {
                return false;
            }
        }
        return true;
    }
}
