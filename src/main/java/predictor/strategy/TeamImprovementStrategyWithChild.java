package predictor.strategy;

import predictor.graph.EnemyGraph;
import predictor.team.Hero;
import predictor.graph.TeamGraph;

import java.util.List;

public abstract class TeamImprovementStrategyWithChild extends TeamImprovementStrategy {

    protected TeamImprovementStrategy childStrategy;

    @Override
    public void initialize(
            List<Hero> heroesList,
            TeamGraph teamGraph,
            EnemyGraph enemyGraph,
            List<String> bannedHeroesNames
    ) {
        super.initialize(heroesList, teamGraph, enemyGraph, bannedHeroesNames);
        this.childStrategy.initialize(heroesList, teamGraph, enemyGraph, bannedHeroesNames);
    }
}
