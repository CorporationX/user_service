package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.util.TestUser.FILTERED_BY_EXP_MIN;
import static school.faang.user_service.util.TestUser.USER_LIST;

class UserExperienceMinFilterTest {

    private final UserExperienceMinFilter userExperienceMinFilter = new UserExperienceMinFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .experienceMin(10)
                .build();
        boolean isApplicable = userExperienceMinFilter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIfFilterNotSpecified() {
        UserFilterDto filters = new UserFilterDto();
        boolean isApplicable = userExperienceMinFilter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .experienceMin(95)
                .build();
        Stream<User> receivedUsers = userExperienceMinFilter.apply(USER_LIST.stream(), filters);
        assertEquals(FILTERED_BY_EXP_MIN, receivedUsers.toList());
    }

}