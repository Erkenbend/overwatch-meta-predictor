package predictor.util;

import org.junit.jupiter.api.Test;
import predictor.graph.EnemyGraph;
import predictor.graph.TeamGraph;
import predictor.team.Hero;
import predictor.team.Role;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceReaderTest {

    @Test
    void getHeroesList() {
        List<Hero> heroesList = assertDoesNotThrow(ResourceReader::getHeroesList);
        assertNotNull(heroesList);
        assertEquals(4, heroesList.size());
        assertEquals(new Hero("Firstdamage", Role.DAMAGE), heroesList.get(0));
        assertEquals(new Hero("Seconddamage", Role.DAMAGE), heroesList.get(1));
        assertEquals(new Hero("Support", Role.SUPPORT), heroesList.get(2));
        assertEquals(new Hero("Tank", Role.TANK), heroesList.get(3));
    }

    @Test
    void getTeamGraph() {
        List<Hero> heroesList = assertDoesNotThrow(ResourceReader::getHeroesList);
        TeamGraph teamGraph = assertDoesNotThrow(() -> ResourceReader.getTeamGraph(heroesList));
        assertEquals(1, teamGraph.getConnection(0, 3));
        assertEquals(-1, teamGraph.getConnection(1, 3));
        assertEquals(2, teamGraph.getConnection(2, 1));
    }

    @Test
    void getEnemyGraph() {
        List<Hero> heroesList = assertDoesNotThrow(ResourceReader::getHeroesList);
        EnemyGraph enemyGraph = assertDoesNotThrow(() -> ResourceReader.getEnemyGraph(heroesList));
        assertEquals(2, enemyGraph.getDominance(0, 2));
        assertEquals(1, enemyGraph.getDominance(1, 0));
        assertEquals(-1, enemyGraph.getDominance(2, 3));
    }
}
