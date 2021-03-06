package pancake;

import java.util.*;



public class PancakeNetwork {

    public static final int PANCAKE_COUNT = 50;

    public static int[] getInput() {
        return getRandomInput();
        //return getPairwiseInput();
    }

    public static int[] getRandomInput() {
        ArrayList<Integer> input = new ArrayList<>();
        for (int i = 1; i < PANCAKE_COUNT; i++) {
            input.add(i);
        }

        Random r = new Random(1337);
        Collections.shuffle(input, r);

        int[] array = new int[input.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = input.get(i);
        }
        if (!Utils.validateInput(array)) {
            throw new IllegalArgumentException("generated input array is invalid");
        }
        return array;
    }

    public static int[] getPairwiseInput() {
        ArrayList<Integer> input = new ArrayList<>();
        for (int i = 1; i < PANCAKE_COUNT / 2; i += 2) {
            input.add(i + 1);
            input.add(i);
        }
        int[] array = new int[input.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = input.get(i);
        }
        if (!Utils.validateInput(array)) {
            throw new IllegalArgumentException("generated input array is invalid");
        }
        return array;
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
        long start = System.currentTimeMillis();
        while (!stack.empty()) {
            if (System.currentTimeMillis() - start > 50) {
                return null;
            }

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
