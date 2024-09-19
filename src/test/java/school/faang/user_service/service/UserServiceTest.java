package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private static final long ID = 1L;

    @Test
    @DisplayName("Успех если user существует")
    public void whenFindByIdWithExistedUserThenSuccess() {
        User existedUser = User.builder()
                .id(ID)
                .build();
        when(userRepository.findById(ID)).thenReturn(Optional.of(existedUser));

        User resultUser = userService.findById(ID);

        assertNotNull(resultUser);
        assertEquals(existedUser, resultUser);
    }

    @Test
    @DisplayName("Ошибка если user не существует")
    public void whenFindByIdWithNotExistedUserThenException() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.findById(ID));
        verify(userRepository).findById(ID);
    }
}