package pancake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class PanCakeStack {


    public static ArrayList<Integer> startSearch(int[] input) {
        int maxDepth = Utils.gapHeuristic(input);
        ArrayList<Integer> solution;
        while (true) {
            System.out.printf("----------------\nmax depth: %s\n----------------\n", maxDepth);
            solution = search(input, maxDepth);
            if (solution != null) {
                break;
            }
            maxDepth++;
        }
        return solution;
    }

    public static ArrayList<Integer> search(int[] input, int maxDepth) {
        Stack<StackObject> stack = new Stack<>();
        StackObject root = new StackObject(-1);

        for (int operation = input.length - 2; operation >= 0; operation--) {
            root.getStack().push(operation);
        }
        stack.push(root);

        while (!stack.empty()) {
            printStack(stack);
            // maxDepth == 3 && stack.size() == 2 && currentStackObject.getOperation() == 2
            StackObject currentStackObject = stack.peek();
            if (currentStackObject.getStack().empty()) {
                stack.pop();
                continue;
            }
            int operation = currentStackObject.getStack().pop();

            // TODO use solution stack to avoid redoing the flips every time
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

    private static void printStack(Stack<StackObject> stack) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            StackObject stackObject = stack.get(i);
            System.out.printf("%d: %s\n", stackObject.getOperation(), stackObject.getStack());
        }
        System.out.println();
    }
}
