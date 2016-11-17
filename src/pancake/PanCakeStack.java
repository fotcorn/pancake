package pancake;

import java.util.ArrayList;
import java.util.Stack;

public class PanCakeStack {


    public static ArrayList<Integer> startSearch(int[] input) {
        int maxDepth = Utils.gapHeuristic(input);
        ArrayList<Integer> solution;
        while (true) {
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
            StackObject currentStackObject = stack.peek();
            if (currentStackObject.getStack().empty()) {
                stack.pop();
                continue;
            }
            int operation = currentStackObject.getStack().pop();

            ArrayList<Integer> possibleSolution = new ArrayList<>(stack.size() + 1);
            for (int i = 1; i < stack.size(); i++) {
                possibleSolution.add(stack.get(i).getOperation());
            }
            possibleSolution.add(operation);

            if (Utils.validateSolution(input, possibleSolution)) {
                return possibleSolution;
            }

            if (Utils.gapHeuristic(possibleSolution) + stack.size() > maxDepth) {
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

    private static class StackObject {
        int operation;
        Stack<Integer> stack;

        public StackObject(int operation) {
            this.operation = operation;
            stack = new Stack<>();
        }

        public int getOperation() {
            return operation;
        }

        public void setOperation(int operation) {
            this.operation = operation;
        }

        public Stack<Integer> getStack() {
            return stack;
        }
    }
}
