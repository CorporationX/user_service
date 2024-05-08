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
import static school.faang.user_service.util.TestUser.FILTERED_BY_NAME;
import static school.faang.user_service.util.TestUser.USER_LIST;

@ExtendWith(MockitoExtension.class)
class UserNameFilterTest {

    private final UserNameFilter nameFilter = new UserNameFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("John")
                .build();
        boolean isApplicable = nameFilter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIsFilterNotSpecified() {
        UserFilterDto filters = new UserFilterDto();
        boolean isApplicable = nameFilter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("John")
                .build();
        Stream<User> receivedUsers = nameFilter.apply(USER_LIST.stream(), filters);
        assertEquals(FILTERED_BY_NAME, receivedUsers.toList());
    }
}
