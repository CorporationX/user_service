package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static school.faang.user_service.util.TestUser.FILTERED_USERS;
import static school.faang.user_service.util.TestUser.USER_LIST;

class UserContactFilterTest {

    private final UserContactFilter userContactFilter = new UserContactFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .contactPattern("Alice")
                .build();
        boolean isApplicable = userContactFilter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIfFilterNotSpecified() {
        UserFilterDto filters = new UserFilterDto();
        boolean isApplicable = userContactFilter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .contactPattern("Alice")
                .build();
        Stream<User> receivedUsers = userContactFilter.apply(USER_LIST.stream(), filters);
        assertEquals(FILTERED_USERS, receivedUsers.toList());
    }
}