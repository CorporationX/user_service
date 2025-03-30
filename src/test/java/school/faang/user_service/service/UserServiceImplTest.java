package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl userService;

    long userId;

    @BeforeEach
    public void init() {
        userId = 10L;
    }

    @Test
    public void testGetUser_InvalidUserId_Throws() {
        var userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                DataRetrievalFailureException.class,
                () -> userService.getUser(userId));
    }

    @Test
    public void testGetUser_UserId_ReturnsUserDto() {
        var userId = 1L;
        var user = createTestUser(userId, "Test user name", "example@gmail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var result = userService.getUser(userId);

        assertEquals(user.getId(), result.id());
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
    }

    @Test
    public void testGetUsersByIds_EmptyIds_ReturnsEmptyList() {
        List<Long> userIds = new ArrayList<>();
        when(userRepository.findAllById(userIds)).thenReturn(List.of());

        var result = userService.getUsersByIds(userIds);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUsersByIds_SeveralIds_ReturnsNonEmptyList() {
        // Arrange
        List<Long> userIds = List.of(1L, 2L, 3L);
        var users = List.of(
                createTestUser(2L, "Test user name 1", "example1@gmail.com"),
                createTestUser(3L, "Test user name 2", "example2@gmail.com"));
        when(userRepository.findAllById(userIds)).thenReturn(users);

        // Act
        var result = userService.getUsersByIds(userIds);

        // Assert
        assertEquals(users.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(users.get(i).getId(), result.get(i).id());
            assertEquals(users.get(i).getUsername(), result.get(i).username());
            assertEquals(users.get(i).getEmail(), result.get(i).email());
        }
    }

    @Test
    public void testGetUserById_UserIsFound_ReturnsUser() {
        var testUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        var result = userService.getUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        assertEquals(testUser, result);
    }

    @Test
    public void testGetUserById_UserIsNotFound_Throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                DataRetrievalFailureException.class,
                () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    private static User createTestUser(long userId, String username, String email) {
        return User.builder()
                .id(userId)
                .username(username)
                .email(email)
                .build();
    }
}