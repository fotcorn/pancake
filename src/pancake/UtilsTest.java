package pancake;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testValidateInput() {
        assertTrue(Utils.validateInput(new int[]{2, 4, 1, 3}));
        assertTrue(Utils.validateInput(new int[]{2, 3, 1}));
        assertTrue(Utils.validateInput(new int[]{3, 2, 1}));

        assertFalse(Utils.validateInput(new int[]{1, 3}));
        assertFalse(Utils.validateInput(new int[]{3, 1}));
        assertFalse(Utils.validateInput(new int[]{1, 1}));
        assertFalse(Utils.validateInput(new int[]{1, 2, 2}));
    }

    @Test
    public void testValidateSolution() {
        assertTrue(Utils.validateSolution(new int[]{2, 1}, new int[]{0}));

        assertTrue(Utils.validateSolution(new int[]{1, 3, 2}, new int[]{0, 1, 0}));
        assertTrue(Utils.validateSolution(new int[]{2, 1, 3}, new int[]{0}));
        assertTrue(Utils.validateSolution(new int[]{2, 3, 1}, new int[]{0, 1}));
        assertTrue(Utils.validateSolution(new int[]{3, 1, 2}, new int[]{1, 0}));
        assertTrue(Utils.validateSolution(new int[]{3, 2, 1}, new int[]{1}));
    }

    @Test
    public void testGAPHeuristic() {
        assertEquals(Utils.gapHeuristic(new int[]{1, 2}), 0);
        assertEquals(Utils.gapHeuristic(new int[]{2, 1}), 1);

        assertEquals(Utils.gapHeuristic(new int[]{1, 2, 3}), 0);
        assertEquals(Utils.gapHeuristic(new int[]{1, 3, 2}), 2);
        assertEquals(Utils.gapHeuristic(new int[]{2, 1, 3}), 1);
        assertEquals(Utils.gapHeuristic(new int[]{2, 3, 1}), 2);
        assertEquals(Utils.gapHeuristic(new int[]{3, 1, 2}), 2);
        assertEquals(Utils.gapHeuristic(new int[]{3, 2, 1}), 1);
    }

    private static void assertFlip(int[] input, int action, int[] expectedOutput) {
        Utils.flip(input, action);
        assertArrayEquals(input, expectedOutput);
    }

    @Test
    public void testFlip() {
        assertFlip(new int[]{1, 2, 3, 4, 5}, 0, new int[]{2, 1, 3, 4, 5});
        assertFlip(new int[]{1, 2, 3, 4, 5}, 1, new int[]{3, 2, 1, 4, 5});
        assertFlip(new int[]{1, 2, 3, 4, 5}, 2, new int[]{4, 3, 2, 1, 5});
        assertFlip(new int[]{1, 2, 3, 4, 5}, 3, new int[]{5, 4, 3, 2, 1});
    }
}
