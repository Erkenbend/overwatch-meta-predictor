package predictor.graph;

import org.junit.jupiter.api.Test;
import predictor.util.Scored;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphHelperTest {

    @Test
    void zipAndSort() {
        List<String> heroesNames = Arrays.asList("Hero1", "Hero2", "Hero3", "Hero4");
        List<Integer> scores = Arrays.asList(1, 3, -1, 0);

        assertEquals(Arrays.asList(
                new Scored<>("Hero2", 3),
                new Scored<>("Hero1", 1),
                new Scored<>("Hero4", 0),
                new Scored<>("Hero3", -1)
        ), GraphHelper.zipAndSort(heroesNames, scores));
    }

    @Test
    void getPossiblePairs() {
        List<Integer[]> expectedResult = Arrays.asList(
                new Integer[] {0, 1},
                new Integer[] {0, 2},
                new Integer[] {0, 3},
                new Integer[] {1, 2},
                new Integer[] {1, 3},
                new Integer[] {2, 3}
        );

        List<Integer[]> actualResult = GraphHelper.getPossiblePairs(0, 4);

        assertEquals(expectedResult.size(), actualResult.size());
        for (Integer[] pair : expectedResult) {
            assertTrue(expectedResult.stream().anyMatch(p -> Arrays.equals(p, pair)));
        }
    }

    @Test
    void getPossiblePairsWithRepetition() {
        List<Integer[]> expectedResult = Arrays.asList(
                new Integer[] {0, 0},
                new Integer[] {0, 1},
                new Integer[] {0, 2},
                new Integer[] {0, 3},
                new Integer[] {1, 0},
                new Integer[] {1, 1},
                new Integer[] {1, 2},
                new Integer[] {1, 3},
                new Integer[] {2, 0},
                new Integer[] {2, 1},
                new Integer[] {2, 2},
                new Integer[] {2, 3},
                new Integer[] {3, 0},
                new Integer[] {3, 1},
                new Integer[] {3, 2},
                new Integer[] {3, 3}
        );

        List<Integer[]> actualResult = GraphHelper.getPossiblePairsWithRepetition(0, 4);

        assertEquals(expectedResult.size(), actualResult.size());
        for (Integer[] pair : expectedResult) {
            assertTrue(expectedResult.stream().anyMatch(p -> Arrays.equals(p, pair)));
        }
    }
}