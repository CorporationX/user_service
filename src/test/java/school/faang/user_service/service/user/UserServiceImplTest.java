package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapper userMapper;
    @Mock
    private UserContext userContext;

    private static final long USER_ID = 1L;
    private static final long USER_REQUESTER_ID = 1L;
    private static final long COUNTRY_ID = 7L;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private User user;
    private Country country;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);

        country = new Country();
        country.setId(COUNTRY_ID);

        user.setCountry(country);
        ReflectionTestUtils.setField(userService, "minPasswordLength", MIN_PASSWORD_LENGTH);
    }

    @Test
    public void shouldCreateUserWhenDataIsValid() {
        CreateUserDto userDto = new CreateUserDto(
                "User",
                "user@gmail.com",
                "123456789",
                COUNTRY_ID
        );

        UserDto expected = createUserDto(USER_ID);

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(countryRepository.getByIdOrThrow(userDto.countryId())).thenReturn(country);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto result = userService.create(userDto);

        assertEquals(expected, result);
        verify(userMapper).toUser(userDto);
        verify(countryRepository).getByIdOrThrow(userDto.countryId());
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    public void shouldThrowExceptionWhenPasswordShort() {
        CreateUserDto userDto = new CreateUserDto(
                "User",
                "user@gmail.com",
                "1234",
                COUNTRY_ID
        );

        assertThrows(DataValidationException.class, () ->
                userService.create(userDto)
        );
    }

    @Test
    public void shouldThrowExceptionWhenPasswordExactlyMinLength() {
        CreateUserDto userDto = new CreateUserDto(
                "User",
                "user@gmail.com",
                "123456",
                COUNTRY_ID
        );

        UserDto expected = createUserDto(USER_ID);

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(countryRepository.getByIdOrThrow(userDto.countryId())).thenReturn(country);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto result = userService.create(userDto);

        assertEquals(expected, result);
    }

    @Test
    public void shouldUpdateUserWhenDataIsValid() {
        UpdateUserDto updateUserDto = new UpdateUserDto(
                "Петя",
                "petya@mail.ru",
                "+89290295131",
                "hardworking",
                COUNTRY_ID,
                "New York"
        );

        UserDto expected = new UserDto(
                USER_ID,
                "Петя",
                "petya@mail.ru",
                "+89290295131",
                "hardworking"
        );

        when(userContext.getUserId()).thenReturn(USER_REQUESTER_ID);
        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);
        when(countryRepository.getByIdOrThrow(updateUserDto.countryId())).thenReturn(country);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto result = userService.update(USER_ID, updateUserDto);

        assertEquals(expected, result);
        verify(userContext).getUserId();
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(userMapper).update(updateUserDto, user);
        verify(countryRepository).getByIdOrThrow(updateUserDto.countryId());
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    public void shouldGetUserById() {
        UserDto expected = createUserDto(USER_ID);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto result = userService.getById(USER_ID);

        assertEquals(expected, result);
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(userMapper).toUserDto(user);
    }

    @Test
    public void shouldGetUser() {
        UserDto expected = createUserDto(USER_ID);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto result = userService.getUser(USER_ID);

        assertEquals(expected, result);
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(userMapper).toUserDto(user);
    }

    @Test
    public void shouldGetUsersByIds() {
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);

        List<Long> ids = Arrays.asList(USER_ID, 2L, 3L);
        List<User> users = Arrays.asList(user, user2, user3);

        UserDto userDto1 = createUserDto(USER_ID);
        UserDto userDto2 = createUserDto(2L);
        UserDto userDto3 = createUserDto(3L);
        List<UserDto> expected = Arrays.asList(userDto1, userDto2, userDto3);

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toUserDtoList(users)).thenReturn(expected);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(expected, result);
        assertEquals(3, result.size());
        verify(userRepository).findAllById(ids);
        verify(userMapper).toUserDtoList(users);
    }

    @Test
    public void shouldReturnEmptyListWhenNoUsersFound() {
        List<Long> ids = List.of(999L, 998L);
        List<User> emptyList = List.of();
        List<UserDto> expectedEmpty = List.of();

        when(userRepository.findAllById(ids)).thenReturn(emptyList);
        when(userMapper.toUserDtoList(emptyList)).thenReturn(expectedEmpty);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(0, result.size());
        verify(userRepository).findAllById(ids);
        verify(userMapper).toUserDtoList(emptyList);
    }

    private UserDto createUserDto(long id) {
        return new UserDto(
                id,
                "User",
                "user@gmail.com",
                "+77777777777",
                "Promising"
        );
    }
}