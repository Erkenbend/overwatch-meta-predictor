package predictor.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import predictor.exception.GraphException;
import predictor.team.Hero;
import predictor.team.Role;
import predictor.util.ResourceReader;
import predictor.util.Scored;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamGraphTest {

    private static TeamGraph teamGraph;

    @BeforeEach
    void setUp() {
        final List<Hero> heroList = assertDoesNotThrow(ResourceReader::getHeroesList);
        teamGraph = assertDoesNotThrow(() -> ResourceReader.getTeamGraph(heroList));
    }

    @Test
    void set_ok() {
        assertDoesNotThrow(() -> teamGraph.set(2, 1, 2));
    }

    @Test
    void set_illegalArgument1() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> teamGraph.set(0, 1, -1)
        );

        assertEquals("y should be lower than x", e.getMessage());
    }

    @Test
    void set_illegalArgument2() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> teamGraph.set(4, 1, 0)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void set_illegalArgument3() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> teamGraph.set(1, -1, 1)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void getConnection_int_ok() {
        assertEquals(-1, teamGraph.getConnection(2, 0));
        assertEquals(-1, teamGraph.getConnection(0, 2));
        assertEquals(2, teamGraph.getConnection(2, 1));
        assertEquals(2, teamGraph.getConnection(1, 2));
        assertEquals(0, teamGraph.getConnection(3, 3));
    }

    @Test
    void getConnection_int_illegalArgument1() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> teamGraph.getConnection(4, 2)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void getConnection_int_illegalArgument2() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> teamGraph.getConnection(0, -1)
        );

        assertEquals("not in range", e.getMessage());
        assertTrue(e.getCause() instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    void getConnection_string_ok() {
        assertEquals(0, assertDoesNotThrow(() -> teamGraph.getConnection("Firstdamage", "Seconddamage")));
        assertEquals(0, assertDoesNotThrow(() -> teamGraph.getConnection("Seconddamage", "Firstdamage")));
        assertEquals(-1, assertDoesNotThrow(() -> teamGraph.getConnection("Seconddamage", "Tank")));
        assertEquals(-1, assertDoesNotThrow(() -> teamGraph.getConnection("Tank", "Seconddamage")));
        assertEquals(0, assertDoesNotThrow(() -> teamGraph.getConnection("Tank", "Tank")));
    }

    @Test
    void getConnection_string_nonExistentHero() {
        GraphException e = assertThrows(
                GraphException.class,
                () -> teamGraph.getConnection("Firstdamage", "Nonexistent")
        );

        assertEquals("Hero Nonexistent not in heroes names list", e.getMessage());
    }

    @Test
    void getSortedConnections_int_ok() {
        // Get sorted connections for "Seconddamage"
        final List<Scored<String>> scoredList = assertDoesNotThrow(() -> teamGraph.getSortedConnections(1));

        // Result should be sorted in this order
        assertEquals(Arrays.asList(
                new Scored<>("Support", 2),
                new Scored<>("Firstdamage", 0),
                new Scored<>("Tank", -1)
        ), scoredList);
    }

    @Test
    void getSortedConnections_int_illegalArgument1() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> teamGraph.getSortedConnections(-1)
        );

        assertEquals("not in range", e.getMessage());
    }

    @Test
    void getSortedConnections_int_illegalArgument2() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> teamGraph.getSortedConnections(5)
        );

        assertEquals("not in range", e.getMessage());
    }

    @Test
    void getSortedConnections_string_nonExistentHero() {
        GraphException e = assertThrows(
                GraphException.class,
                () -> teamGraph.getSortedConnections("Nonexistent")
        );

        assertEquals("Hero Nonexistent not in heroes names list", e.getMessage());
    }
}