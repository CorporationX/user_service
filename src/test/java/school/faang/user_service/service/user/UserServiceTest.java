package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserDto userDto;

    @Mock
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        userDto = createUserDto(1L, "johndoe", "jh@example.com", "https://minio.com/1.jpg", 12L);
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userDto.getId()));

        verify(userRepository, times(1)).findById(userDto.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = createUser(userDto.getId(), userDto.getUsername());
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userDto.getId());

        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository);

        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    public void testGetUsersEmptyList() {
        UserDto secondUser = createUserDto(2L, "johndoe", "jh@example.com", "https://minio.com/1.jpg", 12L);
        List<Long> userIds = List.of(userDto.getId(), secondUser.getId());
        when(userRepository.findAllById(userIds)).thenReturn(new ArrayList<>());
        List<UserDto> result = userService.getUsers(userIds);

        verify(userRepository, times(1)).findAllById(userIds);
        verifyNoMoreInteractions(userRepository);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testGetUsersSuccess() {
        UserDto secondUserDto = createUserDto(2L, "hj", "hj@example.com", "https://minio.com/1.jpg", 12L);
        List<Long> userIds = List.of(userDto.getId(), secondUserDto.getId());
        User firstUser = createUser(userDto.getId(), userDto.getUsername());
        User secondUser = createUser(secondUserDto.getId(), secondUserDto.getUsername());

        when(userRepository.findAllById(userIds)).thenReturn(List.of(firstUser, secondUser));
        when(userMapper.toDto(firstUser)).thenReturn(userDto);
        when(userMapper.toDto(secondUser)).thenReturn(secondUserDto);
        List<UserDto> result = userService.getUsers(userIds);

        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(2)).toDto(any(User.class));
        verifyNoMoreInteractions(userRepository);
        assertEquals(result.size(), 2);
        assertEquals(userDto.getId(), result.get(0).getId());
        assertEquals(secondUserDto.getId(), result.get(1).getId());
        assertEquals(firstUser.getUsername(), result.get(0).getUsername());
        assertEquals(secondUser.getUsername(), result.get(1).getUsername());
    }

    @Test
    void testSearchUsers() {
        Long searchingUserId = 42L;

        List<Long> result = userService.searchUsers(searchingUserId);

        assertEquals(List.of(1L, 2L, 3L), result);

        for (Long userId : result) {
            verify(searchAppearanceEventPublisher, times(1))
                    .publishSearchAppearanceEvent(argThat(event ->
                            event.getUserId().equals(userId) &&
                                    event.getSearchingUserId().equals(searchingUserId) &&
                                    event.getViewedAt() != null
                    ));
        }
    }

    private UserDto createUserDto(long id, String username, String email, String picUrl, long premiumId) {
        UserDto user = UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .userProfilePicFileId(picUrl)
                .premiumId(premiumId).build();
        return user;
    }

    private User createUser(long id, String username) {
        return User.builder()
                .id(id)
                .username(username).build();
    }
}
