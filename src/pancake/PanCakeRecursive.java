package pancake;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PanCakeRecursive {

    public static void main(String[] args) {
        int size = 4;

        ArrayList<Integer> listInput = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            listInput.add(i + 1);
        }

        Collections.shuffle(listInput);

        int[] input = new int[listInput.size()];
        for(int i = 0; i < listInput.size(); i++) {
            input[i] = listInput.get(i);
        }

        if (!Utils.validateInput(input)) {
            throw new RuntimeException("Invalid input!");
        }

        System.out.printf("Searching for solution with input: %s\n", listInput);

        ArrayList<Integer> solution = startSearch(input);

        System.out.printf("Correct solution found: %s\n", Utils.validateSolution(input, solution));
        System.out.printf("Solution: %s\n", solution);
    }

    public static ArrayList<Integer> startSearch(int[] input) {
        int maxDepth = Utils.gapHeuristic(input);
        ArrayList<Integer> solution;
        while (true) {
            solution = search(input, -1, 0, maxDepth);
            if (solution != null) {
                break;
            }
            maxDepth++;
        }
        Collections.reverse(solution);
        return solution;
    }

    public static ArrayList<Integer> search(int[] input, int lastOperation, int currentDepth, int maxDepth) {
        for (int currentOperation = 0; currentOperation < input.length - 1; currentOperation++) {
            if (currentOperation != lastOperation) {
                int[] newInput = Arrays.copyOf(input, input.length);
                Utils.flip(newInput, currentOperation);
                if (Utils.isCorrect(newInput)) {
                    return new ArrayList<>(Collections.singletonList(currentOperation));
                } else {
                    if (Utils.gapHeuristic(newInput) + currentDepth < maxDepth) {
                        ArrayList<Integer> solution = search(newInput, currentOperation, currentDepth + 1, maxDepth);
                        if (solution != null) {
                            solution.add(currentOperation);
                        }
                        return solution;
                    }
                }
            }
        }
        return null;
    }
}
