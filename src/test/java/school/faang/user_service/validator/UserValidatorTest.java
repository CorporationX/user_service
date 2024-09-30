package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserValidator userValidator;

    private final User testUser = new User();

    private User prepareTestingUser() {
        testUser.setUsername("KateBraun");
        testUser.setEmail("kate@example.com");
        testUser.setPhone("1112223333");
        return testUser;
    }

    @Test
    public void testPrepareAndSaveUsersIfUsernameAlreadyExist() {
        User savedUser = prepareTestingUser();
        User userFromDB = User.builder()
                .username(savedUser.getUsername())
                .build();
        when(userRepository.findByUsernameOrEmailOrPhone(anyString(), anyString(), anyString()))
                .thenReturn(List.of(userFromDB));

        boolean testUsersIsExists = userValidator.validateUserBeforeSave(savedUser);

        assertTrue(testUsersIsExists);
    }

    @Test
    public void testPrepareAndSaveUsersIfEmailAlreadyExist() {
        User savedUser = prepareTestingUser();
        User userFromDB = User.builder()
                .email(savedUser.getEmail())
                .build();
        when(userRepository.findByUsernameOrEmailOrPhone(anyString(), anyString(), anyString()))
                .thenReturn(List.of(userFromDB));

        boolean testUsersIsExists = userValidator.validateUserBeforeSave(savedUser);

        assertTrue(testUsersIsExists);
    }

    @Test
    public void testPrepareAndSaveUsersIfPhoneAlreadyExist() {
        User savedUser = prepareTestingUser();
        User userFromDB = User.builder()
                .phone(savedUser.getPhone())
                .build();
        when(userRepository.findByUsernameOrEmailOrPhone(anyString(), anyString(), anyString()))
                .thenReturn(List.of(userFromDB));

        boolean testUsersIsExists = userValidator.validateUserBeforeSave(savedUser);

        assertTrue(testUsersIsExists);
    }

    @Test
    public void testPrepareAndSaveUsersIfUserNotExist() {
        User savedUser = prepareTestingUser();
        when(userRepository.findByUsernameOrEmailOrPhone(anyString(), anyString(), anyString()))
                .thenReturn(List.of());

        boolean testUsersIsExists = userValidator.validateUserBeforeSave(savedUser);

        assertFalse(testUsersIsExists);
    }
}
