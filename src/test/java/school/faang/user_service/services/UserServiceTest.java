package school.faang.user_service.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    private User requester = new User();
    private User receiver = new User();
    private List<User> dBUsers = new ArrayList<>();
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;

    @BeforeEach
    void init() {
        receiver = User.builder()
                .id(REQUESTER_ID)
                .id(RECEIVER_ID)
                .build();

        dBUsers.add(receiver);
        dBUsers.add(requester);
    }

    @Nested
    class positiveTest {
        @Test
        @DisplayName("Успешное получение пользователей")
        void whenGetThenReturn() {
            List<Long> usersId = new ArrayList<>();
            usersId.add(REQUESTER_ID);
            usersId.add(RECEIVER_ID);

            when(userRepository.findAllById(usersId)).thenReturn(dBUsers);

            List<User> result = userService.getUsersById(usersId);

            assertNotNull(result);
            assertEquals(result.size(), dBUsers.size());
            assertEquals(result, dBUsers);
        }
    }
}