package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.randomAvatar.AvatarService;
import school.faang.user_service.service.s3Service.S3Service;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserFilterValidation;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    private UserFilterValidation userFilterValidation;

    private UserMapper userMapper;

    private final UserFilter nameUserFilter = Mockito.mock(UserFilter.class);

    @Mock
    private S3Service s3Service;

    @Mock
    private AvatarService avatarService;

    @Mock
    private UserValidator userValidator;

    private UserService userService;

    private List<UserFilter> filters;

    private User user;
    private UserDto userDto;
    private UserFilterDto userFilterDto;
    //private File avatar;
    private MultipartFile avatar;

    private Country country;

    private UserProfilePic userProfilePic;

    private byte[] avatarBytes;

    @BeforeEach
    void beforeEachInit() {
        user = new User();
        userDto = UserDto.builder()
                .id(1L)
                .countryId(1L)
                .userProfilePic(UserProfilePic.builder()
                        .smallFileId("fileId")
                        .build())
                .build();
        userFilterDto = UserFilterDto.builder().build();

        filters = List.of(nameUserFilter/*, emailUserFilter*/);
        userRepository = Mockito.mock(UserRepository.class);
        userFilterValidation = Mockito.mock(UserFilterValidation.class);
        userMapper = Mockito.mock(UserMapper.class);
        //avatar = new MockMultipartFile("avatar", "avatar", "jpeg", new byte[]{});
        avatar = mock(MockMultipartFile.class);
        //avatar = new File("randomPhoto.svg");
        userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("avatarId");
        user.setUserProfilePic(userProfilePic);

        country = Country.builder().id(1L).build();
        avatarBytes = new byte[100];

        userService = new UserService(userRepository, filters, userFilterValidation, userMapper, avatarService, countryRepository, userValidator);
    }

    @Test
    @DisplayName(value = "Getting premium users throws exception cause userFilter is null")
    public void testGetPremiumUsersWithNullableUserFilter() {
        UserFilterDto userFilterDto = null;

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> userService.getPremiumUsers(userFilterDto));
    }

    @Test
    @DisplayName(value = "Getting premium users returns all users cause userFilters aren't any applicable")
    public void testGetPremiumUsersWithEmptyUserFilter() {
        List<UserDto> expected = List.of(userDto);
        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user));
        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(false);
        when(userMapper.toDtoList(List.of(user))).thenReturn(List.of(userDto));

        List<UserDto> actual = userService.getPremiumUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

    @Test
    @DisplayName(value = "Getting premium users returns filtered users")
    public void testGetPremiumUsersByUserFilter() {
        user = User.builder().id(1L).email("email@email.com").username("user name").build();
        userFilterDto = UserFilterDto.builder().emailPattern("email").namePattern("name").build();
        userDto = UserDto.builder().id(1L).username("user name").build();
        List<UserDto> expected = List.of(userDto);

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user));

        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(true);

        when(filters.get(0).isApplicable(any())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(user));

        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> actual = userService.getPremiumUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

    @Test
    public void testExistsByIdReturnsTrue() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(true);
        boolean exists = userService.existsById(userId);

        assertThrows(DataValidationException.class, () -> userService.getRegularUsers(userFilterDto));
        assertTrue(exists);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName(value = "Getting regular users returns all users cause userFilters aren't any applicable")
    public void testGetRegularUsersWithEmptyUserFilter() {
        List<UserDto> expected = List.of(userDto);
        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findRegularUsers()).thenReturn(Stream.of(user));
        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(false);
        when(userMapper.toDtoList(List.of(user))).thenReturn(List.of(userDto));

        List<UserDto> actual = userService.getRegularUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

    @Test
    @DisplayName(value = "Getting premium users returns filtered users")
    public void testGetRegularUsersByUserFilter() {
        user = User.builder().id(1L).email("email@email.com").username("user name").build();
        userFilterDto = UserFilterDto.builder().emailPattern("email").namePattern("name").build();
        userDto = UserDto.builder().id(1L).username("user name").build();
        List<UserDto> expected = List.of(userDto);

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findRegularUsers()).thenReturn(Stream.of(user));

        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(true);

        when(filters.get(0).isApplicable(any())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(user));

        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> actual = userService.getRegularUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

    @Test
    public void testExistsByIdReturnsFalse() {
        Long userId = 2L;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean exists = userService.existsById(userId);

        assertFalse(exists);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    public void testCreateUserSuccessWithAvatar() {
        when(countryRepository.findById(userDto.getCountryId())).thenReturn(Optional.of(country));
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.createUser(userDto, null);

        verify(countryRepository, times(1)).findById(userDto.getCountryId());
        verify(userRepository, times(2)).save(user);
    }

    @Test
    public void testGetByIdUserFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(user);
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto, result);
    }

    @Test
    public void testGetByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getById(1L);
        });

        assertEquals("User with 1 id doesn't exist", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    @DisplayName("Получение аватара по userId - пользователь не найден")
    void testGetAvatarUserNotFound() {
        // Mock случая, когда пользователь не найден
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Ожидаем, что будет выброшено исключение
        assertThrows(EntityNotFoundException.class, () -> userService.getAvatar(1L));

        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(avatarService);
    }

    @Test
    @DisplayName("Получение аватара по userId - успешно")
    void testGetAvatarSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(avatarService.get(any(String.class))).thenReturn(avatarBytes);

        byte[] avatar = userService.getAvatar(1L);

        assertArrayEquals(avatarBytes, avatar);
        verify(userRepository, times(1)).findById(1L);
        verify(avatarService, times(1)).get(any(String.class));
    }
}
