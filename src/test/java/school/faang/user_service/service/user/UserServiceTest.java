package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataGettingException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("nadir");
        user.setAboutMe("About nadir");
        user.setEmail("nadir@gmail.com");
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return optional of user when such user exists")
        @Test
        void getUser() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertDoesNotThrow(() -> userService.getUser(anyLong()));

            verify(userMapper).toDto(user);
        }

        @Test
        void existsById() {
            assertDoesNotThrow(() -> userService.existsById(anyLong()));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataGettingException when such user doesn't exist")
        @Test
        void getUser() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(DataGettingException.class, () -> userService.getUser(anyLong()));

            verifyNoInteractions(userMapper);
        }
    }
}