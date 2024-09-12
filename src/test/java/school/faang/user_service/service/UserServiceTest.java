package school.faang.user_service.service;

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
import school.faang.user_service.test_data.event.TestDataEvent;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private TestDataEvent testDataEvent;

    @BeforeEach
    void setUp() {
        testDataEvent = new TestDataEvent();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testGetUser_Success() {
            User user = testDataEvent.getUser();

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            User result = userService.getUserById(user.getId());
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals("User1", result.getUsername());
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testGetUser_NotFound_throwDataValidationException() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            var exception = assertThrows(DataValidationException.class,
                    () -> userService.getUserById(1L)
            );

            assertEquals("User with ID: 1 not found", exception.getMessage());
        }
    }
}
