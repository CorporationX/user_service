package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.userdto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

class PhoneFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private PhoneFilter phoneFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        phoneFilter = new PhoneFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenPhonePatternIsNotNull() {
        when(userFilterDto.getPhonePattern()).thenReturn("+7952444524");
        assertTrue(phoneFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenPhonePatternIsNull() {
        when(userFilterDto.getPhonePattern()).thenReturn(null);
        assertFalse(phoneFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchPhonePattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getPhonePattern()).thenReturn("+854521475544");

        filteredUsers = phoneFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }

    @Test
    void testApplyWhenNoUsersMatchPhonePattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getPhonePattern()).thenReturn("+99998755225");
        filteredUsers = phoneFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        userFirst.setPhone("+854521475544");
        userSecond.setPhone("+78468222");
        userThird.setPhone("+78545236528");
        return List.of(userFirst, userSecond, userThird);
    }
}