package predictor.util;

import predictor.exception.InvalidRoleException;
import predictor.exception.ResourceReaderException;
import predictor.graph.EnemyGraph;
import predictor.graph.TeamGraph;
import predictor.team.Hero;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: refactor getTeamGraph and getEnemyGraph common parts
public class ResourceReader {

    // TODO: read this from config file
    private static final String HEROES_LIST_FILE_NAME = "/heroes_list";
    private static final String TEAM_GRAPH_FILE_NAME = "/team_graph";
    private static final String ENEMY_GRAPH_FILE_NAME = "/enemy_graph";

    public static List<Hero> getHeroesList() throws ResourceReaderException {
        List<Hero> heroes = new ArrayList<>();

        try (InputStream inputStream = ResourceReader.class.getResourceAsStream(HEROES_LIST_FILE_NAME);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            br.lines().forEach(line -> {
                try {
                    heroes.add(new Hero(line));
                } catch (InvalidRoleException e) {
                    // e.printStackTrace();
                    throw new RuntimeException("Invalid Role", e);
                }
            });

        } catch (IOException e) {
            // e.printStackTrace();
            throw new ResourceReaderException("Could not read heroes list", e);
        }

        return heroes;
    }

    public static TeamGraph getTeamGraph(List<Hero> heroesList) throws ResourceReaderException {
        TeamGraph teamGraph = new TeamGraph(heroesList);

        try (InputStream inputStream = ResourceReader.class.getResourceAsStream(TEAM_GRAPH_FILE_NAME);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            List<String> lines = br.lines().collect(Collectors.toList());
            int nbHeroes = lines.size();
            if (nbHeroes != heroesList.size()) {
                throw new ResourceReaderException("Heroes List and Team Graph size do not match");
            }

            for (int currentLineIndex = 0; currentLineIndex < nbHeroes; currentLineIndex++) {
                String currentLineContent = lines.get(currentLineIndex);
                String[] currentLineAsArray = currentLineContent.split(","); // TODO replace with config separator
                for (int j = 0; j < currentLineAsArray.length; j++) {
                    teamGraph.set(currentLineIndex, j, Integer.parseInt(currentLineAsArray[j]));
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            throw new ResourceReaderException("Could not read heroes list", e);
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            throw new ResourceReaderException("Invalid integer value", e);
        }

        return teamGraph;
    }

    public static EnemyGraph getEnemyGraph(List<Hero> heroesList) throws ResourceReaderException {
        EnemyGraph enemyGraph = new EnemyGraph(heroesList);


        try (InputStream inputStream = ResourceReader.class.getResourceAsStream(ENEMY_GRAPH_FILE_NAME);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            List<String> lines = br.lines().collect(Collectors.toList());
            int nbHeroes = lines.size();
            if (nbHeroes != heroesList.size()) {
                throw new ResourceReaderException("Heroes List and Enemy Graph size do not match");
            }

            for (int currentLineIndex = 0; currentLineIndex < nbHeroes; currentLineIndex++) {
                String currentLineContent = lines.get(currentLineIndex);
                String[] currentLineAsArray = currentLineContent.split(","); // TODO replace with config separator
                for (int j = 0; j < currentLineAsArray.length; j++) {
                    enemyGraph.set(currentLineIndex, j, Integer.parseInt(currentLineAsArray[j]));
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            throw new ResourceReaderException("Could not read enemy graph", e);
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            throw new ResourceReaderException("Invalid integer value", e);
        }

        return enemyGraph;
    }
}