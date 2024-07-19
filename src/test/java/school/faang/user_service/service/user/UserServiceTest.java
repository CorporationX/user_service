package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService service;

    private final long userId = 1;

    private User generateUser(long id) {
        User user = new User();

        user.setId(id);

        return user;
    };

    @Test
    public void testGetUserEmpty() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserDto actual = service.getUser(userId);

        Assertions.assertNull(actual);
    }

    @Test
    public void testGetUser() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(generateUser(userId)));

        UserDto actual = service.getUser(userId);

        Assertions.assertEquals(userId, actual.id());
    }

    @Test
    public void testGetUsersByIdsEmpty() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<UserDto> expected = new ArrayList<>();
        Mockito.when(userRepository.findAllById(ids)).thenReturn(new ArrayList<>());

        // when
        List<UserDto> actual = service.getUsersByIds(ids);

        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetUsersByIds() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<User> usersList = List.of(generateUser(1L), generateUser(2L));
        List<UserDto> expected = usersList.stream().map(UserDto::toDto).toList();
        Mockito.when(userRepository.findAllById(ids)).thenReturn(usersList);

        // when
        List<UserDto> actual = service.getUsersByIds(ids);

        // then
        Assertions.assertEquals(expected, actual);
    }
}
