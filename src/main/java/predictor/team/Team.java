package predictor.team;

import predictor.exception.GraphException;
import predictor.graph.EnemyGraph;
import predictor.graph.GraphHelper;
import predictor.graph.TeamGraph;
import predictor.strategy.TeamImprovementStrategy;
import predictor.util.Scored;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Team {
    private List<Hero> heroesList;
    private TeamImprovementStrategy improvementStrategy;

    public Team(List<Hero> heroesList) {
        this.heroesList = heroesList;
    }

    public int getTotalSynergy(TeamGraph teamGraph) {
        return GraphHelper.getPossiblePairs(0, this.heroesList.size()).stream()
                .mapToInt(pair -> {
                    try {
                        // System.out.println(String.format("%s with %s: %d", this.heroesList.get(pair[0]).getName(), this.heroesList.get(pair[1]).getName(), teamGraph.getConnection(this.heroesList.get(pair[0]).getName(), this.heroesList.get(pair[1]).getName())));
                        return teamGraph.getConnection(this.heroesList.get(pair[0]).getName(), this.heroesList.get(pair[1]).getName());
                    } catch (GraphException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();
    }

    public int getTotalDominance(Team over, EnemyGraph enemyGraph) {
        return GraphHelper.getPossiblePairsWithRepetition(0, this.heroesList.size()).stream()
                .mapToInt(match -> {
                    try {
                        // System.out.println(String.format("%s over %s: %d", this.heroesList.get(match[0]).getName(), over.heroesList.get(match[1]).getName(), enemyGraph.getDominance(this.heroesList.get(match[0]).getName(), over.heroesList.get(match[1]).getName())));
                        return enemyGraph.getDominance(this.heroesList.get(match[0]).getName(), over.heroesList.get(match[1]).getName());
                    } catch (GraphException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();
    }

    public int getSynergyTo(String hero, TeamGraph teamGraph) {
        return this.heroesList.stream().mapToInt(
                teamHero -> {
                    try {
                        return teamGraph.getConnection(teamHero.getName(), hero);
                    } catch (GraphException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();
    }

    /**
     * Get synergy of each hero on the list with the team, sorted ascending
     *
     * @param heroes
     * @param teamGraph
     * @return
     */
    public List<Scored<Hero>> getSynergiesPerHero(List<Hero> heroes, TeamGraph teamGraph) {
        List<Scored<Hero>> synergies = new ArrayList<>();
        for (Hero hero : heroes) {
            synergies.add(new Scored<>(hero, this.getSynergyTo(hero.getName(), teamGraph)));
        }

        Collections.shuffle(synergies);
        Collections.sort(synergies);
        return synergies;
    }

    public int getDominanceOver(String hero, EnemyGraph enemyGraph) {
        return this.heroesList.stream().mapToInt(
                teamHero -> {
                    try {
                        return enemyGraph.getDominance(teamHero.getName(), hero);
                    } catch (GraphException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();
    }

    /**
     * Get dominance of the team over each hero on the list, sorted ascending
     *
     * @param heroes
     * @param enemyGraph
     * @return
     */
    public List<Scored<Hero>> getDominancePerHero(List<Hero> heroes, EnemyGraph enemyGraph) {
        List<Scored<Hero>> dominances = new ArrayList<>();
        heroes.forEach(hero ->
                dominances.add(new Scored<>(hero, this.getDominanceOver(hero.getName(), enemyGraph)))
        );

        Collections.shuffle(dominances);
        Collections.sort(dominances);
        return dominances;
    }

    public void improveAgainst(final Team enemyTeam) {
        this.heroesList = this.improvementStrategy.nextIteration(this, enemyTeam);
    }

    @Override
    public String toString() {
        List<Hero> orderedHeroesList = new ArrayList<>();
        orderedHeroesList.addAll(TeamHelper.getSortedHeroListForRole(heroesList, Role.TANK));
        orderedHeroesList.addAll(TeamHelper.getSortedHeroListForRole(heroesList, Role.DAMAGE));
        orderedHeroesList.addAll(TeamHelper.getSortedHeroListForRole(heroesList, Role.SUPPORT));
        return String.format("Team{heroesList=%s}", orderedHeroesList);
    }

    public void setImprovementStrategy(TeamImprovementStrategy improvementStrategy) {
        this.improvementStrategy = improvementStrategy;
    }

    public List<Hero> getHeroesList() {
        return this.heroesList;
    }

    public static Team copyOf(Team team) {
        return new Team(new ArrayList<>(team.heroesList));
    }

    /**
     * Propose a switch for a given position in team, according to synergy and dominance gain
     *
     * @param position             where in the team should the switch happen
     * @param allHeroesList
     * @param teamGraph
     * @param enemyGraph
     * @param bannedHeroesNames
     * @param enemyTeam
     * @param enforceHeroLimits    if true, no hero repetitions in team
     * @param enforceRoleLimits    if true, 2-2-2 role lock
     * @param totalScoreCalculator how should the total score of the replacement
     *                             be calculated, based on synergy and dominance
     * @return
     */
    public Hero getBestSwitchForPosition(
            int position,
            List<Hero> allHeroesList, TeamGraph teamGraph, EnemyGraph enemyGraph,
            List<String> bannedHeroesNames,
            Team enemyTeam, boolean enforceHeroLimits, boolean enforceRoleLimits,
            BinaryOperator<Integer> totalScoreCalculator
    ) {
        Hero heroToSwitch = this.getHeroesList().get(position);
        Role roleToSwitch = heroToSwitch.getRole();
//        int currentSynergyToTeam = this.getSynergyTo(heroToSwitch.getName(), teamGraph);
//        int currentDominanceOverEnemyTeam = -enemyTeam.getDominanceOver(heroToSwitch.getName(), enemyGraph);
//        System.out.println(String.format("%s: S%s D%s", heroToSwitch.getName(), currentSynergyToTeam, currentDominanceOverEnemyTeam));

        this.heroesList.remove(position);  // temporarily remove hero for calculations
        Hero switchTo;
        try {
            List<Scored<Hero>> candidatesSynergyToTeam = allHeroesList.stream()
                    .filter(h -> !enforceRoleLimits || h.getRole() == roleToSwitch)
                    .filter(h -> !enforceHeroLimits || !this.heroesList.contains(h))
                    .filter(h -> !bannedHeroesNames.contains(h.getName()))
                    .map(h -> new Scored<>(h, this.getSynergyTo(h.getName(), teamGraph)))
                    .collect(Collectors.toList());

            List<Scored<Hero>> candidatesDominanceOverEnemyTeam = allHeroesList.stream()
                    .filter(h -> !enforceRoleLimits || h.getRole() == roleToSwitch)
                    .filter(h -> !enforceHeroLimits || !this.heroesList.contains(h))
                    .filter(h -> !bannedHeroesNames.contains(h.getName()))
                    .map(h -> new Scored<>(h, -enemyTeam.getDominanceOver(h.getName(), enemyGraph)))
                    .collect(Collectors.toList());

            // Both lists have the same hero ordering so we can zip them without problems
            List<Scored<Hero>> globalSortedCandidates = IntStream.range(0, candidatesSynergyToTeam.size())
                    .mapToObj(i -> new Scored<>(
                                    candidatesSynergyToTeam.get(i).getObj(),
                                    totalScoreCalculator.apply(
                                            candidatesSynergyToTeam.get(i).getScore(),
                                            candidatesDominanceOverEnemyTeam.get(i).getScore()
                                    )
                            )
                    )
                    .collect(Collectors.toList());
            Collections.shuffle(globalSortedCandidates);  // shuffle to avoid alphabetically sorted heroes
            Collections.sort(globalSortedCandidates);
            Collections.reverse(globalSortedCandidates);

//            System.out.println(globalSortedCandidates);
            switchTo = globalSortedCandidates.get(0).getObj();
//            System.out.println(String.format("%s: %s", switchTo.getName(), globalSortedCandidates.get(0).getScore()));
        } finally {
            this.heroesList.add(position, heroToSwitch);  // re-add hero to leave team intact after operation
        }

        return switchTo;
    }

    public Hero getBestSwitchForPosition(
            int position,
            List<Hero> allHeroesList, TeamGraph teamGraph, EnemyGraph enemyGraph,
            List<String> bannedHeroesNames,
            Team enemyTeam
    ) {
        return this.getBestSwitchForPosition(position,
                allHeroesList, teamGraph, enemyGraph, bannedHeroesNames, enemyTeam,
                true, true, Integer::sum);
    }
}
