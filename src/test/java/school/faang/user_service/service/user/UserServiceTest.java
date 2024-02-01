package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private UserService userService;
    private static final long EXISTENT_USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;
    private User existentUser;

    @BeforeEach
    public void init() {
        existentUser = new User();
        existentUser.setId(EXISTENT_USER_ID);
    }

    @Test
    public void testGetExistingUserById_userExists_returnsUser() {
        mockUserFindBiId(EXISTENT_USER_ID);
        User existingUserById = userService.getExistingUserById(EXISTENT_USER_ID);
        assertEquals(existingUserById, existentUser);
    }

    @Test
    public void testGetExistingUserById_userNotExist_throwsEntityNotFoundException() {
        mockUserFindBiId(NON_EXISTENT_USER_ID);
        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getExistingUserById(NON_EXISTENT_USER_ID)
        );
    }

    @Test
    public void testGetUserById_userExists_returnsUserDto() {
        mockUserFindBiId(EXISTENT_USER_ID);
        UserDto existingUserById = userService.getUser(EXISTENT_USER_ID);
        assertEquals(existingUserById, userMapper.toDto(existentUser));
    }

    @Test
    public void testGetUserById_userNotExists_throwsEntityNotFoundException() {
        mockUserFindBiId(NON_EXISTENT_USER_ID);
        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getExistingUserById(NON_EXISTENT_USER_ID)
        );
    }

    private void mockUserFindBiId(long id) {
        Optional<User> userOpt = id == EXISTENT_USER_ID ? Optional.of(existentUser) : Optional.empty();
        Mockito.when(userRepository.findById(id)).thenReturn(userOpt);
    }
}
