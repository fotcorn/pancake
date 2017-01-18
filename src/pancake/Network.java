package pancake;

import mpi.MPI;
import mpi.Request;
import mpi.Status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class Network {

    private final static int MASTER = 0;

    // Slave => Master
    private final static int I_NEED_WORK = 100;
    private final static int I_HAVE_FOUND_A_SOLUTION = 101;
    private final static int NO_WORK_LEFT = 102;

    // Master => Slave
    private final static int GIVE_WORK = 200;
    private final static int SOLUTION_WAS_FOUND = 201;
    private final static int RESTART = 202;

    // Both ways
    private final static int HERE_IS_WORK = 300;

    private static HashMap<Integer, String> packetNames = new HashMap<>();

    public static void main(String[] args) {
        packetNames.put(MASTER, "MASTER");
        packetNames.put(I_NEED_WORK, "I_NEED_WORK");
        packetNames.put(I_HAVE_FOUND_A_SOLUTION, "I_HAVE_FOUND_A_SOLUTION");
        packetNames.put(GIVE_WORK, "GIVE_WORK");
        packetNames.put(SOLUTION_WAS_FOUND, "SOLUTION_WAS_FOUND");
        packetNames.put(RESTART, "RESTART");
        packetNames.put(HERE_IS_WORK, "HERE_IS_WORK");
        packetNames.put(NO_WORK_LEFT, "NO_WORK_LEFT");

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();

        if (rank == MASTER) {
            Network.runMaster();
        } else {
            boolean ret = false;
            while (!ret) {
                ret = Network.runSlave(rank);
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
            System.out.println("-----------------------------");
            System.out.printf("Start new work with maxDepth %d\n", maxDepth);
            System.out.println("-----------------------------");


            Stack<StackObject> initialWork = PancakeNetwork.getInitialWork(input);

            Object[] buf = new Object[1];
            Status status = MPI.COMM_WORLD.Recv(buf, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, Network.I_NEED_WORK);
            System.out.printf("M: Received I_NEED_WORK message from %d\n", status.source);

            MPI.COMM_WORLD.Isend(new Object[]{new HereIsWorkPackage(input, initialWork, maxDepth, -1)}, 0, 1, MPI.OBJECT,
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
                System.out.printf("M: received package: %s, %d\n", packetNames.get(status.tag), status.source);
                switch (status.tag) {
                    case Network.I_NEED_WORK:
                        int i = nodesWithWork.indexOf(status.source);
                        if (i != -1) {
                            nodesWithWork.remove(i);
                            if (nodesWithWork.size() == 0) {
                                int size = MPI.COMM_WORLD.Size();
                                for (int rank = 1; rank < size; rank++) {
                                    MPI.COMM_WORLD.Isend(new Object[]{new EmptyPackage()}, 0, 1, MPI.OBJECT,
                                            rank, Network.RESTART);
                                }
                                System.out.printf("M: sent RESTART message to everyone\n");
                                break networkLoop;
                            }
                        }
                        int index = random.nextInt(nodesWithWork.size());
                        int rank = nodesWithWork.get(index);
                        MPI.COMM_WORLD.Isend(new Integer[]{new Integer(status.source)}, 0, 1, MPI.OBJECT, rank,
                                Network.GIVE_WORK);
                        System.out.printf("M: sent GIVE_WORK message to %s\n", rank);
                        break;
                    case Network.NO_WORK_LEFT:
                        i = nodesWithWork.indexOf(status.source);
                        if (i != -1) {
                            nodesWithWork.remove(i);
                            if (nodesWithWork.size() == 0) {
                                int size = MPI.COMM_WORLD.Size();
                                for (rank = 1; rank < size; rank++) {
                                    MPI.COMM_WORLD.Isend(new Object[]{new EmptyPackage()}, 0, 1, MPI.OBJECT,
                                            rank, Network.RESTART);
                                }
                                System.out.printf("M: sent RESTART message to everyone\n");
                                break networkLoop;
                            }
                        }
                        break;
                    case Network.HERE_IS_WORK:
                        HereIsWorkPackage hereIsWorkPackage = (HereIsWorkPackage) buf[0];
                        if (hereIsWorkPackage.maxDepth != maxDepth) {
                            System.out.println("M: received old HERE_IS_WORK package, not sending it out.");
                            break;
                        }
                        MPI.COMM_WORLD.Isend(buf, 0, 1, MPI.OBJECT, hereIsWorkPackage.requestingNode, Network.HERE_IS_WORK);
                        nodesWithWork.add(hereIsWorkPackage.requestingNode);
                        System.out.printf("M: sent HERE_IS_WORK message to %s\n", hereIsWorkPackage.requestingNode);
                        break;
                    case Network.I_HAVE_FOUND_A_SOLUTION:
                        System.out.printf("M: %s has found a solution\n", status.source);
                        SolutionPackage solutionPackage = (SolutionPackage) buf[0];

                        if (!Utils.validateSolution(input, solutionPackage.solution)) {
                            throw new IllegalArgumentException("received solution is invalid");
                        }

                        for (int s : solutionPackage.solution) {
                            System.out.println(s);
                        }
                        int size = MPI.COMM_WORLD.Size();
                        for (int receiver = 1; receiver < size; receiver++) {
                            MPI.COMM_WORLD.Isend(new Object[]{new EmptyPackage()}, 0, 1, MPI.OBJECT,
                                    receiver, Network.SOLUTION_WAS_FOUND);
                        }

                        break main_loop;
                    default:
                        throw new IllegalArgumentException("Master: Received illegal package type\n");
                }
            }
            maxDepth++;
        }
    }


    private static boolean runSlave(int rank) {
        Stack<StackObject> stack = new Stack<>();
        int maxDepth = 0;
        int[] input = new int[0];
        boolean workRequestSent = false;


        NetworkRequest request = new NetworkRequest();
        while (true) {
            Status status;
            if (workRequestSent) {
                status = request.Wait();
            } else {
                status = request.Test();
            }
            while (status != null) {
                System.out.printf("S %d: received package %s\n", rank, packetNames.get(status.tag));
                switch (status.tag) {
                    case Network.HERE_IS_WORK:
                        if (!stack.empty()) {
                            throw new IllegalArgumentException(String.format("S %d: HERE_IS_WORK message received with non-empty stack", rank));
                        } else {
                            HereIsWorkPackage hereIsWorkPackage = (HereIsWorkPackage) request.buf[0];
                            input = hereIsWorkPackage.input;
                            stack = hereIsWorkPackage.work;
                            maxDepth = hereIsWorkPackage.maxDepth;
                            workRequestSent = false;
                        }
                        break;
                    case Network.GIVE_WORK:
                        if (stack.isEmpty()) {
                            MPI.COMM_WORLD.Send(new Object[]{new EmptyPackage()}, 0, 1, MPI.OBJECT, Network.MASTER, Network.NO_WORK_LEFT);
                        } else {
                            Stack<StackObject> newStack = new Stack<>();
                            for (StackObject obj : stack) {
                                obj.getStack();

                                StackObject newObj = new StackObject(obj.getOperation());

                                Stack<Integer> oStack = obj.getStack();
                                Stack<Integer> nStack = newObj.getStack();

                                for (int i : oStack.subList(0, oStack.size() / 2)) {
                                    nStack.push(i);
                                }
                                oStack.subList(0, oStack.size() / 2).clear();

                                newStack.push(newObj);
                            }
                            int destinationRank = (Integer) request.buf[0];
                            HereIsWorkPackage hereIsWorkPackage = new HereIsWorkPackage(input, newStack, maxDepth, destinationRank);
                            MPI.COMM_WORLD.Send(new Object[]{hereIsWorkPackage}, 0, 1, MPI.OBJECT, Network.MASTER, Network.HERE_IS_WORK);
                            System.out.printf("S %d: sent HERE_IS_WORK message to master for %d\n", rank, destinationRank);
                        }
                        break;
                    case Network.RESTART:
                        return false;
                    case Network.SOLUTION_WAS_FOUND:
                        return true;
                    default:
                        throw new IllegalArgumentException("Unexpected package received:" + status.tag);
                }
                status = request.Test();
            }

            if (stack.empty()) {
                System.out.printf("S %d: sending I_NEED_WORK message\n", rank);
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

    private static class NetworkRequest {

        private Request request = null;
        public Object[] buf = new Object[1];

        public Status Test() {
            if (request == null) {
                this.request = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
            }
            Status status = request.Test();
            if (status != null) {
                this.request = null;
            }
            return status;
        }

        public Status Wait() {
            if (request == null) {
                this.request = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
            }
            Status status = request.Wait();
            this.request = null;
            return status;
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
