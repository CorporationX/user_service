package school.faang.user_service.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFindUserByIdFailed() {
        Mockito.when(userRepo.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(1L));
    }

    @Test
    public void testFindUserByIdSuccess() {
        User user = new User();
        user.setId(1L);
        Mockito.lenient().when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        Assertions.assertEquals(user, userService.findUserById(1L));
    }
}
