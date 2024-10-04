package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserBanService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserBanServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserBanService userBanService;

    @Test
    public void testUpdateUsers_ShouldSetBannedTrue() {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        User user1 = new User();
        user1.setId(1L);
        user1.setBanned(false);

        User user2 = new User();
        user2.setId(2L);
        user2.setBanned(false);

        User user3 = new User();
        user3.setId(3L);
        user3.setBanned(false);

        List<User> users = Arrays.asList(user1, user2, user3);

        when(userRepository.findAllById(userIds)).thenReturn(users);

        userBanService.updateUsers(userIds);

        for (User user : users) {
            assert user.isBanned();
        }
        verify(userRepository, times(1)).saveAll(users); // Проверяем, что вызов saveAll был сделан один раз
    }

    @Test
    public void testUpdateUsers_WhenNoUsersFound() {
        List<Long> userIds = Arrays.asList(4L, 5L, 6L);

        when(userRepository.findAllById(userIds)).thenReturn(List.of());

        userBanService.updateUsers(userIds);

        verify(userRepository, times(1)).findAllById(userIds); // Проверяем вызов findAllById
        verify(userRepository, never()).saveAll(anyList()); // Проверяем, что saveAll не вызывается, так как пользователей нет
    }
}
