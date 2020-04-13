package predictor.strategy;

import predictor.graph.EnemyGraph;
import predictor.team.Hero;
import predictor.team.Team;
import predictor.graph.TeamGraph;

import java.util.List;

public abstract class TeamImprovementStrategy {
    protected List<Hero> heroesList;
    protected TeamGraph teamGraph;
    protected EnemyGraph enemyGraph;
    protected List<String> bannedHeroesNames;

    public void initialize(
            List<Hero> heroesList,
            TeamGraph teamGraph,
            EnemyGraph enemyGraph,
            List<String> bannedHeroesNames
    ) {
        this.heroesList = heroesList;
        this.teamGraph = teamGraph;
        this.enemyGraph = enemyGraph;
        this.bannedHeroesNames = bannedHeroesNames;
    }

    public abstract List<Hero> nextIteration(Team myTeam, final Team enemyTeam);
}
