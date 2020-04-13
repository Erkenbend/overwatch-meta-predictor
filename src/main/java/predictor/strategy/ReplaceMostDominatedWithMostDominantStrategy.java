package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Role;
import predictor.util.Scored;
import predictor.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReplaceMostDominatedWithMostDominantStrategy extends TeamImprovementStrategy {
    @Override
    public List<Hero> nextIteration(Team myTeam, final Team enemyTeam) {
        List<Hero> teamHeroesList = new ArrayList<>(myTeam.getHeroesList());
        List<String> teamHeroesNameList = teamHeroesList.stream().map(Hero::getName).collect(Collectors.toList());
        List<Scored<Hero>> dominancePerHero = enemyTeam.getDominancePerHero(heroesList, enemyGraph);
        Collections.reverse(dominancePerHero);  // My hero that is most dominated by the enemy should come up first

        // Search for most dominated hero in team and remove it
        Hero mostDominatedHero = null;
        int mostDominatedHeroIndexInTeam;
        for (Scored<Hero> dominanceOverEnemyTeam : dominancePerHero) {
            if (teamHeroesNameList.contains(dominanceOverEnemyTeam.getObj().getName())) {
                mostDominatedHero = dominanceOverEnemyTeam.getObj();
                mostDominatedHeroIndexInTeam = teamHeroesNameList.indexOf(mostDominatedHero.getName());
                teamHeroesList.remove(mostDominatedHeroIndexInTeam);
                break;
            }
        }

        if (mostDominatedHero == null) {
            throw new RuntimeException("Could not most dominated hero");
        }

        // Add most dominant hero with corresponding role
        Role roleToReplace = mostDominatedHero.getRole();
        Collections.reverse(dominancePerHero);
        for (Scored<Hero> dominanceOverEnemyTeam : dominancePerHero) {
            Hero currentHero = dominanceOverEnemyTeam.getObj();
            if (currentHero.getRole().equals(roleToReplace) &&
                    !teamHeroesList.contains(currentHero) &&
                    !bannedHeroesNames.contains(currentHero.getName())
            ) {
                teamHeroesList.add(currentHero);
                break;
            }
        }

        return teamHeroesList;
    }
}
