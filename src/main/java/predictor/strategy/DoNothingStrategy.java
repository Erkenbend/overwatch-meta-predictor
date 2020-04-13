package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Team;

import java.util.List;

/**
 * This strategy sticks with the original team
 */
public class DoNothingStrategy extends TeamImprovementStrategy {

    @Override
    public List<Hero> nextIteration(Team myTeam, final Team enemyTeam) {
        // do literally nothing
        return myTeam.getHeroesList();
    }
}
