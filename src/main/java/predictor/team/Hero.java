package predictor.team;

import predictor.exception.InvalidRoleException;

import java.util.Objects;

public class Hero {
    private String name;
    private Role role;

    public Hero(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    public Hero(String name, String roleLetter) throws InvalidRoleException {
        this.name = name;
        this.role = Role.fromLetter(roleLetter);
    }

    public Hero(String heroEntry) throws InvalidRoleException {
        String[] heroAsArray = heroEntry.split(",");  // TODO replace with config separator
        this.name = heroAsArray[0];
        this.role = Role.fromLetter(heroAsArray[1]);
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Hero &&
                Objects.equals(((Hero) other).name, this.name) &&
                ((Hero) other).role == this.role);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, role.getLetter());
    }
}
