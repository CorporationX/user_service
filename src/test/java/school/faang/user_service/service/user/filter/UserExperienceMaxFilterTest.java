package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static school.faang.user_service.util.TestUser.FILTERED_USERS;
import static school.faang.user_service.util.TestUser.USER_LIST;

class UserExperienceMaxFilterTest {

    private final UserExperienceMaxFilter userExperienceMaxFilter = new UserExperienceMaxFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .experienceMax(80)
                .build();
        boolean isApplicable = userExperienceMaxFilter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIfFilterNotSpecified() {
        UserFilterDto filters = new UserFilterDto();
        boolean isApplicable = userExperienceMaxFilter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .experienceMax(80)
                .build();
        Stream<User> receivedUsers = userExperienceMaxFilter.apply(USER_LIST.stream(), filters);
        assertEquals(FILTERED_USERS, receivedUsers.toList());
    }
}