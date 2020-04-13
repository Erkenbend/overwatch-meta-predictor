package predictor.graph;

import predictor.team.Hero;
import predictor.util.Scored;
import predictor.exception.GraphException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Anti-symmetrical matrix (oriented graph) representing how heroes counter each other.
 * Only the lower-left part is stored in memory
 * <p>
 * <p>
 * Example:
 * <p>
 * Hero1 Hero2 Hero3
 * Hero1   0
 * Hero2   1     0
 * Hero3   3    -1      0
 * <p>
 * Hero 2 is a soft counter to heroes 1 and 3. Hero 3 is a hard counter to Hero 1.
 */
public class EnemyGraph extends Graph {

    /**
     * Constructor for EnemyGraph.
     *
     * Warning: the matrix will be empty of content. To get a filled matrix, either:
     * - use the Graph::set method in parent class
     * - or initialize with ResourceReader::getEnemyGraph
     *
     * @param heroesList heroes list
     */
    public EnemyGraph(List<Hero> heroesList) {
        super(heroesList);
    }

    public int getDominance(String from, String over) throws GraphException {
        int posX = this.getIndexOfHero(from);
        int posY = this.getIndexOfHero(over);

        return this.getDominance(posX, posY);
    }

    public int getDominance(int x, int y) {
        try {
            // enemy_graph is a anti-symmetrical matrix, we only load the down-left part
            return y > x ? -this.matrix[y][x] : this.matrix[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not in range", e);
        }
    }

    /**
     * @param hero hero name
     * @return all 1-to-1 matches against this hero, sorted strongest to weakest
     * @throws GraphException when hero not found
     */
    public List<Scored<String>> getSortedCounters(String hero) throws GraphException {
        return this.getSortedCounters(this.getIndexOfHero(hero));
    }

    /**
     * @param pos hero position in matrix
     * @return all 1-to-1 matches against this hero, sorted strongest to weakest
     */
    public List<Scored<String>> getSortedCounters(int pos) {
        if (pos < 0 || pos >= this.matrix.length) {
            throw new IllegalArgumentException("not in range");
        }

        // list heroes dominance over input hero
        List<Integer> counters = IntStream.range(0, this.matrix.length)
                .map(opponentPos -> this.getDominance(opponentPos, pos))
                .boxed().collect(Collectors.toList());
        counters.remove(pos);

        // list other heroes from name list
        List<String> otherHeroesNames = new ArrayList<>(this.heroesList).stream()
                .map(Hero::getName).collect(Collectors.toList());
        otherHeroesNames.remove(pos);

        return GraphHelper.zipAndSort(otherHeroesNames, counters);
    }
}
