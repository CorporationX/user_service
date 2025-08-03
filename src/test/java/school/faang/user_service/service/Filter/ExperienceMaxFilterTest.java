package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.ExperienceMax;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class ExperienceMaxFilterTest {
    private final ExperienceMax experienceMax = new ExperienceMax();
    private static final int EXPERIENCE_MAX = 40;
    private static final int EXPERIENCE_MIN = 27;
    private static final int EXPERIENCE_FILTER = 30;

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = createFilter(EXPERIENCE_MAX);
        boolean result = experienceMax.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = createFilter(null);
        boolean result = experienceMax.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = createUsers(EXPERIENCE_MIN, EXPERIENCE_MAX);
        UserFilterDto filter = createFilter(EXPERIENCE_FILTER);

        List<User> result = experienceMax.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EXPERIENCE_MIN, result.get(0).getExperience());
    }

    @Test
    public void testApplyNotSuitableUsers() {
        Stream<User> users = createUsers(EXPERIENCE_MIN, EXPERIENCE_MAX);
        UserFilterDto filter = createFilter(EXPERIENCE_FILTER);

        List<User> result = experienceMax.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private UserFilterDto createFilter(Integer experience) {
        return new UserFilterDto(null, null, null, experience);
    }

    private Stream<User> createUsers(int experienceMin, int experienceMax) {
        return Stream.of(
                User.builder().experience(experienceMin).build(),
                User.builder().experience(experienceMax).build());
    }
}
