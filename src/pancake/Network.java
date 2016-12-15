package pancake;

import mpi.MPI;
import mpi.Status;

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

    // Both ways
    private final static int HERE_IS_WORK = 300;

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();

        if (rank == MASTER) {
            Network.runMaster();
        } else {
            Network.runSlave();
        }
    }

    private static void runMaster() {
        ArrayList<Integer> nodesWithWork = new ArrayList<>();
        Random random = new Random();
        // TODO initialize work

        Status status = MPI.COMM_WORLD.Recv(null, 0, 0, MPI.NULL, MPI.ANY_SOURCE, Network.I_NEED_WORK);

        MPI.COMM_WORLD.Send(new StackObject[]{new StackObject(-1)}, 0, 1, MPI.OBJECT, status.source,
                Network.HERE_IS_WORK);
        nodesWithWork.add(status.source);

        try {
            // wait a bit so the first worker has time to build an initial stack
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Thread.sleep failed");
        }

        main_loop: while (true) {
            Object[] buf = new Object[1];
            status = MPI.COMM_WORLD.Recv(buf, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);

            switch (status.tag) {
                case Network.I_NEED_WORK:
                    int index = random.nextInt(nodesWithWork.size());
                    int rank = nodesWithWork.get(index);
                    MPI.COMM_WORLD.Send(new Integer[]{new Integer(status.source)}, 0, 1, MPI.OBJECT, rank,
                            Network.GIVE_WORK);
                    break;
                case Network.HERE_IS_WORK:
                    HereIsWorkPackage hereIsWorkPackage = (HereIsWorkPackage) buf[0];
                    MPI.COMM_WORLD.Send(buf, 0, 1, MPI.OBJECT, hereIsWorkPackage.requestingNode, Network.HERE_IS_WORK);
                    break;
                case Network.I_HAVE_FOUND_A_SOLUTION:
                    SolutionPackage solutionPackage = (SolutionPackage) buf[0];
                    for (int s : solutionPackage.solution) {
                        System.out.println(s);
                    }
                    MPI.COMM_WORLD.Send(new Object[]{new Object()}, 0, 1, MPI.OBJECT,
                            MPI.ANY_SOURCE, Network.SOLUTION_WAS_FOUND);
                    break main_loop;
            }
        }
    }

    private static void runSlave() {

    }

    private static class HereIsWorkPackage {
        public Stack<StackObject> work;
        public int requestingNode;
    }

    private static class SolutionPackage {
        public ArrayList<Integer> solution;
    }
}
