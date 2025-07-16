package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserContext userContext;

    @InjectMocks
    public UserServiceImpl userService;

    @Test
    public void testGetUser() {
        long id = 2L;
        User user = new User(2L, "Bob", "ya@ya.ru", "898", "1234", false,
                "lol", null, null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null,
                null);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto dto = userMapper.toUserDto(userRepository.findById(id).orElseThrow());
        UserDto result = new UserDto(2L, "Bob", "ya@ya.ru", "898", "lol");
        assertEquals(dto, result);
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> userIds = List.of(1L, 2L);
        User userOne = new User(1L, "Bob", "ya@ya.ru", "898", "1234", false,
                "lol", null, null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null,
                null);
        User userTwo = new User(2L, "Rob", "ya@ya.ru", "898", "1234", false,
                "lol", null, null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null,
                null);
        List<User> users = List.of(userOne, userTwo);
        Mockito.when(userRepository.findAllById(userIds)).thenReturn(users);

        List<UserDto> listDto = userRepository.findAllById(userIds).stream()
                .map(userMapper::toUserDto)
                .toList();
        List<UserDto> result = List.of(
                new UserDto(1L, "Bob", "ya@ya.ru", "898", "lol"),
                new UserDto(2L, "Rob", "ya@ya.ru", "898", "lol"));
        assertEquals(listDto, result);
    }
}
