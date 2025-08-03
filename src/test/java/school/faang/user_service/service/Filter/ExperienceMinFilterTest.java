package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.ExperienceMinFilter;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperienceMinFilterTest {
    private final ExperienceMinFilter experienceMinFilter = new ExperienceMinFilter();
    private static final int EXPERIENCE_MAX = 40;
    private static final int EXPERIENCE_MIN = 27;
    private static final int EXPERIENCE_FILTER = 13;
    private static final int NON_MATCHING_FILTER = 50;

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = createFilter(EXPERIENCE_MIN);
        boolean result = experienceMinFilter.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = createFilter(null);
        boolean result = experienceMinFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = createUsers(EXPERIENCE_MIN, EXPERIENCE_MAX);
        UserFilterDto filter = createFilter(EXPERIENCE_FILTER);

        List<User> result = experienceMinFilter.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EXPERIENCE_MIN, result.get(0).getExperience());
    }

    @Test
    public void testApplyNotSuitableUsers() {
        Stream<User> users = createUsers(EXPERIENCE_MIN, EXPERIENCE_MAX);
        UserFilterDto filter = createFilter(NON_MATCHING_FILTER);

        List<User> result = experienceMinFilter.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private UserFilterDto createFilter(Integer experience) {
        return new UserFilterDto(null, null, experience, null);
    }

    private Stream<User> createUsers(int experienceMin, int experienceMax) {
        return Stream.of(
                User.builder().experience(experienceMin).build(),
                User.builder().experience(experienceMax).build());
    }
}
