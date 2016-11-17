package pancake;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PanCakeStackTest {

    private void assertAlgorithm(int[] input) {
        ArrayList<Integer> solution = PanCakeStack.startSearch(input);
        assertTrue(Utils.validateInput(input));
        assertFalse(Utils.isCorrect(input));
        assertNotNull(solution);
        assertTrue(Utils.validateSolution(input, solution));
    }

    @Test
    public void test2() {
        assertAlgorithm(new int[]{2, 1});
    }

    @Test
    public void test3() {
        assertAlgorithm(new int[]{1, 3, 2});
        assertAlgorithm(new int[]{2, 1, 3});
        assertAlgorithm(new int[]{2, 3, 1});
        assertAlgorithm(new int[]{3, 1, 2});
        assertAlgorithm(new int[]{3, 2, 1});
    }

    @Test
    public void test4() {
        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                for (int k = 1; k < 5; k++) {
                    for (int l = 1; l < 5; l++) {
                        if (!(i == j || i == k || i == l || j == k || j == l || k == l) &&
                                !(i == 1 && j == 2 && k == 3 &&  l== 4)) {
                            assertAlgorithm(new int[] {i, j, k, l});
                        }
                    }
                }
            }
        }
    }
}
