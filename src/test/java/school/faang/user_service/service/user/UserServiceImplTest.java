package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
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
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final long USER_ID = 10L;
    private static final long REQUESTER_ID = 10L;
    private static final long OTHER_USER_ID = 20L;

    private static final long COUNTRY_ID = 1L;
    private static final long OTHER_COUNTRY_ID = 2L;
    private static final long INVALID_COUNTRY_ID = 999L;

    private static final String USERNAME = "john";
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD_VALID = "veryStrongPass";
    private static final String PASSWORD_SHORT = "short";
    private static final String PASSWORD_MIN_OK = "12345678";

    private static final String UPDATED_USERNAME = "johnny";
    private static final String UPDATED_EMAIL = "johnny@example.com";
    private static final String UPDATED_PHONE = "+65 1234 5678";
    private static final String UPDATED_ABOUT = "About me updated";
    private static final String UPDATED_CITY = "Singapore";

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "minPasswordLength", MIN_PASSWORD_LENGTH);
    }

    @Test
    @DisplayName("create: ok -> maps with real mapper, resolves country, saves, returns dto")
    void create_ok() {
        Country country = new Country();
        country.setId(COUNTRY_ID);

        when(countryRepository.getByIdOrThrow(COUNTRY_ID)).thenReturn(country);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0, User.class);
            u.setId(USER_ID);
            return u;
        });

        CreateUserDto input = new CreateUserDto(USERNAME, EMAIL, PASSWORD_VALID, COUNTRY_ID);
        UserDto result = userService.create(input);

        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USERNAME, result.username());
        assertEquals(EMAIL, result.email());

        assertNull(result.phone());
        assertNull(result.aboutMe());

        verify(userMapper).toUser(input);
        verify(countryRepository).getByIdOrThrow(COUNTRY_ID);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDto(any(User.class));
    }

    @Test
    @DisplayName("create: password too short -> DataValidationException (mapper/repo not called)")
    void create_passwordTooShort() {
        CreateUserDto input = new CreateUserDto(USERNAME, EMAIL, PASSWORD_SHORT, COUNTRY_ID);

        DataValidationException ex =
                assertThrows(DataValidationException.class, () -> userService.create(input));
        assertEquals("Password should be more than " + MIN_PASSWORD_LENGTH + " symbols!", ex.getMessage());

        verifyNoInteractions(userMapper, userRepository, countryRepository);
    }

    @Test
    @DisplayName("create: password length exactly min -> ok")
    void create_passwordAtBoundary_ok() {
        Country country = new Country();
        country.setId(COUNTRY_ID);

        when(countryRepository.getByIdOrThrow(COUNTRY_ID)).thenReturn(country);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0, User.class);
            u.setId(USER_ID);
            return u;
        });

        CreateUserDto input = new CreateUserDto(USERNAME, EMAIL, PASSWORD_MIN_OK, COUNTRY_ID);
        UserDto result = userService.create(input);

        assertEquals(USER_ID, result.id());
        verify(countryRepository).getByIdOrThrow(COUNTRY_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("create: invalid country -> propagates exception, save not called")
    void create_invalidCountry() {
        CreateUserDto input = new CreateUserDto(USERNAME, EMAIL, PASSWORD_VALID, INVALID_COUNTRY_ID);

        when(countryRepository.getByIdOrThrow(INVALID_COUNTRY_ID))
                .thenThrow(new RuntimeException("Country not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.create(input));
        assertEquals("Country not found", ex.getMessage());

        verify(countryRepository).getByIdOrThrow(INVALID_COUNTRY_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: sets country on entity saved to repository")
    void create_setsCountryOnEntity() {
        Country country = new Country();
        country.setId(COUNTRY_ID);

        when(countryRepository.getByIdOrThrow(COUNTRY_ID)).thenReturn(country);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0, User.class);
            u.setId(USER_ID);
            return u;
        });

        CreateUserDto input = new CreateUserDto(USERNAME, EMAIL, PASSWORD_VALID, COUNTRY_ID);
        userService.create(input);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertNotNull(saved.getCountry());
        assertEquals(COUNTRY_ID, saved.getCountry().getId());
    }

    @Test
    @DisplayName("update: ok -> requester matches, real mapper updates fields, country resolved, saved, returns dto")
    void update_ok() {
        Country newCountry = new Country();
        newCountry.setId(OTHER_COUNTRY_ID);

        User existing = new User();
        existing.setId(USER_ID);
        existing.setUsername(USERNAME);
        existing.setEmail(EMAIL);

        when(userContext.getUserId()).thenReturn(REQUESTER_ID);
        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(existing);
        when(countryRepository.getByIdOrThrow(OTHER_COUNTRY_ID)).thenReturn(newCountry);
        when(userRepository.save(existing)).thenAnswer(inv -> inv.getArgument(0, User.class));

        UpdateUserDto input = buildUpdateUserDto();
        UserDto result = userService.update(USER_ID, input);

        assertEquals(USER_ID, result.id());
        assertEquals(UPDATED_USERNAME, result.username());
        assertEquals(UPDATED_EMAIL, result.email());

        verify(userContext).getUserId();
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(userMapper).update(input, existing);
        verify(countryRepository).getByIdOrThrow(OTHER_COUNTRY_ID);
        verify(userRepository).save(existing);
        verify(userMapper).toUserDto(existing);
    }

    @Test
    @DisplayName("update: forbidden when requester != userId (no mapper/repo/country calls)")
    void update_forbidden() {
        UpdateUserDto input = buildUpdateUserDto();
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);

        ForbiddenException ex =
                assertThrows(ForbiddenException.class, () -> userService.update(USER_ID, input));

        assertTrue(ex.getMessage().contains(String.valueOf(OTHER_USER_ID)));
        verify(userContext).getUserId();
        verifyNoInteractions(userRepository, countryRepository);
        verify(userMapper, never()).update(any(), any());
        verify(userMapper, never()).toUserDto(any());
    }

    @Test
    @DisplayName("update: invalid country -> propagates exception, save not called")
    void update_invalidCountry_propagates() {
        User existing = new User();
        existing.setId(USER_ID);

        when(userContext.getUserId()).thenReturn(REQUESTER_ID);
        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(existing);
        when(countryRepository.getByIdOrThrow(OTHER_COUNTRY_ID))
                .thenThrow(new RuntimeException("Country not found"));

        UpdateUserDto input = buildUpdateUserDto();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.update(USER_ID, input));
        assertEquals("Country not found", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getById: ok -> repository returns entity, mapper returns dto")
    void getById_ok() {
        User entity = new User();
        entity.setId(USER_ID);
        entity.setUsername(USERNAME);
        entity.setEmail(EMAIL);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(entity);

        UserDto result = userService.getById(USER_ID);

        assertEquals(USER_ID, result.id());
        assertEquals(USERNAME, result.username());
        assertEquals(EMAIL, result.email());
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(userMapper).toUserDto(entity);
    }

    @Test
    @DisplayName("getById: repo throws -> propagates")
    void getById_notFound_propagates() {
        when(userRepository.getByIdOrThrow(USER_ID))
                .thenThrow(new RuntimeException("User not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getById(USER_ID));
        assertEquals("User not found", ex.getMessage());
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(userMapper, never()).toUserDto(any());
    }

    private UpdateUserDto buildUpdateUserDto() {
        return UpdateUserDto.builder()
                .username(UPDATED_USERNAME)
                .email(UPDATED_EMAIL)
                .phone(UPDATED_PHONE)
                .aboutMe(UPDATED_ABOUT)
                .countryId(OTHER_COUNTRY_ID)
                .city(UPDATED_CITY)
                .build();
    }

}