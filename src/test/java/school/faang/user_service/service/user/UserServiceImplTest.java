package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.TestS3Config;
import school.faang.user_service.avatar.dto.AvatarDto;
import school.faang.user_service.avatar.service.UserAvatarService;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.filter.FilterService;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(TestS3Config.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private FilterService<User, UserFilterDto> filterService;
    @Mock
    private UserAvatarService avatarService;
    @Mock
    private ExecutorService executor;
    @InjectMocks
    private UserServiceImpl service;

    @Test
    void create_success() {
        var country = UserServiceTestData.buildCountry(1L, "Kazakhstan");
        var createDto = UserServiceTestData.buildCreateDto("Myrzakhmet", 1L);
        var currentUser = UserServiceTestData.buildLiteUser(null, createDto, country);
        var user = UserServiceTestData.buildLiteUser(1L, createDto, country);
        final var userDto = UserServiceTestData.toViewDto(user);

        when(countryRepository.getByIdOrThrow(country.getId()))
                .thenReturn(country);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(avatarService.generateAndUpload(eq(createDto.username())))
                .thenReturn(new AvatarDto());

        var actual = service.create(createDto);
        assertEquals(userDto.id(), actual.id());
        assertEquals(userDto.username(), actual.username());
        assertEquals(userDto.email(), actual.email());
        assertEquals(userDto.phone(), actual.phone());
        assertEquals(userDto.aboutMe(), actual.aboutMe());

    }

    @Test
    void update() {
        var country = UserServiceTestData.buildCountry(1L, "America");
        var user = UserServiceTestData.buildFullUser(1L, "Myrzakhmet", country);
        var updateDto = UserServiceTestData.buildUpdateDto(user);
        var newUser = userMapper.clone(user);
        userMapper.update(updateDto, newUser);

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.getByIdOrThrow(user.getId()))
                .thenReturn(user);
        when(countryRepository.getByIdOrThrow(updateDto.countryId()))
                .thenReturn(country);
        when(userRepository.save(eq(newUser)))
                .thenReturn(newUser);

        var expected = userMapper.toUserDto(newUser);
        var actual = service.update(user.getId(), updateDto);
        assertEquals(expected, actual);
    }

    @Test
    void getById() {
        var country = UserServiceTestData.buildCountry(1L, "CountryName");
        var user = UserServiceTestData.buildFullUser(1L, "user", country);

        when(userRepository.getByIdOrThrow(user.getId())).thenReturn(user);

        var expected = userMapper.toUserDto(user);
        var actual = service.getById(user.getId());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("school.faang.user_service.service.user.UserServiceTestData#provideParams")
    void getUsers(UserFilterDto filterDto, List<User> users,
                  List<User> filteredUsers, List<UserDto> expected) {
        if (filterDto.onlyPremium()) {
            when(userRepository.findPremiumUsers())
                    .thenReturn(users.stream());
        } else {
            when(userRepository.findAll()).thenReturn(users);
        }

        when(filterService.getFilteredList(eq(users), eq(filterDto)))
                .thenReturn(filteredUsers);

        var actual = service.getUsers(filterDto);
        assertEquals(expected, actual);
    }
}