package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ScheduledUserServiceTest {
    @InjectMocks
    private ScheduledUserService scheduledUserService;
    @Mock
    private UserRepository userRepository;

        @Test
    public void successDeleteNonActiveUser() {
        long months = 3L;
        LocalDateTime timeToDelete = LocalDateTime.now().minusMonths(months);
        scheduledUserService.deleteNonActiveUsers();
        Mockito.verify(userRepository, times(1)).deleteAllInactiveUsersAndUpdatedAtOverMonths(timeToDelete);
    }
}
