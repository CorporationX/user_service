package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final long ANY_ID = 123L;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Exception when the user with this ID doesn't exist")
    void whenUserNotExistThenThrowException() {
        when(userRepository.findById(ANY_ID)).thenReturn(null);
        assertThrows(NullPointerException.class,
                () -> userService.getUser(ANY_ID), "User with id " + ANY_ID + " doesn't exist");
    }
}