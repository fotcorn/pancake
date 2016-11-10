package pancake;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

public class PanCakeRecursiveTest {

    private void assertAlgorithm(int[] input) {
        ArrayList<Integer> solution = PanCakeRecursive.startSearch(input);
        assertNotNull(solution);
        Utils.validateSolution(input, solution);
    }

    @Test
    public void test() {
        assertAlgorithm(new int[]{2, 1});

        assertAlgorithm(new int[]{1, 3, 2});
        assertAlgorithm(new int[]{2, 1, 3});
        assertAlgorithm(new int[]{2, 3, 1});
        assertAlgorithm(new int[]{3, 1, 2});
        assertAlgorithm(new int[]{3, 2, 1});
    }
}
