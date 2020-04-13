package predictor.app;

import predictor.graph.EnemyGraph;
import predictor.graph.TeamGraph;
import predictor.team.Hero;
import predictor.team.Team;
import predictor.team.TeamFactory;
import predictor.util.ResourceReader;

import java.util.ArrayList;
import java.util.List;

/**
 * This "main" class is for development purpose only
 * <p>
 * It demonstrates the "switchTo" functionality of the Team class
 */
public class SwitchApp {
    public static void main(String[] args) throws Exception {
        // Read from files
        List<Hero> heroesList = ResourceReader.getHeroesList();
        TeamGraph teamGraph = ResourceReader.getTeamGraph(heroesList);
        EnemyGraph enemyGraph = ResourceReader.getEnemyGraph(heroesList);

        // Configure hero pools
        // List<String> bannedHeroesNames = Arrays.asList("Reinhardt", "McCree", "Widowmaker", "Moira");
        List<String> bannedHeroesNames = new ArrayList<>();

        // Create initial teams
        int teamSize = 6;
        Team team1 = TeamFactory.createTeam(heroesList, teamSize, bannedHeroesNames);
        System.out.println(String.format("Initial Team 1: %s", team1));
        Team team2 = TeamFactory.createTeam(heroesList, teamSize, bannedHeroesNames);
        System.out.println(String.format("Initial Team 2: %s", team2));

        for (int i = 0; i < teamSize; i++) {  // I'm in position i in team 1, what should I switch to?
            Hero switchTo = team1.getBestSwitchForPosition(i,
                    heroesList, teamGraph, enemyGraph, bannedHeroesNames, team2);
            String heroToSwitchName = team1.getHeroesList().get(i).getName();
            String switchToName = switchTo.getName();
            if (heroToSwitchName.equals(switchToName)) {
                System.out.println(String.format(
                        "Position %d: %s should not switch", i, heroToSwitchName
                ));
            } else {
                System.out.println(String.format(
                        "Position %d: %s should switch to %s", i, heroToSwitchName, switchToName
                ));
            }
        }
    }
}
