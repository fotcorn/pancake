package pancake;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PanCakeRecursive {

    public static void main(String[] args) {
        int[] input = {2, 1, 3};
        if (!Utils.validateInput(input)) {
            throw new RuntimeException("Invalid input!");
        }

        ArrayList<Integer> solution = startSearch(input);

        System.out.printf("Correct solution found: %s\n", Utils.validateSolution(input, solution));
        System.out.printf("Solution: %s\n", solution);
    }

    public static ArrayList<Integer> startSearch(int[] input) {
        int maxDepth = Utils.gapHeuristic(input);
        ArrayList<Integer> solution;
        while (true) {
            solution = search(input, -1, maxDepth);
            if (solution != null) {
                break;
            }
            maxDepth++;
        }
        return solution;
    }

    public static ArrayList<Integer> search(int[] input, int lastOperation, int maxDepth) {
        for (int currentOperation = 0; currentOperation < input.length - 1; currentOperation++) {
            if (currentOperation != lastOperation) {
                int[] newInput = Arrays.copyOf(input, input.length);
                Utils.flip(newInput, currentOperation);
                if (Utils.isCorrect(newInput)) {
                    return new ArrayList<>(Collections.singletonList(currentOperation));
                } else {
                    if (Utils.gapHeuristic(newInput) < maxDepth) {
                        ArrayList<Integer> solution = search(newInput, currentOperation, maxDepth);
                        if (solution != null) {
                            solution.add(currentOperation);
                        }
                        return solution;
                    } else {
                        return null;
                    }
                }
            }
        }
        throw new RuntimeException("Something is wrong with the algorithm.");
    }
}
