package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This strategy makes a random change to
 */
public class OneRandomChangeUntilBetterDominanceStrategy extends TeamImprovementStrategy {

    @Override
    public List<Hero> nextIteration(Team myTeam, final Team enemyTeam) {
        final int initialDominance = myTeam.getTotalDominance(enemyTeam, this.enemyGraph);
        Random rd = new Random();
        int nbTries = 0;
        final int maxNbTries = 50;
        int newDominance;
        List<Hero> teamHeroesListCopy;

        do {
            // Pick a random hero and remove it
            teamHeroesListCopy = new ArrayList<>(myTeam.getHeroesList());
            Hero picked = teamHeroesListCopy.get(rd.nextInt(teamHeroesListCopy.size()));
            teamHeroesListCopy.remove(picked);

            // Find replacement among other heroes with same role, preventing duplicates
            List<Hero> otherHeroesWithSameRole = heroesList.stream()
                    .filter(h -> h.getRole().equals(picked.getRole()) && !h.getName().equals(picked.getName()) && !bannedHeroesNames.contains(h.getName()))
                    .collect(Collectors.toList());
            Hero replacement;
            do {
                Collections.shuffle(otherHeroesWithSameRole);
                replacement = otherHeroesWithSameRole.get(0);
            } while (teamHeroesListCopy.contains(replacement));
            teamHeroesListCopy.add(replacement);

            // Recompute dominance
            newDominance = new Team(teamHeroesListCopy).getTotalDominance(enemyTeam, this.enemyGraph);
        } while (newDominance <= initialDominance && ++nbTries < maxNbTries);

        if (nbTries == maxNbTries) {
            // No better team found
            return myTeam.getHeroesList();
        }

        return teamHeroesListCopy;
    }
}
