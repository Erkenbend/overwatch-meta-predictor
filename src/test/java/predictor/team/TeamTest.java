package predictor.team;

import org.junit.jupiter.api.Test;
import predictor.exception.ResourceReaderException;
import predictor.graph.EnemyGraph;
import predictor.graph.TeamGraph;
import predictor.util.ResourceReader;
import predictor.util.Scored;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    private List<Hero> heroList;
    private TeamGraph teamGraph;
    private EnemyGraph enemyGraph;

    TeamTest() throws ResourceReaderException {
        this.heroList = ResourceReader.getHeroesList();
        this.teamGraph = ResourceReader.getTeamGraph(heroList);
        this.enemyGraph = ResourceReader.getEnemyGraph(heroList);
    }

    @Test
    void getTotalSynergy_1() {
        Team team = new Team(Arrays.asList(
                new Hero("Seconddamage", Role.DAMAGE),
                new Hero("Tank", Role.TANK),
                new Hero("Support", Role.SUPPORT)
        ));

        // Seconddamage + Tank --> -1
        // Seconddamage + Support --> 2
        // Tank + Support --> 0
        assertEquals(1, team.getTotalSynergy(teamGraph));
    }

    @Test
    void getTotalSynergy_2() {
        Team team = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Tank", Role.TANK),
                new Hero("Support", Role.SUPPORT)
        ));

        // Firstdamage + Tank --> 1
        // Firstdamage + Support --> -1
        // Tank + Support --> 0
        assertEquals(0, team.getTotalSynergy(teamGraph));
    }

    @Test
    void getTotalSynergy_3() {
        Team team = new Team(Arrays.asList(
                new Hero("Tank", Role.TANK),
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Seconddamage", Role.DAMAGE),
                new Hero("Support", Role.SUPPORT)
        ));

        // Tank + Firstdamage --> 1
        // Tank + Seconddamage --> -1
        // Tank + Support --> 0
        // Firstdamage + Seconddamage --> 0
        // Firstdamage + Support --> -1
        // Seconddamage + Support --> 2
        assertEquals(1, team.getTotalSynergy(teamGraph));
    }

    @Test
    void getTotalDominance_1() {
        Team team = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Seconddamage", Role.DAMAGE),
                new Hero("Tank", Role.TANK)
        ));

        assertEquals(0, team.getTotalDominance(team, enemyGraph));
    }

    @Test
    void getTotalDominance_2() {
        Team team1 = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Tank", Role.TANK)
        ));

        Team team2 = new Team(Arrays.asList(
                new Hero("Seconddamage", Role.DAMAGE),
                new Hero("Support", Role.SUPPORT)
        ));

        // Firstdamage over Seconddamage --> -1
        // Firstdamage over Support --> 2
        // Tank over Seconddamage --> -1
        // Tank over Support --> 1
        assertEquals(1, team1.getTotalDominance(team2, enemyGraph));
        assertEquals(-1, team2.getTotalDominance(team1, enemyGraph));
    }

    @Test
    void getTotalDominance_3() {
        Team team1 = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Seconddamage", Role.DAMAGE)
        ));

        Team team2 = new Team(Arrays.asList(
                new Hero("Support", Role.SUPPORT),
                new Hero("Firstdamage", Role.DAMAGE)
        ));

        // Firstdamage over Support --> 2
        // Firstdamage over Firstdamage --> 0
        // Seconddamage over Support --> 0
        // Seconddamage over Firstdamage --> 1
        assertEquals(3, team1.getTotalDominance(team2, enemyGraph));
        assertEquals(-3, team2.getTotalDominance(team1, enemyGraph));
    }

    @Test
    void getSynergyTo() {
        Team team = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Seconddamage", Role.DAMAGE)
        ));

        Hero heroToCheck = new Hero("Support", Role.SUPPORT);

        // Firstdamage + Support --> -1
        // Seconddamage + Support --> 2
        assertEquals(1, team.getSynergyTo(heroToCheck.getName(), teamGraph));
    }

    @Test
    void getSynergiesPerHero() {
        Team team = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Support", Role.SUPPORT)
        ));

        // Firstdamage: + Firstdamage 0, + Support -1 --> -1 (position 0 or 1)
        // Seconddamage: + Firstdamage 0, + Support 2 --> 2 (position 3)
        // Tank: + Firstdamage 1, + Support 0 --> 1 (position 2)
        // Support: + Firstdamage -1, + Support 0 --> -1 (position 0 or 1)
        List<Scored<Hero>> synergiesPerHero = team.getSynergiesPerHero(heroList, teamGraph);

        assertEquals(4, synergiesPerHero.size());
        assertTrue(Arrays.asList("Firstdamage", "Support").contains(synergiesPerHero.get(0).getObj().getName()));
        assertEquals(-1, synergiesPerHero.get(0).getScore());
        assertTrue(Arrays.asList("Firstdamage", "Support").contains(synergiesPerHero.get(1).getObj().getName()));
        assertEquals(-1, synergiesPerHero.get(1).getScore());
        assertEquals("Tank", synergiesPerHero.get(2).getObj().getName());
        assertEquals(1, synergiesPerHero.get(2).getScore());
        assertEquals("Seconddamage", synergiesPerHero.get(3).getObj().getName());
        assertEquals(2, synergiesPerHero.get(3).getScore());
    }

    @Test
    void getDominanceOver() {
        Team team = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Seconddamage", Role.DAMAGE)
        ));

        Hero heroToCheck = new Hero("Support", Role.SUPPORT);

        // Firstdamage over Support --> 2
        // Seconddamage over Support --> 0
        assertEquals(2, team.getDominanceOver(heroToCheck.getName(), enemyGraph));
    }

    @Test
    void getDominancePerHero() {
        Team team = new Team(Arrays.asList(
                new Hero("Seconddamage", Role.DAMAGE),
                new Hero("Tank", Role.TANK)
        ));

        // Seconddamage over Firstdamage 1, Tank over Firstdamage 1 --> 2 (position 3)
        // Seconddamage over Seconddamage 0, Tank over Seconddamage -1 --> -1 (position 0)
        // Seconddamage over Tank 1, Tank over Tank 0 --> 1 (position 1 or 2)
        // Seconddamage over Support 0, Tank over Support 1 --> 1 (position 1 or 2)
        List<Scored<Hero>> dominancePerHero = team.getDominancePerHero(heroList, enemyGraph);

        assertEquals(4, dominancePerHero.size());
        assertEquals("Seconddamage", dominancePerHero.get(0).getObj().getName());
        assertEquals(-1, dominancePerHero.get(0).getScore());
        assertTrue(Arrays.asList("Tank", "Support").contains(dominancePerHero.get(1).getObj().getName()));
        assertEquals(1, dominancePerHero.get(1).getScore());
        assertTrue(Arrays.asList("Tank", "Support").contains(dominancePerHero.get(2).getObj().getName()));
        assertEquals(1, dominancePerHero.get(2).getScore());
        assertEquals("Firstdamage", dominancePerHero.get(3).getObj().getName());
        assertEquals(2, dominancePerHero.get(3).getScore());
    }

    @Test
    void getBestSwitchForPosition_switchToPerfectSynergy() {
        List<Hero> team1HeroList = new ArrayList<>();  // short syntax produces immutable hero list
        team1HeroList.add(new Hero("Seconddamage", Role.DAMAGE));
        team1HeroList.add(new Hero("Seconddamage", Role.DAMAGE));
        team1HeroList.add(new Hero("Firstdamage", Role.DAMAGE));
        Team team1 = new Team(team1HeroList);

        Team team2 = new Team(Arrays.asList(
                new Hero("Seconddamage", Role.DAMAGE),
                new Hero("Support", Role.SUPPORT),
                new Hero("Support", Role.SUPPORT)
        ));

        // Firstdamage in team1 looks for best switch
        // Current synergy with team: 0, Current dominance over enemy team: 3 --> Score 3
        // Scores for: Support=4, [Seconddamage=0 banned], Tank=-1 --> switch to Support
        Hero switchTo = team1.getBestSwitchForPosition(
                2, heroList, teamGraph, enemyGraph,
                Collections.singletonList("Seconddamage"), team2, false, false, Integer::sum
        );

        assertEquals(new Hero("Support", Role.SUPPORT), switchTo);
    }

    @Test
    void getBestSwitchForPosition_switchToHardCounter() {
        List<Hero> team1HeroList = new ArrayList<>();  // short syntax produces immutable hero list
        team1HeroList.add(new Hero("Tank", Role.TANK));
        team1HeroList.add(new Hero("Seconddamage", Role.DAMAGE));
        team1HeroList.add(new Hero("Firstdamage", Role.DAMAGE));
        Team team1 = new Team(team1HeroList);

        Team team2 = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Support", Role.SUPPORT),
                new Hero("Support", Role.SUPPORT)
        ));

        // Seconddamage in team1 looks for best switch
        // Current synergy with team: -1, Current dominance over enemy team: 1 --> Score 0
        // Scores for: Firstdamage=5, Tank=4, Support=-3 --> switch to Firstdamage
        Hero switchTo = team1.getBestSwitchForPosition(
                1, heroList, teamGraph, enemyGraph,
                new ArrayList<>(), team2, false, false, Integer::sum
        );

        assertEquals(new Hero("Firstdamage", Role.DAMAGE), switchTo);
    }

    @Test
    void getBestSwitchForPosition_switchToOtherDamage() {
        List<Hero> team1HeroList = new ArrayList<>();  // short syntax produces immutable hero list
        team1HeroList.add(new Hero("Firstdamage", Role.DAMAGE));
        team1HeroList.add(new Hero("Tank", Role.TANK));
        team1HeroList.add(new Hero("Support", Role.SUPPORT));
        Team team1 = new Team(team1HeroList);

        Team team2 = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Tank", Role.TANK),
                new Hero("Support", Role.SUPPORT)
        ));

        // Firstdamage in team1 looks for best switch
        // Current synergy with team: 0, Current dominance over enemy team: 1 --> Score 1
        // Scores for: Seconddamage=3, [Tank=2 not same role], [Support=-3 not same role] --> switch to Seconddamage
        Hero switchTo = team1.getBestSwitchForPosition(
                0, heroList, teamGraph, enemyGraph,
                new ArrayList<>(), team2, true, true, Integer::sum
        );

        assertEquals(new Hero("Seconddamage", Role.DAMAGE), switchTo);
    }

    @Test
    void getBestSwitchForPosition_switchForbidden() {
        List<Hero> team1HeroList = new ArrayList<>();  // short syntax produces immutable hero list
        team1HeroList.add(new Hero("Firstdamage", Role.DAMAGE));
        team1HeroList.add(new Hero("Tank", Role.TANK));
        team1HeroList.add(new Hero("Support", Role.SUPPORT));
        Team team1 = new Team(team1HeroList);

        Team team2 = new Team(Arrays.asList(
                new Hero("Firstdamage", Role.DAMAGE),
                new Hero("Tank", Role.TANK),
                new Hero("Support", Role.SUPPORT)
        ));

        // Firstdamage in team1 looks for best switch
        // Current synergy with team: 0, Current dominance over enemy team: 1 --> Score 1
        // Scores for: [Seconddamage=3 banned], [Tank=2 not same role], [Support=-3 not same role] --> do not switch
        Hero switchTo = team1.getBestSwitchForPosition(
                0, heroList, teamGraph, enemyGraph,
                Collections.singletonList("Seconddamage"), team2, true, true, Integer::sum
        );

        assertEquals(new Hero("Firstdamage", Role.DAMAGE), switchTo);
    }
}
