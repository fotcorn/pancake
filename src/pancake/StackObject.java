package pancake;

import java.util.Stack;


class StackObject {
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
