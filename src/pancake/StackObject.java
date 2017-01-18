package pancake;

import java.io.Serializable;
import java.util.Stack;


class StackObject implements Serializable {
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

    @Override
    public String toString() {
        return String.format("[o: %d, %s]\n", this.operation, this.stack);
    }
}
