package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserPhoneFilterTest {
    private final UserPhoneFilter userPhoneFilter = new UserPhoneFilter();

    // -------------------------------------------------
    // isApplicable()
    // -------------------------------------------------
    @Test
    public void testIsApplicable() {
        boolean result = userPhoneFilter
                .isApplicable(new UserFiltersDto(null, "777901485", 0, 0));

        assertTrue(result);
    }

    @Test
    public void testIsNotApplicable_Empty() {
        boolean result = userPhoneFilter
                .isApplicable(new UserFiltersDto(null, "", 0, 0));

        assertFalse(result);
    }

    @Test
    public void testIsNotApplicable_Blanked() {
        boolean result = userPhoneFilter
                .isApplicable(new UserFiltersDto(null, "   ", 0, 0));

        assertFalse(result);
    }

    @Test
    public void testIsNotApplicable_Null() {
        boolean result = userPhoneFilter
                .isApplicable(new UserFiltersDto(null, null, 0, 0));

        assertFalse(result);
    }

    // -------------------------------------------------
    // apply()
    // -------------------------------------------------
    @Test
    public void testApplyCorrectPhoneNumber() {
        Stream<User> users = Stream.of(
                User.builder().phone("111111111").build(),
                User.builder().phone("777901485").build());

        List<User> userList = userPhoneFilter
                .apply(users, new UserFiltersDto(null, "777901485", 0, 0))
                .toList();

        assertEquals(1, userList.size());
        assertEquals("777901485", userList.get(0).getPhone());
    }

    @Test
    public void testApplyWrongPhoneNumber() {
        Stream<User> users = Stream.of(
                User.builder().phone("111111111").build(),
                User.builder().phone("222222222").build());

        List<User> userList = userPhoneFilter
                .apply(users, new UserFiltersDto(null, "777901485", 0, 0))
                .toList();

        assertEquals(0, userList.size());
    }
}