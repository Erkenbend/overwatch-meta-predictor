package predictor.graph;

import predictor.util.Scored;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GraphHelper {

    /**
     * Assuming that the scores in the second list correspond to the names in
     * the first list, merge them together and return a list of scored names,
     * ordered by descending scores
     *
     * @param heroesNames list of heroes names
     * @param scores list of score
     * @return zipped sorted list
     */
    public static List<Scored<String>> zipAndSort(List<String> heroesNames, List<Integer> scores) {
        // zip heroes names and scores
        Iterator<String> heroesNamesIterator = heroesNames.iterator();
        Iterator<Integer> scoresIterator = scores.iterator();
        List<Scored<String>> scoredHeroes = new ArrayList<>();
        while(heroesNamesIterator.hasNext() && scoresIterator.hasNext()) {
            String heroName = heroesNamesIterator.next();
            int score = scoresIterator.next();
            scoredHeroes.add(new Scored<>(heroName, score));
        }

        // sort on descending score
        Collections.sort(scoredHeroes);
        Collections.reverse(scoredHeroes);
        return scoredHeroes;
    }

    /**
     * Find all possible pairs of integer <b>without repetition</b> between
     * lowerLimit (inclusive) and upperLimit (exclusive)
     *
     * Use for example when computing all the relationships within a team
     *
     * TODO: maybe refactor this, use iterators/streams?
     */
    public static List<Integer[]> getPossiblePairs(int lowerLimit, int upperLimit) {
        List<Integer[]> result = new ArrayList<>();
        for (int i = lowerLimit; i < upperLimit; i++) {
            for (int j = i + 1; j < upperLimit; j++) {
                result.add(new Integer[] {i, j});
            }
        }
        return result;
    }

    /**
     * Find all possible pairs of integer <b>with repetition</b> between
     * lowerLimit (inclusive) and upperLimit (exclusive)
     *
     * Use for example when computing all the relationships between 2 teams
     *
     * TODO: maybe refactor this, use iterators/streams?
     */
    public static List<Integer[]> getPossiblePairsWithRepetition(int lowerLimit, int upperLimit) {
        List<Integer[]> result = new ArrayList<>();
        for (int i = lowerLimit; i < upperLimit; i++) {
            for (int j = lowerLimit; j < upperLimit; j++) {
                result.add(new Integer[] {i, j});
            }
        }
        return result;
    }
}
