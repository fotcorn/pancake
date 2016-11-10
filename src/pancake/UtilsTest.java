package pancake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UtilsTest {

    @Test
    public void testValidateInput() {
        assertTrue(Utils.validateInput(Arrays.asList(2, 4, 1, 3)));
        assertTrue(Utils.validateInput(Arrays.asList(2, 3, 1)));
        assertTrue(Utils.validateInput(Arrays.asList(3, 2, 1)));

        assertFalse(Utils.validateInput(Arrays.asList(1, 3)));
        assertFalse(Utils.validateInput(Arrays.asList(3, 1)));
        assertFalse(Utils.validateInput(Arrays.asList(1, 1)));
        assertFalse(Utils.validateInput(Arrays.asList(1, 2, 2)));
    }

    @Test
    public void testValidateSolution() {
        assertTrue(Utils.validateSolution(Arrays.asList(2, 1), Collections.singletonList(0)));

        assertTrue(Utils.validateSolution(Arrays.asList(1, 3, 2), Arrays.asList(0, 1, 0)));
        assertTrue(Utils.validateSolution(Arrays.asList(2, 1, 3), Collections.singletonList(0)));
        assertTrue(Utils.validateSolution(Arrays.asList(2, 3, 1), Arrays.asList(0, 1)));
        assertTrue(Utils.validateSolution(Arrays.asList(3, 1, 2), Arrays.asList(1, 0)));
        assertTrue(Utils.validateSolution(Arrays.asList(3, 2, 1), Collections.singletonList(1)));
    }

    @Test
    public void testGAPHeuristic() {
        assertEquals(Utils.gapHeuristic(Arrays.asList(1, 2)), 0);
        assertEquals(Utils.gapHeuristic(Arrays.asList(2, 1)), 1);

        assertEquals(Utils.gapHeuristic(Arrays.asList(1, 2, 3)), 0);
        assertEquals(Utils.gapHeuristic(Arrays.asList(1, 3, 2)), 2);
        assertEquals(Utils.gapHeuristic(Arrays.asList(2, 1, 3)), 1);
        assertEquals(Utils.gapHeuristic(Arrays.asList(2, 3, 1)), 2);
        assertEquals(Utils.gapHeuristic(Arrays.asList(3, 1, 2)), 2);
        assertEquals(Utils.gapHeuristic(Arrays.asList(3, 2, 1)), 1);
    }

    private static void assertFlip(List<Integer> input, int action, List<Integer> expectedOutput) {
        Utils.flip(input, action);
        assertEquals(input, expectedOutput);
    }

    @Test
    public void testFlip() {
        assertFlip(Arrays.asList(1, 2, 3, 4, 5), 0, Arrays.asList(2, 1, 3, 4, 5));
        assertFlip(Arrays.asList(1, 2, 3, 4, 5), 1, Arrays.asList(3, 2, 1, 4, 5));
        assertFlip(Arrays.asList(1, 2, 3, 4, 5), 2, Arrays.asList(4, 3, 2, 1, 5));
        assertFlip(Arrays.asList(1, 2, 3, 4, 5), 3, Arrays.asList(5, 4, 3, 2, 1));
    }
}
