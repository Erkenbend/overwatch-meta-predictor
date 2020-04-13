package predictor.graph;

import predictor.team.Hero;
import predictor.util.Scored;
import predictor.exception.GraphException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Symmetrical matrix (non-oriented graph) representing heroes strength when combined to each other.
 * Only the lower-left part is stored in memory
 * <p>
 * <p>
 * Example:
 * <p>
 *       Hero1 Hero2 Hero3
 * Hero1   0
 * Hero2   1     0
 * Hero3   2    -1      0
 * <p>
 * Heroes 1 and 2 are a good team, but not as much as heroes 1 and 3 together. Heroes 2 and 3 do
 * not really work well together.
 */
public class TeamGraph extends Graph {

    /**
     * Constructor for TeamGraph.
     *
     * Warning: the matrix will be empty of content. To get a filled matrix, either:
     * - use the Graph::set method in parent class
     * - or initialize with ResourceReader::getTeamGraph
     *
     * @param heroesList heroes list
     */
    public TeamGraph(List<Hero> heroesList) {
        super(heroesList);
    }

    public int getConnection(String heroX, String heroY) throws GraphException {
        int posX = this.getIndexOfHero(heroX);
        int posY = this.getIndexOfHero(heroY);

        return this.getConnection(posX, posY);
    }

    public int getConnection(int x, int y) {
        try {
            // team_graph is a symmetrical matrix, we only load the down-left part
            return y > x ? this.matrix[y][x] : this.matrix[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not in range", e);
        }
    }

    /**
     * @param hero hero name
     * @return all connections to other heroes, sorted best to worst
     * @throws GraphException when hero not found
     */
    public List<Scored<String>> getSortedConnections(String hero) throws GraphException {
        return this.getSortedConnections(this.getIndexOfHero(hero));
    }

    /**
     * @param pos hero position in matrix
     * @return all connections to other heroes, sorted best to worst
     */
    public List<Scored<String>> getSortedConnections(int pos) {
        if (pos < 0 || pos >= this.matrix.length) {
            throw new IllegalArgumentException("not in range");
        }

        // list heroes connection to input hero
        List<Integer> connections = IntStream.range(0, this.matrix.length)
                .map(teammatePos -> this.getConnection(teammatePos, pos))
                .boxed().collect(Collectors.toList());
        connections.remove(pos);

        // list other heroes from name list
        List<String> otherHeroesNames = new ArrayList<>(this.heroesList).stream()
                .map(Hero::getName).collect(Collectors.toList());
        otherHeroesNames.remove(pos);

        return GraphHelper.zipAndSort(otherHeroesNames, connections);
    }
}
