package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private static final long USER_ID = 1L;
    private static final long USER_TWO_ID = 2L;
    private static final long COUNTRY_ID = 77L;
    private static final String COUNTRY = "USA";
    private static final String USER_NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "JOn1234!";
    private static final String ABOUT_ME = "aboutMe";

    @Test
    @DisplayName("Should get the user by ID")
    public void testGetUser() {
        User user = createUser(USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto dto = userService.getUser(USER_ID);
        UserDto result = createUserDto(USER_ID);

        assertEquals(dto, result);

        verify(userMapper).toUserDto(user);
    }

    @Test
    @DisplayName("Should get users from the list of IDs")
    public void testGetUsersByIds() {
        List<Long> userIds = List.of(USER_ID, USER_TWO_ID);
        User userOne = createUser(USER_ID);
        User userTwo = createUser(USER_TWO_ID);
        List<User> users = List.of(userOne, userTwo);

        when(userRepository.findAllById(userIds)).thenReturn(users);

        List<UserDto> listDto = userService.getUsersByIds(userIds);
        List<UserDto> result = List.of(createUserDto(USER_ID), createUserDto(USER_TWO_ID));

        assertEquals(listDto, result);

        verify(userMapper).toUserDto(userOne);
        verify(userMapper).toUserDto(userTwo);
    }

    @Test
    @DisplayName("Should create a new user")
    public void testCreateUser() {
        CreateUserDto createUserDto = createCreateDto();
        User user = createUser(USER_ID);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(countryRepository.getByIdOrThrow(createUserDto.countryId())).thenReturn(createCountry());

        UserDto result = userService.create(createUserDto);

        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USER_NAME, result.username());
        assertEquals(EMAIL, result.email());

        verify(userMapper).toUser(createUserDto);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDto(user);
    }

    private CreateUserDto createCreateDto() {
        return new CreateUserDto(USER_NAME, EMAIL, PASSWORD, COUNTRY_ID);
    }

    private User createUser(long id) {
        return User.builder()
                .id(id)
                .username(USER_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .aboutMe(ABOUT_ME)
                .country(createCountry())
                .build();
    }

    private UserDto createUserDto(long id) {
        return new UserDto(id, USER_NAME, EMAIL, null, ABOUT_ME);
    }

    private Country createCountry() {
        return Country.builder()
                .id(COUNTRY_ID)
                .title(COUNTRY)
                .build();
    }
}