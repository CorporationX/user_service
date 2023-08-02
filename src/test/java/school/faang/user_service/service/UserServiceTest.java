package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

 /*   @Test
    public void testFindUserThrowEntityExc() {
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1L));
    }*/

    @Test
    public void testFindUserCallFindById() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        userService.findUserById(1L);
    }
}