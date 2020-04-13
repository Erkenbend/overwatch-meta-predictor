package predictor.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoredTest {

    @Test
    void compareTo_equal() {
        Scored<String> obj1 = new Scored<>("Obj1", 2);
        Scored<String> obj2 = new Scored<>("Obj2", 2);

        assertEquals(0, obj1.compareTo(obj2));
    }

    @Test
    void compareTo_greater() {
        Scored<String> obj1 = new Scored<>("Obj1", -1);
        Scored<String> obj2 = new Scored<>("Obj2", 2);

        assertTrue(obj1.compareTo(obj2) < 0);
    }

    @Test
    void compareTo_lower() {
        Scored<String> obj1 = new Scored<>("Obj1", 0);
        Scored<String> obj2 = new Scored<>("Obj2", -2);

        assertTrue(obj1.compareTo(obj2) > 0);
    }

    @Test
    void testToString() {
        Scored<String> testString = new Scored<>("Test", 8);
        assertEquals("<Test, 8>", testString.toString());
    }
}