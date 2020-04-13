package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Team;

import java.util.List;

/**
 * This strategy is based on ReplaceMostDominatedWithMostDominantStrategy. It does
 * the same than its "parent" strategy first, then checks that the overall synergy
 * does not decrease.
 */
public class BestDominanceReplacementWithoutSynergyDecreaseStrategy extends TeamImprovementStrategyWithChild {

    private static final int MAX_NB_TRIES = 10;
    private static final int ALLOWED_MARGIN = 2;  // how much synergy decrease is allowed for better dominance

    public BestDominanceReplacementWithoutSynergyDecreaseStrategy() {
        this.childStrategy = new ReplaceMostDominatedWithMostDominantStrategy();
    }

    @Override
    public List<Hero> nextIteration(Team myTeam, Team enemyTeam) {
        int initialSynergy = myTeam.getTotalSynergy(teamGraph);
        List<Hero> newProposition;
        int newSynergy;

        int nbTries = 0;
        do {
            newProposition = this.childStrategy.nextIteration(myTeam, enemyTeam);  // this does not always give the same result
            Team newTeam = new Team(newProposition);
            newSynergy = newTeam.getTotalSynergy(teamGraph);
        } while (nbTries++ < MAX_NB_TRIES && newSynergy < initialSynergy - ALLOWED_MARGIN);

        if (nbTries != MAX_NB_TRIES) {
            return newProposition;
        }

        // if we did not manage to find a better option, do not change team
        return myTeam.getHeroesList();
    }
}
