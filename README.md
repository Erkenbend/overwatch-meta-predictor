# Overwatch Meta Predictor

## What does it do?

This code aims at predicting the Most Efficient Team Available (META) given certain
rules for an Overwatch-like game.

### Base principles

The program relies on 1-to-1 relationships of two sorts between every pair of heroes:
- as **teammates**: how well does each hero **synergise** with each other hero
- as **enemies**: how much does each hero **counter** each other hero

The files `team_graph` and `enemy_graph` under `src/main/resources` describe those
relationships.

In order to find the so-called Meta, two random teams are initialized and **make changes
to their composition in turns**, so that the synergy within the team grows and at the same
time it counters the other team better.

The underlying hypothesis is that this method eventually reaches an equilibrium and
thus produces two teams in which any change either reduces the overall synergy of the
team or is worse at countering the enemy team.

Each team "decides" which changes to make at each turn according to 3 things:
- its own current composition
- the composition of the enemy team
- a **strategy** assigned at the beginning; this can be any
implementation of `predictor.strategy.TeamImprovementStrategy`

### Rules

Per default, classic Overwatch rules are applied when checking for team validity:
- 6 heroes per team
- no repetition (hero limit = max. one of each per team)
- 2-2-2 role-lock (exactly 2 Damage Dealers, 2 Tanks, 2 Supports)
- hero pools (list of banned heroes given at the start)

### Limitations

This program **will not** predict the perfect Meta for the highest levels of play for a
simple reason: synergies and counters are also about positioning, communication, the map
being played, the individuals playing the game, plus a thousand small things that cannot
be taken into account when considering only 1-to-1 relationships.

However, these factors are of lesser importance in "lower"-ranked games (say diamond and below),
so it is expected that the results tend to be more in line with reality.

The performance of this program is also limited by the author's own knowledge of the game;
the 465 (times 2 for team and enemy) 1-to-1 relationships are all subject to interpretation
and many of them may be plain wrong. Improvements welcome.

Also, the only JAR-usable mode is currently this turn-battle for successive improvements.
There are some other functions, for example "Which hero should I switch to", that are worth
exploring but did not make it into the compiled version yet. See the `predictor.app` package. 

## How to use it?

### Software Requirements

In order to compile and run this program, you will need:
- Java 11 -- https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
- Maven 3.6+ -- https://maven.apache.org/download.cgi

All other dependencies will be automatically downloaded by Maven when building the JAR.

### Build the JAR

Run the following command at the root of the project:

```bash
mvn clean package
```

This will compile the source files, run tests, then build the JAR-file into `target/overwatch_meta_predictor.jar`

### Run the program

Base command:

```bash
# Raw command without options
java -jar target/overwatch_meta_predictor.jar
```

A number of options are available for configuration; use `--help` to find out more

Usage examples:

```bash
# With hero pools of 2020-04-13, 100 iterations, same strategy for both teams
java -jar target/overwatch_meta_predictor.jar --ban-hero McCree --ban-hero Widowmaker --ban-hero Reinhardt --ban-hero Brigitte --nb-iterations 100 --strategy-team-1 RandomHeroBestSwitch --strategy-team-2 RandomHeroBestSwitch

# With all heroes allowed, 1000 iterations, different strategy for both teams, quiet mode
java -jar target/overwatch_meta_predictor.jar -s1 BestDominanceReplacementWithoutSynergyDecrease -s2 BestSynergyReplacementWithoutDominanceDecrease -q
```

## How to contribute?

This small project could benefit from many improvements, see non-exhaustive list below:

### New strategies

Some example strategies have already been implemented but better ones are surely possible! Define
a new Strategy by creating a class extending `TeamImprovementStrategy` in the
`predictor.strategy` package.

Much information concerning your team and the enemy team is available in the public methods of
`Team`. Furthermore, you have direct access to both matrices (`team_graph` and `enemy_graph`)
from within the strategy class. 

In order to make the strategy usable in the compiled version, add it to the map in
`predictor.app.ConsoleApp`.

### Better matrices

Each matrix (or graph, same thing) contains 465 relationships (for 31 heroes). Due to the author's limited
knowledge, some of them may be wrong, some others very wrong. Better coefficients mean better
predictions.

Each line of the matrix correspond to the matching line in the `heroes_list` file. In order
to check your changes, please modify and use the `predictor.app.DevApp` class.

### Other minor improvements

Some TODOs have been hidden in classes, for cases in which improvements would be welcome but
are not critical.

### Bug fixes

It is a program, it will have bugs. If you happen to stumble upon one, kindly report and/or
fix it.

### Echo-support

The newest Overwatch hero *Echo* has yet to be added into the program. With her, 32*2 new
1-to-1 relationships need to be added to the list.

