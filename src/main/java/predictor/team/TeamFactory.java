package predictor.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TeamFactory {

    public static Team createTeam(List<Hero> heroesList, int teamSize) {
        return createTeam(heroesList, teamSize, true, new ArrayList<>());
    }

    public static Team createTeam(List<Hero> heroesList, int teamSize, List<String> bannedHeroesNames) {
        return createTeam(heroesList, teamSize, true, bannedHeroesNames);
    }

    public static Team createTeam(List<Hero> heroesList, int teamSize, boolean withRoleLimit, List<String> bannedHeroesNames) {
        return withRoleLimit ?
                createTeamWithRoleLimits(heroesList, teamSize, bannedHeroesNames, 2, 2, 2) :
                createTeamWithoutRepetition(heroesList, teamSize, bannedHeroesNames);
    }

    public static Team createTeamWithRepetitionAllowed(List<Hero> heroesList, int teamSize) {
        return createTeamWithRepetitionAllowed(heroesList, teamSize, new ArrayList<>());
    }

    public static Team createTeamWithRepetitionAllowed(List<Hero> heroesList, int teamSize, List<String> bannedHeroesNames) {
        Random rd = new Random();
        List<Hero> resultList = new ArrayList<>();
        for (int i = 0; i < teamSize; i++) {
            Hero newHero;
            do {
                newHero = heroesList.get(rd.nextInt(heroesList.size()));
            } while (bannedHeroesNames.contains(newHero.getName()));
            resultList.add(newHero);
        }

        return new Team(resultList);
    }

    private static Team createTeamWithoutRepetition(List<Hero> heroesList, int teamSize, List<String> bannedHeroesNames) {
        return new Team(getRandomWithoutRepetition(
                heroesList.stream().filter(h -> !bannedHeroesNames.contains(h.getName())).collect(Collectors.toList()),
                teamSize
        ));
    }


    private static Team createTeamWithRoleLimits(List<Hero> heroesList, int teamSize, List<String> bannedHeroesNames,
                                                 int nbTanks, int nbDamage, int nbSupport
    ) {
        if (teamSize != nbTanks + nbDamage + nbSupport) {
            throw new IllegalArgumentException("Team size should equal nbTanks + nbDamage + nbSupport");
        }

        List<Hero> tanks = heroesList.stream().filter(h -> h.getRole().equals(Role.TANK) && !bannedHeroesNames.contains(h.getName())).collect(Collectors.toList());
        List<Hero> damageDealers = heroesList.stream().filter(h -> h.getRole().equals(Role.DAMAGE) && !bannedHeroesNames.contains(h.getName())).collect(Collectors.toList());
        List<Hero> supports = heroesList.stream().filter(h -> h.getRole().equals(Role.SUPPORT) && !bannedHeroesNames.contains(h.getName())).collect(Collectors.toList());

        List<Hero> heroesInTeam = new ArrayList<>();
        heroesInTeam.addAll(getRandomWithoutRepetition(tanks, nbTanks));
        heroesInTeam.addAll(getRandomWithoutRepetition(damageDealers, nbDamage));
        heroesInTeam.addAll(getRandomWithoutRepetition(supports, nbSupport));

        return new Team(heroesInTeam);
    }

    private static List<Hero> getRandomWithoutRepetition(List<Hero> lst, int nb) {
        List<Hero> lstCopy = new ArrayList<>(lst);
        Collections.shuffle(lstCopy);
        return lstCopy.subList(0, nb);
    }
}
