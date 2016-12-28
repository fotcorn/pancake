package pancake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class PancakeNetwork {

    public static int[] getInput() {
        return new int[] {5, 2, 3, 1, 4};
    }

    public static Stack<StackObject> getInitialWork(int[] input) {
        Stack<StackObject> stack = new Stack<>();
        StackObject root = new StackObject(-1);

        for (int operation = input.length - 2; operation >= 0; operation--) {
            root.getStack().push(operation);
        }
        stack.push(root);
        return stack;
    }

    public static ArrayList<Integer> search(int[] input, Stack<StackObject> stack, int maxDepth) {
        while (!stack.empty()) {
            StackObject currentStackObject = stack.peek();
            if (currentStackObject.getStack().empty()) {
                stack.pop();
                continue;
            }
            int operation = currentStackObject.getStack().pop();

            int[] newInput = Arrays.copyOf(input, input.length);
            for (int i = 1; i < stack.size(); i++) {
                Utils.flip(newInput, stack.get(i).getOperation());
            }
            Utils.flip(newInput, operation);

            if (Utils.isCorrect(newInput)) {
                ArrayList<Integer> solution = new ArrayList<>();
                for (int i = 1; i < stack.size(); i++) {
                    solution.add(stack.get(i).getOperation());
                }
                solution.add(operation);
                return solution;
            }

            if (Utils.gapHeuristic(newInput) + stack.size() > maxDepth) {
                continue;
            }

            // create new stack object
            StackObject newStackObject = new StackObject(operation);
            for (int newOperation = input.length - 2; newOperation >= 0; newOperation--) {
                if (newOperation != operation) {
                    newStackObject.getStack().push(newOperation);
                }
            }
            stack.push(newStackObject);
        }
        return null;
    }

}
