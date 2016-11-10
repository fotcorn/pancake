package pancake;

import java.util.Arrays;

public class Utils {
    public static boolean validateInput(int[] input) {
        int[] sortedInput = Arrays.copyOf(input, input.length);
        Arrays.sort(sortedInput);
        for (int i = 0; i < sortedInput.length; i++) {
            if (sortedInput[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateSolution(int[] originalInput, int[] solution) {
        int[] input = Arrays.copyOf(originalInput, originalInput.length);

        for (int action : solution) {
            flip(input, action);
        }
        for (int i = 0; i < input.length; i ++) {
            if (input[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    public static void flip(int[] input, int position) {
        int to = position + 2;
        for(int i = 0; i < to / 2; i++) {
            int temp = input[i];
            input[i] = input[to - i - 1];
            input[to - i - 1] = temp;
        }
    }

    public static int gapHeuristic(int[] input) {
        int gap = 0;
        for (int i = 0; i < input.length - 1; i++) {
            if (Math.abs(input[i] - input[i + 1]) != 1) {
                gap++;
            }
        }
        if (input[input.length - 1] != input.length) {
            gap++;
        }
        return gap;
    }
}
