package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserExperienceFilterTest {
    private final UserExperienceFilter userExperienceFilter = new UserExperienceFilter();

    // -------------------------------------------------
    // isApplicable()
    // -------------------------------------------------
    @Test
    public void testIsApplicableWith_BothRange() {
        boolean result = userExperienceFilter
                .isApplicable(new UserFiltersDto(null, null, 1, 1));
        assertTrue(result);
    }

    @Test
    public void testIsApplicableOnlyWith_MinRange() {
        boolean result = userExperienceFilter
                .isApplicable(new UserFiltersDto(null, null, 1, 0));
        assertTrue(result);
    }

    @Test
    public void testIsApplicableOnlyWith_MaxRange() {
        boolean result = userExperienceFilter
                .isApplicable(new UserFiltersDto(null, null, 0, 1));
        assertTrue(result);
    }

    @Test
    public void testIsApplicableOnlyWith_NoneRange() {
        boolean result = userExperienceFilter
                .isApplicable(new UserFiltersDto(null, null, 0, 0));
        assertFalse(result);
    }

    // -------------------------------------------------
    // apply()
    // -------------------------------------------------
    @Test
    public void testApplyWithCorrectRangeExperience() {
        Stream<User> users = Stream.of(
                User.builder().experience(6).build(),
                User.builder().experience(5).build(),
                User.builder().experience(3).build(),
                User.builder().experience(1).build()
        );

        List<User> userList = userExperienceFilter
                .apply(users, new UserFiltersDto(null, null, 2, 5))
                .toList();

        assertEquals(2, userList.size());
        assertEquals(5, userList.get(0).getExperience());
        assertEquals(3, userList.get(1).getExperience());
    }

    @Test
    public void testApplyWithWrongRangeExperience() {
        Stream<User> users = Stream.of(
                User.builder().experience(7).build(),
                User.builder().experience(8).build(),
                User.builder().experience(0).build(),
                User.builder().experience(1).build()
        );

        List<User> userList = userExperienceFilter
                .apply(users, new UserFiltersDto(null, null, 2, 5))
                .toList();

        assertEquals(0, userList.size());
    }
}
