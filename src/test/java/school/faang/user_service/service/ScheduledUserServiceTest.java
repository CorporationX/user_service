package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ScheduledUserServiceTest {
    @InjectMocks
    private ScheduledUserService scheduledUserService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void successDeleteNonActiveUsers() {
        long months = 3L;
        LocalDate localDate = LocalDate.now().minusMonths(months);
        scheduledUserService.deleteNonActiveUsers();
        Mockito.verify(userRepository, times(1)).deleteAllInactiveUsersAndUpdatedAtOverMonths(localDate);
    }
}
