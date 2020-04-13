package predictor.strategy;

import predictor.team.Hero;
import predictor.team.Role;
import predictor.util.Scored;
import predictor.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReplaceLeastSynergyWithBestSynergyStrategy extends TeamImprovementStrategy {
    @Override
    public List<Hero> nextIteration(Team myTeam, final Team enemyTeam) {
        List<Hero> teamHeroesList = new ArrayList<>(myTeam.getHeroesList());
        List<String> teamHeroesNameList = teamHeroesList.stream().map(Hero::getName).collect(Collectors.toList());
        List<Scored<Hero>> synergiesPerHero = myTeam.getSynergiesPerHero(heroesList, teamGraph);

        // Search for least synergy in team and remove it
        Hero heroWithLeastSynergy = null;
        int heroWithLeastSynergyIndexInTeam;
        for (Scored<Hero> synergyWithTeam : synergiesPerHero) {
            if (teamHeroesNameList.contains(synergyWithTeam.getObj().getName())) {
                heroWithLeastSynergy = synergyWithTeam.getObj();
                heroWithLeastSynergyIndexInTeam = teamHeroesNameList.indexOf(heroWithLeastSynergy.getName());
                teamHeroesList.remove(heroWithLeastSynergyIndexInTeam);
                break;
            }
        }

        if (heroWithLeastSynergy == null) {
            throw new RuntimeException("Could not find hero with least synergy");
        }

        // Add best synergy with corresponding role
        Role roleToReplace = heroWithLeastSynergy.getRole();
        Collections.reverse(synergiesPerHero);
        for (Scored<Hero> synergyWithTeam : synergiesPerHero) {
            Hero currentHero = synergyWithTeam.getObj();
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
