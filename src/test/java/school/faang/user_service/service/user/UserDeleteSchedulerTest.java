package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.scheduler.UserDeleteScheduler;

import java.time.LocalDate;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserDeleteSchedulerTest {
    @InjectMocks
    private UserDeleteScheduler userDeleteScheduler;
    @Mock
    private UserRepository userRepository;

    @Test
    public void successDeleteNonActiveUsers() {
        long months = 3L;
        LocalDate localDate = LocalDate.now().minusMonths(months);
        userDeleteScheduler.deleteNonActiveUsers();
        Mockito.verify(userRepository, times(1)).deleteAllInactiveUsersAndUpdatedAtOverMonths(localDate);
    }
}
