package pancake;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

public class PanCakeTest {

    @Test
    public void testValidateInput() {
        assertTrue(PanCake.validateInput(Arrays.asList(2, 4, 1, 3)));
        assertTrue(PanCake.validateInput(Arrays.asList(2, 3, 1)));
        assertTrue(PanCake.validateInput(Arrays.asList(3, 2, 1)));

        assertFalse(PanCake.validateInput(Arrays.asList(1, 3)));
        assertFalse(PanCake.validateInput(Arrays.asList(3, 1)));
        assertFalse(PanCake.validateInput(Arrays.asList(1, 1)));
        assertFalse(PanCake.validateInput(Arrays.asList(1, 2, 2)));
    }


}
