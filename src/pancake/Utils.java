package pancake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {
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

    public static int gapHeuristic(List<Integer> input) {
        int gap = 0;
        for (int i = 0; i < input.size() - 1; i++) {
            if (Math.abs(input.get(i) - input.get(i + 1)) != 1) {
                gap++;
            }
        }
        if (input.get(input.size() - 1) != input.size()) {
            gap++;
        }
        return gap;
    }
}
