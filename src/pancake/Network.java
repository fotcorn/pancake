package pancake;

import mpi.MPI;
import mpi.Request;
import mpi.Status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Network {

    private final static int MASTER = 0;

    // Slave => Master
    private final static int I_NEED_WORK = 100;
    private final static int I_HAVE_FOUND_A_SOLUTION = 101;

    // Master => Slave
    private final static int GIVE_WORK = 200;
    private final static int SOLUTION_WAS_FOUND = 201;
    private final static int RESTART = 202;

    // Both ways
    private final static int HERE_IS_WORK = 300;

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();

        if (rank == MASTER) {
            Network.runMaster();
        } else {
            boolean ret = false;
            while (!ret) {
                ret = Network.runSlave();
            }
        }
        MPI.Finalize();
    }

    private static void runMaster() {
        ArrayList<Integer> nodesWithWork = new ArrayList<>();
        Random random = new Random();

        int[] input = PancakeNetwork.getInput();
        int maxDepth = Utils.gapHeuristic(input);

        main_loop: while (true) {
            Stack<StackObject> initialWork = PancakeNetwork.getInitialWork(input);

            Object[] buf = new Object[1];
            Status status = MPI.COMM_WORLD.Recv(buf, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, Network.I_NEED_WORK);
            System.out.printf("M: Received I_NEED_WORK message from %d\n", status.source);

            MPI.COMM_WORLD.Send(new Object[]{new HereIsWorkPackage(input, initialWork, maxDepth, -1)}, 0, 1, MPI.OBJECT,
                    status.source, Network.HERE_IS_WORK);
            nodesWithWork.add(status.source);
            System.out.println("M: Sent HERE_IS_WORK message\n");

            try {
                // wait a bit so the first worker has time to build an initial stack
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep failed");
            }

            networkLoop: while (true) {
                status = MPI.COMM_WORLD.Recv(buf, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
                System.out.printf("M: received package: %d, %d\n", status.tag, status.source);
                switch (status.tag) {
                    case Network.I_NEED_WORK:
                        nodesWithWork.remove(status.source);
                        if (nodesWithWork.size() == 0) {
                            MPI.COMM_WORLD.Bsend(new Object[]{new EmptyPackage()}, 0, 1, MPI.OBJECT,
                                    MPI.ANY_SOURCE, Network.RESTART);
                            System.out.printf("M: sent RESTART message to everyone\n");
                            break networkLoop;
                        }
                        int index = random.nextInt(nodesWithWork.size());
                        int rank = nodesWithWork.get(index);
                        MPI.COMM_WORLD.Bsend(new Integer[]{new Integer(status.source)}, 0, 1, MPI.OBJECT, rank,
                                Network.GIVE_WORK);
                        System.out.printf("M: sent GIVE_WORK message to %s\n", rank);
                        break;
                    case Network.HERE_IS_WORK:
                        HereIsWorkPackage hereIsWorkPackage = (HereIsWorkPackage) buf[0];
                        MPI.COMM_WORLD.Bsend(buf, 0, 1, MPI.OBJECT, hereIsWorkPackage.requestingNode, Network.HERE_IS_WORK);
                        nodesWithWork.add(hereIsWorkPackage.requestingNode);
                        System.out.printf("M: sent HERE_IS_WORK message to %s\n", hereIsWorkPackage.requestingNode);
                        break;
                    case Network.I_HAVE_FOUND_A_SOLUTION:
                        System.out.printf("M: %s has found a solution\n", status.source);
                        SolutionPackage solutionPackage = (SolutionPackage) buf[0];
                        for (int s : solutionPackage.solution) {
                            System.out.println(s);
                        }
                        MPI.COMM_WORLD.Bsend(new Object[]{new EmptyPackage()}, 0, 1, MPI.OBJECT,
                                MPI.ANY_SOURCE, Network.SOLUTION_WAS_FOUND);
                        break main_loop;
                    default:
                        throw new IllegalArgumentException("Master: Received illegal package type\n");
                }
            }
            maxDepth++;
        }
    }

    private static boolean runSlave() {
        Stack<StackObject> stack = new Stack<>();
        int maxDepth = 0;
        int[] input = new int[0];
        boolean workRequestSent = false;
        while (true) {
            Object[] buf = new Object[1];
            Request request = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.OBJECT, Network.MASTER, MPI.ANY_TAG);
            Status test;
            if (workRequestSent) {
                test = request.Wait();
            } else {
                test = request.Test();
            }
            while (test != null) {
                System.out.printf("S: received package %d\n", test.tag);
                switch (test.tag) {
                    case Network.HERE_IS_WORK:
                        if (!stack.empty()) {
                            throw new IllegalArgumentException("HERE_IS_WORK message received with non-empty stack");
                        } else {
                            HereIsWorkPackage hereIsWorkPackage = (HereIsWorkPackage) buf[0];
                            input = hereIsWorkPackage.input;
                            stack = hereIsWorkPackage.work;
                            maxDepth = hereIsWorkPackage.maxDepth;
                            workRequestSent = false;
                        }
                        break;
                    case Network.GIVE_WORK:
                        // TODO: split stack and send
                        break;
                    case Network.RESTART:
                        return false;
                    case Network.SOLUTION_WAS_FOUND:
                        return true;
                }
                test = request.Test();
            }

            if (stack.empty()) {
                System.out.println("S: sending I_NEED_WORK message");
                MPI.COMM_WORLD.Send(new Object[]{new EmptyPackage()}, 0, 1, MPI.OBJECT, Network.MASTER, Network.I_NEED_WORK);
                workRequestSent = true;
            } else {
                ArrayList<Integer> solution = PancakeNetwork.search(input, stack, maxDepth);
                if (solution != null) {
                    MPI.COMM_WORLD.Send(new Object[]{new SolutionPackage(solution)}, 0, 1, MPI.OBJECT, Network.MASTER,
                            Network.I_HAVE_FOUND_A_SOLUTION);
                    return true;
                }
            }
        }
    }

    private static class HereIsWorkPackage implements Serializable {
        public Stack<StackObject> work;
        public int requestingNode;
        public int maxDepth;
        public int[] input;

        public HereIsWorkPackage(int[] input, Stack<StackObject> work, int maxDepth, int requestingNode) {
            this.work = work;
            this.requestingNode = requestingNode;
            this.maxDepth = maxDepth;
            this.input = input;
        }
    }

    private static class SolutionPackage implements Serializable {
        public ArrayList<Integer> solution;

        public SolutionPackage(ArrayList<Integer> solution) {
            this.solution = solution;
        }
    }

    private static class EmptyPackage implements Serializable {}
}
