package predictor.app;

import org.apache.commons.cli.*;
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
 * This is the main class of the JAR-Package.
 * <p>
 * Use the available parameters to get two teams to incrementally
 * improve against one another.
 */
public class ConsoleApp {

    private static final String OPTION_HELP = "help";
    private static final String OPTION_NB_ITERATIONS = "nb-iterations";
    private static final String OPTION_BANNED_HEROES = "ban-hero";
    private static final String OPTION_STRATEGY_TEAM_1 = "strategy-team-1";
    private static final String OPTION_STRATEGY_TEAM_2 = "strategy-team-2";
    private static final String OPTION_QUIET = "quiet";

    public static final String DEFAULT_NB_ITERATIONS = "100";

    public static final int TEAM_SIZE = 6;

    public static void main(String[] args) throws Exception {
        Options options = configureOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            // Read command-line options
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(OPTION_HELP)) {
                formatter.printHelp(" ", options);
                return;
            }

            boolean quietMode = cmd.hasOption(OPTION_QUIET);

            int nbIterations = Integer.parseInt(cmd.getOptionValue(OPTION_NB_ITERATIONS, DEFAULT_NB_ITERATIONS));

            String[] bannedHeroesArray = cmd.getOptionValues(OPTION_BANNED_HEROES);
            if (bannedHeroesArray == null) {
                bannedHeroesArray = new String[0];
            }
            List<String> bannedHeroesNames = Arrays.asList(bannedHeroesArray);

            String strategyNameTeam1 = cmd.getOptionValue(OPTION_STRATEGY_TEAM_1);
            String strategyNameTeam2 = cmd.getOptionValue(OPTION_STRATEGY_TEAM_2);

            // Read from files
            List<Hero> heroesList = ResourceReader.getHeroesList();
            TeamGraph teamGraph = ResourceReader.getTeamGraph(heroesList);
            EnemyGraph enemyGraph = ResourceReader.getEnemyGraph(heroesList);

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
            TeamImprovementStrategy strategyTeam1 = strategyMap.get(strategyNameTeam1);
            if (strategyTeam1 == null) {
                throw new Exception(String.format("Invalid strategy name for team 1: %s", strategyNameTeam1));
            }

            TeamImprovementStrategy strategyTeam2 = strategyMap.get(strategyNameTeam2);
            if (strategyTeam2 == null) {
                throw new Exception(String.format("Invalid strategy name for team 2: %s", strategyNameTeam2));
            }

            // Create initial teams
            Team team1 = TeamFactory.createTeam(heroesList, TEAM_SIZE, bannedHeroesNames);
            team1.setImprovementStrategy(strategyTeam1);
            if (!quietMode) {
                System.out.println(String.format("Initial Team 1: %s", team1));
                System.out.println(String.format("Using strategy: %s", strategyNameTeam1));
            }

            Team team2 = TeamFactory.createTeam(heroesList, TEAM_SIZE, bannedHeroesNames);
            team2.setImprovementStrategy(strategyTeam2);
            if (!quietMode) {
                System.out.println(String.format("Initial Team 2: %s", team2));
                System.out.println(String.format("Using strategy: %s", strategyNameTeam2));
            }

            // Incrementally improve: give each team a copy of the enemy team to study and apply team strategy
            for (int round = 1; round <= nbIterations; round++) {
                if (!quietMode) {
                    System.out.println(String.format("\nIteration %s", round));
                }

                team1.improveAgainst(Team.copyOf(team2));
                TeamHelper.checkTeamValidity(team1.getHeroesList(), bannedHeroesNames);

                team2.improveAgainst(Team.copyOf(team1));
                TeamHelper.checkTeamValidity(team2.getHeroesList(), bannedHeroesNames);

                if (!quietMode) {
                    printTeamsAndStats(team1, team2, teamGraph, enemyGraph);
                }
            }

            // Print final results
            System.out.println("\n----------------------------\nFINAL RESULTS:");
            printTeamsAndStats(team1, team2, teamGraph, enemyGraph);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp(" ", options);
            System.exit(1);
        }

    }

    private static Options configureOptions() {
        Options options = new Options();

        Option help = new Option("h", OPTION_HELP, false, "Print this help and exit");
        help.setRequired(false);
        options.addOption(help);

        Option nbIterations = new Option("n", OPTION_NB_ITERATIONS, true, "Number of iterations");
        nbIterations.setRequired(false);
        options.addOption(nbIterations);

        Option banHero = new Option("bh", OPTION_BANNED_HEROES, true, "Name of hero to ban (repeat as much as necessary)");
        banHero.setRequired(false);
        options.addOption(banHero);

        Option strategyTeam1 = new Option("s1", OPTION_STRATEGY_TEAM_1, true, "Strategy employed by team 1");
        strategyTeam1.setRequired(true);
        options.addOption(strategyTeam1);

        Option strategyTeam2 = new Option("s2", OPTION_STRATEGY_TEAM_2, true, "Strategy employed by team 2");
        strategyTeam2.setRequired(true);
        options.addOption(strategyTeam2);

        Option quiet = new Option("q", OPTION_QUIET, false, "Quiet mode");
        quiet.setRequired(false);
        options.addOption(quiet);

        return options;
    }

    private static void printTeamsAndStats(Team team1, Team team2, TeamGraph teamGraph, EnemyGraph enemyGraph) {
        System.out.println(String.format("Team 1: %s", team1));
        System.out.println(String.format("Team 2: %s", team2));
        System.out.println(String.format("--> Team 1 synergy: %s, Team 2 synergy: %s, Dominance Team 1 over Team 2: %s",
                team1.getTotalSynergy(teamGraph), team2.getTotalSynergy(teamGraph), team1.getTotalDominance(team2, enemyGraph)));
    }
}
