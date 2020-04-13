package predictor.app;

import predictor.graph.EnemyGraph;
import predictor.graph.TeamGraph;
import predictor.team.Hero;
import predictor.team.Team;
import predictor.team.TeamFactory;
import predictor.util.ResourceReader;

import java.util.List;

/**
 * This "main" class is for development purpose only
 * <p>
 * It demonstrates how the basic relationships in the TeamGraph
 * and EnemyGraph work
 */
public class DevApp {
    public static void main(String[] args) throws Exception {
        List<Hero> heroesList = ResourceReader.getHeroesList();
        // heroesList.forEach(System.out::println);

        TeamGraph teamGraph = ResourceReader.getTeamGraph(heroesList);
        // System.out.println(teamGraph.toString());
        System.out.println(teamGraph.getConnection("Pharah", "Mercy"));
        System.out.println(teamGraph.getConnection("Mercy", "Pharah"));

        EnemyGraph enemyGraph = ResourceReader.getEnemyGraph(heroesList);
        // System.out.println(enemyGraph.toString());
        System.out.println(enemyGraph.getDominance("Widowmaker", "Pharah"));
        System.out.println(enemyGraph.getDominance("Pharah", "Widowmaker"));

        System.out.println(teamGraph.getSortedConnections("Pharah"));
        System.out.println(enemyGraph.getSortedCounters("Pharah"));

        for (int i = 0; i < 10; i++) {
            Team team = TeamFactory.createTeamWithRepetitionAllowed(heroesList, 6);
            System.out.println(team);
        }

        // List<Hero> heroes = HeroFactory.createHeroes(heroesList, teamGraph, enemyGraph);
        for (int i = 0; i < 10; i++) {
            Team team = TeamFactory.createTeam(heroesList, 6);
            System.out.println(team);
            System.out.println(String.format("Team synergy: %s", team.getTotalSynergy(teamGraph)));
        }

        // List<Hero> heroes = HeroFactory.createHeroes(heroesList, teamGraph, enemyGraph);
        for (int i = 0; i < 10; i++) {
            Team team1 = TeamFactory.createTeam(heroesList, 6);
            System.out.println(team1);
            Team team2 = TeamFactory.createTeam(heroesList, 6);
            System.out.println(team2);
            System.out.println(String.format("Team dominance: %s", team1.getTotalDominance(team2, enemyGraph)));
        }
    }
}
