package school.faang.user_service.requestformentoring.services;

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
import school.faang.user_service.services.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private final User requester = new User();
    private final User receiver = new User();
    private final List<User> usersDB = new ArrayList<>();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        requester.setId(REQUESTER_ID);
        receiver.setId(RECEIVER_ID);

        usersDB.add(receiver);
        usersDB.add(requester);
    }

    @Nested
    class positiveTest {

        @Test
        @DisplayName("Успешное получение пользователей")
        void whenGetThenReturn() {
            List<Long> usersId = new ArrayList<>();
            usersId.add(REQUESTER_ID);
            usersId.add(RECEIVER_ID);

            when(userRepository.findAllById(usersId)).thenReturn(usersDB);

            List<User> result = userService.getUsersById(usersId);

            assertNotNull(result);
            assertEquals(result.size(), usersDB.size());
            assertEquals(result, usersDB);

        }
    }
}