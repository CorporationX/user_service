package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.test_data.user.TestDataUser;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        TestDataUser testDataEvent = new TestDataUser();

        user = testDataEvent.getUser();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testBanUserSuccess() {
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            userService.banUser(user.getId());

            assertTrue(user.isBanned());
            verify(userRepository, atLeastOnce()).save(user);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testGetUser_NotFound_throwDataValidationException() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            var exception = assertThrows(DataValidationException.class,
                    () -> userService.banUser(1L)
            );

            assertEquals("User with ID: 1 not found.", exception.getMessage());
        }
    }
}
