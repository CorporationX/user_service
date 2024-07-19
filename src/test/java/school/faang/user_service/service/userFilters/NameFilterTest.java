package school.faang.user_service.service.userFilters;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

class NameFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private NameFilter nameFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        nameFilter = new NameFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenNamePatternIsNotNull() {
        when(userFilterDto.getNamePattern()).thenReturn("Kevin");
        assertTrue(nameFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenNamePatternIsNull() {
        when(userFilterDto.getNamePattern()).thenReturn(null);
        assertFalse(nameFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchNamePattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getNamePattern()).thenReturn("Rik");

        filteredUsers = nameFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));
    }

    @Test
    void testApplyWhenNoUsersMatchNamePattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getNamePattern()).thenReturn("Tomas");
        filteredUsers = nameFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        userFirst.setUsername("Rik");
        userSecond.setUsername("Rik");
        userThird.setUsername("Victor");
        return List.of(userFirst, userSecond, userThird);
    }
}