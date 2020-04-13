package predictor.team;

import org.junit.jupiter.api.Test;
import predictor.exception.InvalidRoleException;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void fromLetter_ok1() {
        Role parsedRole = assertDoesNotThrow(() -> Role.fromLetter("T"));
        assertEquals(Role.TANK, parsedRole);
    }

    @Test
    void fromLetter_ok2() {
        Role parsedRole = assertDoesNotThrow(() -> Role.fromLetter("d"));
        assertEquals(Role.DAMAGE, parsedRole);
    }

    @Test
    void fromLetter_invalid1() {
        assertThrows(InvalidRoleException.class, () -> Role.fromLetter("h"));
    }

    @Test
    void fromLetter_invalid2() {
        assertThrows(InvalidRoleException.class, () -> Role.fromLetter("support"));
    }
}
