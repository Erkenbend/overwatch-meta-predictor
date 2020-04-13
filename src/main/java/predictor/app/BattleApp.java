package predictor.app;

import predictor.graph.EnemyGraph;
import predictor.graph.TeamGraph;
import predictor.strategy.*;
import predictor.team.Hero;
import predictor.team.Team;
import predictor.team.TeamFactory;
import predictor.team.TeamHelper;
import predictor.util.ResourceReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This "main" class is for development purpose only
 * <p>
 * Use for testing how different strategies behave against one another
 */
public class BattleApp {
    public static void main(String[] args) throws Exception {
        // Read from files
        List<Hero> heroesList = ResourceReader.getHeroesList();
        TeamGraph teamGraph = ResourceReader.getTeamGraph(heroesList);
        EnemyGraph enemyGraph = ResourceReader.getEnemyGraph(heroesList);

        // Configure hero pools
        // List<String> bannedHeroesNames = Arrays.asList("Reinhardt", "McCree", "Widowmaker", "Moira");
        // List<String> bannedHeroesNames = new ArrayList<>();
        // List<String> bannedHeroesNames = Arrays.asList("Wrecking Ball", "McCree", "Mei", "Brigitte");
        List<String> bannedHeroesNames = Arrays.asList("D.Va", "Reaper", "Sombra", "Ana");

        // Create initial teams
        int teamSize = 6;
        Team team1 = TeamFactory.createTeam(heroesList, teamSize, bannedHeroesNames);
        System.out.println(String.format("Initial Team 1: %s", team1));
        Team team2 = TeamFactory.createTeam(heroesList, teamSize, bannedHeroesNames);
        System.out.println(String.format("Initial Team 2: %s", team2));


        // Initiate Strategies
        Map<String, TeamImprovementStrategy> strategyMap = new HashMap<>();
        strategyMap.put("DoNothing", new DoNothingStrategy());
        strategyMap.put("OneRandomChangeUntilBetterDominance", new OneRandomChangeUntilBetterDominanceStrategy());
        strategyMap.put("OneRandomChangeUntilBetterSynergy", new OneRandomChangeUntilBetterSynergyStrategy());
        strategyMap.put("ReplaceLeastSynergyWithBestSynergy", new ReplaceLeastSynergyWithBestSynergyStrategy());
        strategyMap.put("ReplaceMostDominatedWithMostDominant", new ReplaceMostDominatedWithMostDominantStrategy());
        strategyMap.put("BestSynergyReplacementWithoutDominanceDecrease", new BestSynergyReplacementWithoutDominanceDecreaseStrategy());
        strategyMap.put("BestDominanceReplacementWithoutSynergyDecrease", new BestDominanceReplacementWithoutSynergyDecreaseStrategy());
        strategyMap.put("RandomHeroBestSwitch", new RandomHeroBestSwitchStrategy());
        for (TeamImprovementStrategy strategy : strategyMap.values()) {
            strategy.initialize(heroesList, teamGraph, enemyGraph, bannedHeroesNames);
        }

        // Choose Strategies
        team1.setImprovementStrategy(strategyMap.get("RandomHeroBestSwitch"));
        team2.setImprovementStrategy(strategyMap.get("RandomHeroBestSwitch"));

        // Incrementally improve: give each team a copy of the enemy team to study and apply team strategy
        int maxNbRounds = 100;
        for (int round = 1; round <= maxNbRounds; round++) {
            System.out.println(String.format("\nIteration %s", round));

            team1.improveAgainst(Team.copyOf(team2));
            System.out.println(String.format("Team 1: %s", team1));
            TeamHelper.checkTeamValidity(team1.getHeroesList(), bannedHeroesNames);
            printStats(team1, team2, teamGraph, enemyGraph);

            team2.improveAgainst(Team.copyOf(team1));
            System.out.println(String.format("Team 2: %s", team2));
            TeamHelper.checkTeamValidity(team2.getHeroesList(), bannedHeroesNames);
            printStats(team1, team2, teamGraph, enemyGraph);
        }
    }

    private static void printStats(Team team1, Team team2, TeamGraph teamGraph, EnemyGraph enemyGraph) {
        System.out.println(String.format("Team 1 synergy: %s, Team 2 synergy: %s, Dominance Team 1 over Team 2: %s",
                team1.getTotalSynergy(teamGraph), team2.getTotalSynergy(teamGraph), team1.getTotalDominance(team2, enemyGraph)));
    }
}
