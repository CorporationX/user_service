package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.remote.ImageGeneratorException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.RemoteImageService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final long USER_ID = 1L;

    private static final int TWO_TIMES_USES_REPOSITORY = 2;

    private static final List<Long> USER_IDS = List.of(USER_ID);

    private static final String USER_NAME = "name";

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private RemoteImageService remoteImageService;
    @Mock
    private S3Service s3Service;

    private User user;
    private User secondUser;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;
    private UserRegistrationDto userRegistrationDto;

    @BeforeEach
    public void init() {
        secondUser = User.builder()
                .id(USER_ID)
                .username(USER_NAME)
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .username(USER_NAME)
                .build();

        users = List.of(secondUser);
        userDtos = List.of(userDto);
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Throws exception when user not found")
        void whenUserNotFoundThenThrowException() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.getUserById(anyLong()),
                    "User with this id does not exist in the database");
        }

        @Test
        @DisplayName("Ошибка если user не существует")
        public void whenUserIsNotExistThenThrowException() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(ValidationException.class, () -> userService.getUser(USER_ID));
        }

        @Test
        @DisplayName("Вернет пустой список users если users не существует")
        public void whenUsersIsNotExistThenReturnEmptyList() {
            when(userRepository.findAllById(USER_IDS)).thenReturn(List.of());
            when(userMapper.toDtos(List.of())).thenReturn(new ArrayList<>());

            List<UserDto> resultUserDros = userService.getUsersByIds(USER_IDS);

            assertEquals(0, resultUserDros.size());
            verify(userValidator).validateUserIdIsPositiveAndNotNull(USER_ID);
            verify(userRepository).findAllById(USER_IDS);
            verify(userMapper).toDtos(List.of());
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Doesn't throws exception when user found")
        void whenUserNotFoundThenNotThrownException() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            assertNotNull(userService.getUserById(anyLong()));
        }

        @Test
        @DisplayName("Успех если user существует")
        public void whenUserIsExistThenSuccess() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(secondUser));
            when(userMapper.toDto(secondUser)).thenReturn(userDto);

            UserDto resultUserDto = userService.getUser(USER_ID);

            assertNotNull(resultUserDto);
            verify(userRepository).findById(USER_ID);
            verify(userMapper).toDto(secondUser);
        }

        @Test
        @DisplayName("Успех если users существуют")
        public void whenUsersIsExistThenSuccess() {
            when(userRepository.findAllById(USER_IDS)).thenReturn(users);
            when(userMapper.toDtos(users)).thenReturn(userDtos);

            List<UserDto> resultUserDros = userService.getUsersByIds(USER_IDS);

            assertNotNull(resultUserDros);
            verify(userValidator).validateUserIdIsPositiveAndNotNull(USER_ID);
            verify(userRepository).findAllById(USER_IDS);
            verify(userMapper).toDtos(users);
        }

        @Nested
        class UserRegistration {

            @Test
            @DisplayName("Success when user registration and got remote profile picture correct")
            void whenUserRegistrationDtoIsValidAndRemoteServiceSendPicFoundThenSuccess() {
                userRegistrationDto = new UserRegistrationDto();
                user = new User();

                when(userMapper.toEntity(userRegistrationDto))
                        .thenReturn(user);

                userService.registerUser(userRegistrationDto);

                verify(userMapper)
                        .toEntity(userRegistrationDto);
                verify(userValidator)
                        .validateUserConstrains(user);
                verify(userRepository, times(TWO_TIMES_USES_REPOSITORY))
                        .save(user);
                verify(userMapper)
                        .toDto(user);
                verify(remoteImageService)
                        .getUserProfileImageFromRemoteService();
                verify(s3Service)
                        .uploadHttpData(any(), anyString());

            }

            @Test
            @DisplayName("Success when user registration and don't get remote profile picture then get local pic")
            void whenUserRegistrationDtoIsValidAndRemoteServiceNotSendPicFoundThenSuccess() {
                userRegistrationDto = new UserRegistrationDto();
                user = new User();

                when(userMapper.toEntity(userRegistrationDto))
                        .thenReturn(user);
                when(remoteImageService.getUserProfileImageFromRemoteService())
                        .thenThrow(new ImageGeneratorException(null));
                when(s3Service.isDefaultPictureExistsOnCloud())
                        .thenReturn(Boolean.TRUE);

                userService.registerUser(userRegistrationDto);

                verify(userMapper)
                        .toEntity(userRegistrationDto);
                verify(userValidator)
                        .validateUserConstrains(user);
                verify(userRepository, times(TWO_TIMES_USES_REPOSITORY))
                        .save(user);
                verify(userMapper)
                        .toDto(user);
                verify(s3Service)
                        .getDefaultPictureName();
            }

            @Test
            @DisplayName("Success when user registration and don't get remote profile picture and don't get local pic")
            void whenUserRegistrationDtoIsValidAndNoLocalOrRemotePictureThenSuccess() {
                userRegistrationDto = new UserRegistrationDto();
                user = new User();

                when(userMapper.toEntity(userRegistrationDto))
                        .thenReturn(user);
                when(remoteImageService.getUserProfileImageFromRemoteService())
                        .thenThrow(new ImageGeneratorException(null));
                when(s3Service.isDefaultPictureExistsOnCloud())
                        .thenReturn(Boolean.FALSE);

                userService.registerUser(userRegistrationDto);

                verify(userMapper)
                        .toEntity(userRegistrationDto);
                verify(userValidator)
                        .validateUserConstrains(user);
                verify(userRepository, times(TWO_TIMES_USES_REPOSITORY))
                        .save(user);
                verify(userMapper)
                        .toDto(user);
            }
        }
    }
}