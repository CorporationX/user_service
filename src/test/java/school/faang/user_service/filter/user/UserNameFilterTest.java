package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserNameFilterTest {
    private final UserNameFilter userNameFilter = new UserNameFilter();

    // -------------------------------------------------
    // isApplicable()
    // -------------------------------------------------
    @Test
    public void testIsApplicable() {
        boolean result = userNameFilter
                .isApplicable(new UserFiltersDto("Gleb", null, 0, 0));

        assertTrue(result);
    }

    @Test
    public void testIsNotApplicable_Empty() {
        boolean result = userNameFilter
                .isApplicable(new UserFiltersDto("", null, 0, 0));

        assertFalse(result);
    }

    @Test
    public void testIsNotApplicable_Blanked() {
        boolean result = userNameFilter
                .isApplicable(new UserFiltersDto("   ", null, 0, 0));

        assertFalse(result);
    }

    @Test
    public void testIsNotApplicable_Null() {
        boolean result = userNameFilter
                .isApplicable(new UserFiltersDto(null, null, 0, 0));

        assertFalse(result);
    }

    // -------------------------------------------------
    // apply()
    // -------------------------------------------------
    @Test
    public void testApplyCorrectName() {
        Stream<User> users = Stream.of(
                User.builder().username("Max").build(),
                User.builder().username("Gleb").build());

        List<User> userList = userNameFilter
                .apply(users, new UserFiltersDto("Gleb", null, 0, 0))
                .toList();

        assertEquals(1, userList.size());
        assertEquals("Gleb", userList.get(0).getUsername());
    }

    @Test
    public void testApplyWithCaseIgnore() {
        Stream<User> users = Stream.of(
                User.builder().username("gleb").build(),
                User.builder().username("gLeB").build(),
                User.builder().username("GLEB").build());

        List<User> userList = userNameFilter
                .apply(users, new UserFiltersDto("GlEb", null, 0, 0))
                .toList();

        assertEquals(3, userList.size());
        assertEquals("gleb", userList.get(0).getUsername().toLowerCase());
        assertEquals("gleb", userList.get(1).getUsername().toLowerCase());
    }

    @Test
    public void testApplyWrongNames() {
        Stream<User> users = Stream.of(
                User.builder().username("Max").build(),
                User.builder().username("Oleg").build());

        List<User> userList = userNameFilter
                .apply(users, new UserFiltersDto("GlEb", null, 0, 0))
                .toList();

        assertEquals(0, userList.size());
    }
}