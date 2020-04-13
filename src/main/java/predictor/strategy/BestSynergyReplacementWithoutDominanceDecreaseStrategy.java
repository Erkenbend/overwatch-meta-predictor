package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Team;

import java.util.List;

/**
 * This strategy is based on ReplaceLeastSynergyWithBestSynergyStrategy. It does
 * the same than its "parent" strategy first, then checks that the overall dominance
 * over the other team does not decrease.
 */
public class BestSynergyReplacementWithoutDominanceDecreaseStrategy extends TeamImprovementStrategyWithChild {

    private static final int MAX_NB_TRIES = 10;
    private static final int ALLOWED_MARGIN = 2;  // how much dominance decrease is allowed for better synergy

    public BestSynergyReplacementWithoutDominanceDecreaseStrategy() {
        this.childStrategy = new ReplaceLeastSynergyWithBestSynergyStrategy();
    }

    @Override
    public List<Hero> nextIteration(Team myTeam, Team enemyTeam) {
        int initialDominance = myTeam.getTotalDominance(enemyTeam, enemyGraph);
        List<Hero> newProposition;
        int newDominance;

        int nbTries = 0;
        do {
            newProposition = this.childStrategy.nextIteration(myTeam, enemyTeam);  // this does not always give the same result
            Team newTeam = new Team(newProposition);
            newDominance = newTeam.getTotalDominance(enemyTeam, enemyGraph);
        } while (nbTries++ < MAX_NB_TRIES && newDominance < initialDominance - ALLOWED_MARGIN);

        if (nbTries != MAX_NB_TRIES) {
            return newProposition;
        }

        // if we did not manage to find a better option, do not change team
        return myTeam.getHeroesList();
    }
}
