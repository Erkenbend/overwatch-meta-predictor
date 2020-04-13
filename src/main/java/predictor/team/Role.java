package predictor.team;

import predictor.exception.InvalidRoleException;

public enum Role {
    TANK("T"),
    DAMAGE("D"),
    SUPPORT("S");

    private String letter;

    public String getLetter() {
        return this.letter;
    }

    Role(String letter) {
        this.letter = letter.toUpperCase();
    }

    public static Role fromLetter(String letter) throws InvalidRoleException {
        String uppercaseLetter = letter.toUpperCase();
        for (Role r : Role.values()) {
            if (r.getLetter().equals(uppercaseLetter)) {
                return r;
            }
        }
        throw new InvalidRoleException(String.format("No role for Letter %s", uppercaseLetter));
    }
}
