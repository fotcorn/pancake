package pancake;

import java.util.ArrayList;
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

    public static boolean validateSolution(List<Integer> originalInput, List<Integer> solution) {
        ArrayList<Integer> input = new ArrayList<>(originalInput);

        for (int action : solution) {
            flip(input, action);
        }
        for (int i = 0; i < input.size(); i ++) {
            if (input.get(i) != i + 1) {
                return false;
            }
        }
        return true;
    }

    public static void flip(List<Integer> input, int position) {
        int to = position + 2;
        for(int i = 0; i < to / 2; i++) {
            int temp = input.get(i);
            input.set(i, input.get(to - i - 1));
            input.set(to - i - 1, temp);
        }
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
