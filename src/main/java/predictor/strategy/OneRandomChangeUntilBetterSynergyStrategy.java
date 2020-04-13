package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class OneRandomChangeUntilBetterSynergyStrategy extends TeamImprovementStrategy {

    @Override
    public List<Hero> nextIteration(Team myTeam, final Team enemyTeam) {
        final int initialSynergy = myTeam.getTotalSynergy(this.teamGraph);
        Random rd = new Random();
        int nbTries = 0;
        final int maxNbTries = 50;
        int newSynergy;
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

            // Recompute synergy
            newSynergy = new Team(teamHeroesListCopy).getTotalSynergy(this.teamGraph);
        } while (newSynergy <= initialSynergy && ++nbTries < maxNbTries);

        if (nbTries == maxNbTries) {
            // No better team found
            return myTeam.getHeroesList();
        }

        return teamHeroesListCopy;
    }
}
