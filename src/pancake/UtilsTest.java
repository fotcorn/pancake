package pancake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

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
}
