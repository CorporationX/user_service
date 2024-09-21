package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.RemoteImageService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RemoteImageService remoteImageService;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserValidator userValidator;

    private UserRegistrationDto userRegistrationDto;
    private User user;

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
        @DisplayName("Success when user registration and got remote profile picture correct")
        void whenUserRegistrationDtoIsValidAndRemoteServiceSendPicFoundThenSuccess() throws IOException {
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
        void whenUserRegistrationDtoIsValidAndRemoteServiceNotSendPicFoundThenSuccess() throws IOException {
            userRegistrationDto = new UserRegistrationDto();
            user = new User();

            when(userMapper.toEntity(userRegistrationDto))
                    .thenReturn(user);
            when(remoteImageService.getUserProfileImageFromRemoteService())
                    .thenThrow(new IOException());
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
        void whenUserRegistrationDtoIsValidAndNoLocalOrRemotePictureThenSuccess() throws IOException {
            userRegistrationDto = new UserRegistrationDto();
            user = new User();

            when(userMapper.toEntity(userRegistrationDto))
                    .thenReturn(user);
            when(remoteImageService.getUserProfileImageFromRemoteService())
                    .thenThrow(new IOException());
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
