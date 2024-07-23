package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.userdto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

class EmailFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private EmailFilter emailFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        emailFilter = new EmailFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenEmailPatternIsNotNull() {
        when(userFilterDto.getEmailPattern()).thenReturn("123@mail.ru");
        assertTrue(emailFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenEmailPatternIsNull() {
        when(userFilterDto.getEmailPattern()).thenReturn(null);
        assertFalse(emailFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchEmailPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getEmailPattern()).thenReturn("123@mail.ru");

        filteredUsers = emailFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }

    @Test
    void testApplyWhenNoUsersMatchEmailPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getEmailPattern()).thenReturn("bla@gmail.com");
        filteredUsers = emailFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        userFirst.setEmail("123@mail.ru");
        userSecond.setEmail("321@mail.ru");
        userThird.setEmail("123@gmail.com");
        return List.of(userFirst, userSecond, userThird);
    }
}