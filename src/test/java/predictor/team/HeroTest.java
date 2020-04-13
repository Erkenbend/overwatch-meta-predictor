package predictor.team;

import org.junit.jupiter.api.Test;
import predictor.exception.InvalidRoleException;

import static org.junit.jupiter.api.Assertions.*;

class HeroTest {

    @Test
    void testConstructor_ok1() {
        Hero hero = assertDoesNotThrow(() -> new Hero("Firstdamage,D"));
        assertEquals("Firstdamage", hero.getName());
        assertEquals(Role.DAMAGE, hero.getRole());
    }

    @Test
    void testConstructor_ok2() {
        Hero hero = assertDoesNotThrow(() -> new Hero("Tank", "T"));
        assertEquals("Tank", hero.getName());
        assertEquals(Role.TANK, hero.getRole());
    }

    @Test
    void testConstructor_ok3() {
        Hero hero = assertDoesNotThrow(() -> new Hero("Support", Role.SUPPORT));
        assertEquals("Support", hero.getName());
        assertEquals(Role.SUPPORT, hero.getRole());
    }

    @Test
    void testConstructor_ok4() {
        Hero hero = assertDoesNotThrow(() -> new Hero("Tank,t"));
        assertEquals("Tank", hero.getName());
        assertEquals(Role.TANK, hero.getRole());
    }

    @Test
    void testConstructor_invalidRole() {
        assertThrows(InvalidRoleException.class, () -> new Hero("Unknown,U"));
    }

    @Test
    void testEquals_positive1() {
        Hero hero1 = new Hero("Firstdamage", Role.DAMAGE);
        assertEquals(hero1, hero1);
    }

    @Test
    void testEquals_positive2() {
        Hero hero1 = new Hero("Seconddamage", Role.DAMAGE);
        Hero hero2 = new Hero("Seconddamage", Role.DAMAGE);
        assertEquals(hero2, hero1);
    }

    @Test
    void testEquals_negative1() {
        Hero hero1 = new Hero("Seconddamage", Role.DAMAGE);
        Hero hero2 = new Hero("Seconddamage", Role.SUPPORT);
        assertNotEquals(hero2, hero1);
    }

    @Test
    void testEquals_negative2() {
        Hero hero1 = new Hero("Firstdamage", Role.DAMAGE);
        Hero hero2 = new Hero("Seconddamage", Role.DAMAGE);
        assertNotEquals(hero2, hero1);
    }

    @Test
    void testToString() {
        Hero hero = new Hero("Tank", Role.TANK);
        assertEquals("Tank (T)", hero.toString());
    }
}
