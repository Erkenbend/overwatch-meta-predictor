package predictor.team;

import predictor.exception.InvalidTeamException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TeamHelper {
    private TeamHelper() {
        throw new IllegalStateException("Utility Class");
    }

    public static final int DEFAULT_NB_HEROES_PER_TEAM = 6;

    public static List<Hero> getSortedHeroListForRole(List<Hero> hList, Role role) {
        return hList.stream()
                .filter(h -> h.getRole().equals(role))
                .sorted(Comparator.comparing(Hero::getName))
                .collect(Collectors.toList());
    }

    public static void checkTeamValidity(
            List<Hero> hList,
            List<String> bannedHeroesNames,
            int nbHeroesPerTeam,
            boolean enforceHeroLimits,
            boolean enforceRoleLimits
    ) throws InvalidTeamException {
        List<String> violations = new ArrayList<>();

        if (hList.size() != nbHeroesPerTeam) {
            violations.add(String.format(
                    "Team does not have the required number of heroes (should be %d)", nbHeroesPerTeam));
        }

        boolean hasBannedHeroes = hList.stream().anyMatch(h -> bannedHeroesNames.contains(h.getName()));
        if (hasBannedHeroes) {
            violations.add("Team contains banned heroes");
        }

        if (enforceHeroLimits) {
            // boolean hasDuplicatedHeroes = (hList.stream().distinct().count() != hList.size());  // <-- broken
            boolean hasDuplicatedHeroes = (hList.stream().map(Hero::getName).distinct().count() != hList.size());
            if (hasDuplicatedHeroes) {
                violations.add("Team contains duplicated heroes");
            }
        }

        if (enforceRoleLimits) {
            int nbTanks = (int) hList.stream().filter(h -> h.getRole() == Role.TANK).count();
            int nbDamage = (int) hList.stream().filter(h -> h.getRole() == Role.DAMAGE).count();
            int nbSupport = (int) hList.stream().filter(h -> h.getRole() == Role.SUPPORT).count();

            if (!(nbTanks == 2 && nbDamage == 2 && nbSupport == 2)) {
                violations.add("Role limits are not respected");
            }
        }

        if (!violations.isEmpty()) {
            throw new InvalidTeamException(violations);
        }
    }

    public static void checkTeamValidity(
            List<Hero> hList,
            List<String> bannedHeroesNames
    ) throws InvalidTeamException {
        checkTeamValidity(hList, bannedHeroesNames, DEFAULT_NB_HEROES_PER_TEAM, true, true);
    }
}
