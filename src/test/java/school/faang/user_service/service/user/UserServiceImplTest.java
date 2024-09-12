package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final long id = 1L;

    @Test
    void existsProjectById_ExistsProject() {
        when(userRepository.existsById(id)).thenReturn(true);

        boolean exists = userService.existsUserById(id);

        assertTrue(exists);
        verify(userRepository).existsById(id);
    }

    @Test
    void existsProjectById_NotExistsProject() {
        when(userRepository.existsById(id)).thenReturn(false);

        boolean exists = userService.existsUserById(id);

        assertFalse(exists);
        verify(userRepository).existsById(id);
    }
}