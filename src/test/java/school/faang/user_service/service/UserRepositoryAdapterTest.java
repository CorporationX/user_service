package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.adapter.UserRepositoryAdapter;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void testGetUserById_Success() {
        long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userRepositoryAdapter.getUserById(userId);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userRepositoryAdapter.getUserById(userId));

        verify(userRepository).findById(userId);
    }
}
