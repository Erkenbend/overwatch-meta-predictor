package predictor.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import predictor.exception.GraphException;
import predictor.team.Hero;
import predictor.util.ResourceReader;
import predictor.util.Scored;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnemyGraphTest {

    private static EnemyGraph enemyGraph;

    @BeforeEach
    void setUp() {
        final List<Hero> heroList = assertDoesNotThrow(ResourceReader::getHeroesList);
        enemyGraph = assertDoesNotThrow(() -> ResourceReader.getEnemyGraph(heroList));
    }

    @Test
    void set_ok() {
        assertDoesNotThrow(() -> enemyGraph.set(3, 0, 1));
    }

    @Test
    void set_illegalArgument1() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> enemyGraph.set(1, 2, -2)
        );

        assertEquals("y should be lower than x", e.getMessage());
    }

    @Test
    void set_illegalArgument2() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> enemyGraph.set(4, 2, 1)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void set_illegalArgument3() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> enemyGraph.set(0, -1, 0)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void getDominance_int_ok() {
        assertEquals(-1, enemyGraph.getDominance(2, 3));
        assertEquals(1, enemyGraph.getDominance(3, 2));
        assertEquals(1, enemyGraph.getDominance(1, 0));
        assertEquals(-1, enemyGraph.getDominance(0, 1));
        assertEquals(0, enemyGraph.getDominance(3, 3));
    }

    @Test
    void getDominance_int_illegalArgument1() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> enemyGraph.getDominance(4, 2)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void getDominance_int_illegalArgument2() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> enemyGraph.getDominance(0, -1)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void getDominance_string_ok() {
        assertEquals(2, assertDoesNotThrow(() -> enemyGraph.getDominance("Firstdamage", "Support")));
        assertEquals(-2, assertDoesNotThrow(() -> enemyGraph.getDominance("Support", "Firstdamage")));
        assertEquals(1, assertDoesNotThrow(() -> enemyGraph.getDominance("Seconddamage", "Tank")));
        assertEquals(-1, assertDoesNotThrow(() -> enemyGraph.getDominance("Tank", "Seconddamage")));
        assertEquals(0, assertDoesNotThrow(() -> enemyGraph.getDominance("Tank", "Tank")));
    }

    @Test
    void getDominance_string_nonExistentHero() {
        GraphException e = assertThrows(
                GraphException.class,
                () -> enemyGraph.getDominance("Firstdamage", "Nonexistent")
        );

        assertEquals("Hero Nonexistent not in heroes names list", e.getMessage());
    }

    @Test
    void getSortedCounters_int_ok() {
        // Get sorted counters for "Support"
        final List<Scored<String>> scoredList = assertDoesNotThrow(() -> enemyGraph.getSortedCounters(2));

        // Result should be sorted in this order
        assertEquals(Arrays.asList(
                new Scored<>("Firstdamage", 2),
                new Scored<>("Tank", 1),
                new Scored<>("Seconddamage", 0)
        ), scoredList);
    }

    @Test
    void getSortedCounters_int_illegalArgument1() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> enemyGraph.getSortedCounters(-2)
        );

        assertEquals("not in range", e.getMessage());
    }

    @Test
    void getSortedCounters_int_illegalArgument2() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> enemyGraph.getSortedCounters(4)
        );

        assertEquals("not in range", e.getMessage());
    }

    @Test
    void getSortedCounters_string_nonExistentHero() {
        GraphException e = assertThrows(
                GraphException.class,
                () -> enemyGraph.getSortedCounters("Nonexistent")
        );

        assertEquals("Hero Nonexistent not in heroes names list", e.getMessage());
    }
}
