package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserFilterValidation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.s3.FileDownloadException;
import school.faang.user_service.exception.s3.FileUploadException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.randomAvatar.RandomAvatarService;
import school.faang.user_service.service.s3Service.S3Service;
import school.faang.user_service.service.userService.UserService;
import school.faang.user_service.validation.user.UserValidator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserFilterValidation userFilterValidation;

    private UserMapper userMapper;

    private UserFilter nameUserFilter = Mockito.mock(UserFilter.class);
    private S3Service s3Service;

    @Mock
    private RandomAvatarService randomAvatarService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private List<UserFilter> filters;

    private User user;
    private UserDto userDto;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void beforeEachInit() {
        user = new User();
        userDto = UserDto.builder().build();
        userFilterDto = UserFilterDto.builder().build();

        filters = List.of(nameUserFilter/*, emailUserFilter*/);
        userRepository = Mockito.mock(UserRepository.class);
        userFilterValidation = Mockito.mock(UserFilterValidation.class);
        userMapper = Mockito.mock(UserMapper.class);

        userService = new UserService(userRepository, filters, userFilterValidation, userMapper);
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
}
    private User user;
    private File randomPhoto;

    private UserProfilePic userProfilePic;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("name");

        randomPhoto = new File("randomPhoto.svg");

        userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("avatarId");

        user.setUserProfilePic(userProfilePic);
    }

    @Test
    public void generateRandomAvatarUserNotCurrent() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(false);

        Assertions.assertThrows(DataValidationException.class, () -> userService.generateRandomAvatar(1L));
    }

    @Test
    public void generateRandomAvatarUserNotFound() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.generateRandomAvatar(1L));
    }

    @Test
    public void generateRandomAvatarFileUploadFailed() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(randomAvatarService.getRandomPhoto()).thenReturn(randomPhoto);
        when(s3Service.uploadFile(any(File.class), any(String.class))).thenReturn(null);

        assertThrows(FileUploadException.class, () -> userService.generateRandomAvatar(1L));
    }

    @Test
    public void generateRandomAvatarSuccess() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(randomAvatarService.getRandomPhoto()).thenReturn(randomPhoto);
        when(s3Service.uploadFile(any(File.class), any(String.class))).thenReturn("avatarId");

        String avatarId = userService.generateRandomAvatar(1L);

        assertEquals("avatarId", avatarId);
    }

    @Test
    public void getUserRandomAvatarUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.getUserRandomAvatar(1L));
    }

    @Test
    public void getUserRandomAvatarAvatarIdNull() {
        userProfilePic.setFileId(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(FileDownloadException.class, () -> userService.getUserRandomAvatar(1L));
    }

    @Test
    public void getUserRandomAvatarSuccess() {
        byte[] avatarBytes = new byte[]{1, 2, 3, 4};
        InputStream avatarStream = new ByteArrayInputStream(avatarBytes);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(s3Service.getFile("avatarId")).thenReturn(avatarStream);

        byte[] result = userService.getUserRandomAvatar(1L);

        assertArrayEquals(avatarBytes, result);
    }
}
