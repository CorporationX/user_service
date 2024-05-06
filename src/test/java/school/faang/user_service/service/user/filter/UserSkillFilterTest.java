package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.util.TestUser.FILTERED_USERS;
import static school.faang.user_service.util.TestUser.USER_LIST;

class UserSkillFilterTest {

    private final UserSkillFilter userSkillFilter = new UserSkillFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .skillPattern("Move")
                .build();
        boolean isApplicable = userSkillFilter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIfFilterNotSpecified() {
        UserFilterDto filters = new UserFilterDto();
        boolean isApplicable = userSkillFilter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .skillPattern("Move")
                .build();
        Stream<User> receivedUsers = userSkillFilter.apply(USER_LIST.stream(), filters);
        assertEquals(FILTERED_USERS, receivedUsers.toList());
    }

}