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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final long USER_ID = 1L;
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;

    private static final String USER_NAME = "name";

    private static final List<Long> USER_IDS = List.of(USER_ID);

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
    private User receiver;
    private User requester;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;
    private UserRegistrationDto userRegistrationDto;

    @BeforeEach
    public void init() {
        requester = User.builder()
                .id(REQUESTER_ID)
                .username(USER_NAME)
                .build();

        receiver = User.builder()
                .id(REQUESTER_ID)
                .id(RECEIVER_ID)
                .build();

        userDto = UserDto.builder()
                .id(REQUESTER_ID)
                .username(USER_NAME)
                .build();

        users = List.of(requester);
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
            when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.empty());

            assertThrows(ValidationException.class, () -> userService.getUser(REQUESTER_ID));
        }

        @Test
        @DisplayName("Вернет пустой список users если users не существует")
        public void whenUsersIsNotExistThenReturnEmptyList() {
            when(userRepository.findAllById(USER_IDS)).thenReturn(List.of());
            when(userMapper.toDtos(List.of())).thenReturn(new ArrayList<>());

            List<UserDto> resultUserDros = userService.getUsersByIds(USER_IDS);

            assertEquals(0, resultUserDros.size());
            verify(userValidator).validateUserIdIsPositiveAndNotNull(REQUESTER_ID);
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
            when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.ofNullable(requester));
            when(userMapper.toDto(requester)).thenReturn(userDto);

            UserDto resultUserDto = userService.getUser(REQUESTER_ID);

            assertNotNull(resultUserDto);
            verify(userRepository).findById(REQUESTER_ID);
            verify(userMapper).toDto(requester);
        }

        @Test
        @DisplayName("Успех если users существуют")
        public void whenUsersIsExistThenSuccess() {
            when(userRepository.findAllById(USER_IDS)).thenReturn(users);
            when(userMapper.toDtos(users)).thenReturn(userDtos);

            List<UserDto> resultUserDros = userService.getUsersByIds(USER_IDS);

            assertNotNull(resultUserDros);
            verify(userValidator).validateUserIdIsPositiveAndNotNull(REQUESTER_ID);
            verify(userRepository).findAllById(USER_IDS);
            verify(userMapper).toDtos(users);
        }

        @Test
        @DisplayName("Успешное получение пользователей")
        void whenGetThenReturn() {
            users = List.of(receiver, requester);

            List<Long> usersId = new ArrayList<>();
            usersId.add(REQUESTER_ID);
            usersId.add(RECEIVER_ID);

            when(userRepository.findAllById(usersId)).thenReturn(users);

            List<User> result = userService.getUsersById(usersId);

            assertNotNull(result);
            assertEquals(result.size(), users.size());
            assertEquals(result, users);

            verify(userRepository).findAllById(usersId);
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
                verify(userRepository)
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
                verify(userRepository)
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
                verify(userRepository)
                        .save(user);
                verify(userMapper)
                        .toDto(user);
            }
        }
    }
}