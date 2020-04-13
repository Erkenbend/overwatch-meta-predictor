package predictor.team;

import org.junit.jupiter.api.Test;
import predictor.exception.InvalidTeamException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamHelperTest {

    @Test
    void getSortedHeroListForRole() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Bd", Role.DAMAGE),
                new Hero("Ad", Role.DAMAGE),
                new Hero("At", Role.TANK)
        );

        List<Hero> expectedSortedList = Arrays.asList(
                new Hero("Ad", Role.DAMAGE),
                new Hero("Bd", Role.DAMAGE)
        );

        List<Hero> sortedHeroListForRoleDamage =
                TeamHelper.getSortedHeroListForRole(heroList, Role.DAMAGE);

        assertEquals(expectedSortedList, sortedHeroListForRoleDamage);
    }

    @Test
    void checkTeamValidity_minimalChecks() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Ad", Role.DAMAGE),
                new Hero("Ad", Role.DAMAGE),
                new Hero("At", Role.TANK)
        );

        assertDoesNotThrow(() ->
                TeamHelper.checkTeamValidity(
                        heroList, new ArrayList<>(),
                        4, false, false
                )
        );
    }

    @Test
    void checkTeamValidity_ok() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Bs", Role.SUPPORT),
                new Hero("Ad", Role.DAMAGE),
                new Hero("Bd", Role.DAMAGE),
                new Hero("At", Role.TANK),
                new Hero("Bt", Role.TANK)
        );

        assertDoesNotThrow(() ->
                TeamHelper.checkTeamValidity(
                        heroList, Arrays.asList("Cs", "Ct"),
                        6, true, true
                )
        );
    }

    @Test
    void checkTeamValidity_wrongNbHeroes() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Ad", Role.DAMAGE),
                new Hero("At", Role.TANK)
        );

        InvalidTeamException e = assertThrows(
                InvalidTeamException.class,
                () ->
                        TeamHelper.checkTeamValidity(
                                heroList, new ArrayList<>(),
                                6, false, false
                        )
        );

        assertEquals(
                "Invalid Team! Violations: [Team does not have the required number of heroes (should be 6)]",
                e.getMessage()
        );
    }

    @Test
    void checkTeamValidity_hasBannedHero() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Ad", Role.DAMAGE),
                new Hero("At", Role.TANK)
        );

        InvalidTeamException e = assertThrows(
                InvalidTeamException.class,
                () ->
                        TeamHelper.checkTeamValidity(
                                heroList, Collections.singletonList("At"),
                                3, false, false
                        )
        );

        assertEquals(
                "Invalid Team! Violations: [Team contains banned heroes]",
                e.getMessage()
        );
    }

    @Test
    void checkTeamValidity_hasDuplicates() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Ad", Role.DAMAGE),
                new Hero("Ad", Role.DAMAGE)
        );

        InvalidTeamException e = assertThrows(
                InvalidTeamException.class,
                () ->
                        TeamHelper.checkTeamValidity(
                                heroList, new ArrayList<>(),
                                3, true, false
                        )
        );

        assertEquals(
                "Invalid Team! Violations: [Team contains duplicated heroes]",
                e.getMessage()
        );
    }

    @Test
    void checkTeamValidity_wrongRoleLimits() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Bs", Role.SUPPORT),
                new Hero("Cs", Role.SUPPORT),
                new Hero("At", Role.TANK),
                new Hero("Bt", Role.TANK),
                new Hero("Ct", Role.TANK)
        );

        InvalidTeamException e = assertThrows(
                InvalidTeamException.class,
                () ->
                        TeamHelper.checkTeamValidity(
                                heroList, new ArrayList<>(),
                                6, true, true
                        )
        );

        assertEquals(
                "Invalid Team! Violations: [Role limits are not respected]",
                e.getMessage()
        );
    }

    @Test
    void checkTeamValidity_multipleViolations() {
        List<Hero> heroList = Arrays.asList(
                new Hero("As", Role.SUPPORT),
                new Hero("Bs", Role.SUPPORT),
                new Hero("Cs", Role.SUPPORT),
                new Hero("At", Role.TANK),
                new Hero("At", Role.TANK)
        );

        InvalidTeamException e = assertThrows(
                InvalidTeamException.class,
                () ->
                        TeamHelper.checkTeamValidity(
                                heroList, Collections.singletonList("Cs"),
                                6, true, true
                        )
        );

        assertEquals(
                "Invalid Team! Violations: [Team does not have the required number of heroes (should be 6), Team contains banned heroes, Team contains duplicated heroes, Role limits are not respected]",
                e.getMessage()
        );
    }
}