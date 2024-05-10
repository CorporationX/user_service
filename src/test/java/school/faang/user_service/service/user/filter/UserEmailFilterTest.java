package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.util.TestUser.FILTERED_USERS;
import static school.faang.user_service.util.TestUser.USER_LIST;

@ExtendWith(MockitoExtension.class)
class UserEmailFilterTest {
    private final UserEmailFilter userEmailFilter = new UserEmailFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .emailPattern("1@m.ru")
                .build();
        boolean isApplicable = userEmailFilter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIfFilterNotSpecified() {
        UserFilterDto filters = new UserFilterDto();
        boolean isApplicable = userEmailFilter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .emailPattern("1@m.ru")
                .build();
        Stream<User> receivedUsers = userEmailFilter.apply(USER_LIST.stream(), filters);
        assertEquals(FILTERED_USERS, receivedUsers.toList());
    }
}