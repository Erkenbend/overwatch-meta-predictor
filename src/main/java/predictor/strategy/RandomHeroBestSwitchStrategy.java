package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This strategy makes use of the `getBestSwitchForPosition` method
 * in `predictor.team.Team` to make the best possible change for
 * one random position in the team. It uses a custom function to
 * determine what is "best".
 */
public class RandomHeroBestSwitchStrategy extends TeamImprovementStrategy {

    @Override
    public List<Hero> nextIteration(Team myTeam, Team enemyTeam) {
        List<Hero> heroList = new ArrayList<>(myTeam.getHeroesList());
        Random rd = new Random();
        int positionToSwitch = rd.nextInt(heroList.size());

        Hero switchTo = myTeam.getBestSwitchForPosition(
                positionToSwitch,
                this.heroesList, this.teamGraph, this.enemyGraph, this.bannedHeroesNames,
                enemyTeam, true, true,
                (synergy, dominance) -> 2 * synergy + 3 * dominance
        );

        heroList.remove(positionToSwitch);
        heroList.add(positionToSwitch, switchTo);

        return heroList;
    }
}
