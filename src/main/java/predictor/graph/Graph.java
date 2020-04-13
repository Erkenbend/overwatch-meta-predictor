package predictor.graph;

import predictor.team.Hero;
import predictor.exception.GraphException;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public abstract class Graph {
    protected List<Hero> heroesList;
    protected int[][] matrix;

    protected Graph(List<Hero> heroesList) {
        this.heroesList = heroesList;
        int size = heroesList.size();
        this.matrix = new int[size][size];
    }

    public void set(int x, int y, int value) {
        if (y > x) {
            throw new IllegalArgumentException("y should be lower than x");
        }

        try {
            this.matrix[x][y] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not in range", e);
        }
    }

    protected int getIndexOfHero(String heroName) throws GraphException {
        ListIterator<Hero> heroesListIterator = this.heroesList.listIterator();
        while (heroesListIterator.hasNext()) {
            if (heroesListIterator.next().getName().equals(heroName)) {
                return heroesListIterator.previousIndex();
            }
        }

        throw new GraphException(String.format("Hero %s not in heroes names list", heroName));
    }

    @Override
    public String toString() {
        StringBuilder matrixAsStringBuilder = new StringBuilder();
        for (int i = 0; i < this.matrix.length; i++) {
            matrixAsStringBuilder.append(
                    Arrays.stream(this.matrix[i])
                            .limit(i + 1)  // take lower-left triangle
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining(",")) // TODO replace with config separator
            );
            matrixAsStringBuilder.append("\n");
        }

        return String.format("Graph {\nheroesNames=%s,\nmatrix=\n%s}", heroesList, matrixAsStringBuilder.toString());
    }
}
