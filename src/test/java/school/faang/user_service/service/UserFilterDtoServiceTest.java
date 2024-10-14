package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.filter.UserFilterStrategy;
import school.faang.user_service.service.impl.UserFilterService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFilterDtoServiceTest {

    @Mock
    private List<UserFilterStrategy> userFilterStrategies;

    @Mock
    private UserFilterStrategy mockStrategy;

    @Mock
    private UserFilterDto filter;

    @InjectMocks
    private UserFilterService userFilterService;

    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";
    private static final String EMAIL_1 = "user1@example.com";
    private static final String EMAIL_2 = "user2@example.com";

    private User createTestUser(long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    private List<User> createTestUsers() {
        User user1 = createTestUser(1L, USERNAME_1, EMAIL_1);
        User user2 = createTestUser(2L, USERNAME_2, EMAIL_2);
        return List.of(user1, user2);
    }

    @Test
    void testFilterUsers_WithFilterApplied() {
        List<User> users = createTestUsers();

        when(mockStrategy.applyFilter(filter)).thenReturn(true);
        List<User> filteredUsers = List.of(users.get(0));
        when(mockStrategy.filter(users, filter)).thenReturn(filteredUsers);
        when(userFilterStrategies.iterator()).thenReturn(List.of(mockStrategy).iterator());

        List<User> result = userFilterService.filterUsers(users, filter);

        assertEquals(1, result.size());
        assertEquals(USERNAME_1, result.get(0).getUsername());
        verify(mockStrategy, times(1)).filter(users, filter);
    }

    @Test
    void testFilterUsers_NoFilterApplied() {
        List<User> users = createTestUsers();

        when(mockStrategy.applyFilter(filter)).thenReturn(false);
        when(userFilterStrategies.iterator()).thenReturn(List.of(mockStrategy).iterator());

        List<User> result = userFilterService.filterUsers(users, filter);

        assertEquals(2, result.size());
        assertEquals(USERNAME_1, result.get(0).getUsername());
        assertEquals(USERNAME_2, result.get(1).getUsername());
        verify(mockStrategy, times(0)).filter(users, filter);
    }
}
