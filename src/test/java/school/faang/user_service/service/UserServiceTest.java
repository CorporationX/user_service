package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User requester;
    private User receiver;

    @BeforeEach
    void setUp() {
        requester = createUser(1L, "Requester");
        receiver = createUser(2L, "Receiver");
    }

    @Test
    void testGetUserById_Successfully() {
        long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(requester));

        User user = userService.getUserById(id);

        assertNotNull(user);
        assertEquals(requester.getId(), user.getId());
        assertEquals(requester, user);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testGetUserById_WithError() {
        long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(receiver));

        User user = userService.getUserById(id);

        assertNotNull(user);
        //assertEquals(requester.getId(), user.getId());
        //assertEquals(requester, user);
        assertEquals(receiver.getId(), user.getId());
        assertEquals(receiver, user);
        verify(userRepository, times(1)).findById(id);
    }



    public static User createUser(long id, String title) {
        return User.builder()
                .id(id)
                .username(title)
                .build();
    }

}
