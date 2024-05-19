package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.error.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMatchByFilterChecker userMatchByFilterChecker;

    @InjectMocks
    private SubscriptionService subscriptionService;


    @Test
    @DisplayName("Проверка, что нельзя подписаться на пользователя, если уже на него подписан")
    void followUserAlreadySubscribeTest() {

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(10, 20)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(10, 20);
        });

        verify(subscriptionRepository, Mockito.never()).followUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Проверка, что подписка на пользователя работает, в service")
    void followUserSuccessServiceTest() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(10, 20)).thenReturn(false);

        subscriptionService.followUser(10, 20);

        verify(subscriptionRepository).followUser(10, 20);
    }

    @Test
    @DisplayName("Проверка, что фильтрация не найдет пользователей, если ввести несуществующее имя")
    void testIsUserMatchUsernamePatternFiltrationFalse() {

        User user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();
        UserFilterDto filter = new UserFilterDto();
        filter.setUsernamePattern("nonexistent");

        UserMatchByFilterChecker checker = new UserMatchByFilterChecker();
        assertFalse(checker.isUserMatchFiltration(user, filter));
    }

    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в service")
    void testGetFollowersCountPositive() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(1L)).thenReturn(42);

        int followersCount = subscriptionService.getFollowersCount(1L);
        assertEquals(42, followersCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в service, если передать  несуществующее ID")
    void testGetFollowersCountNegative() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(2L)).thenReturn(0); // Предположим, что возвращается 0 при отсутствии данных

        int followersCount = subscriptionService.getFollowersCount(2L);
        assertEquals(0, followersCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в service")
    void testGetFollowingCountPositive() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(1L)).thenReturn(42);

        int FollowingCount = subscriptionService.getFollowingCount(1L);
        assertEquals(42, FollowingCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в service, если передать  несуществующее ID")
    void testGetFollowingCountNegative() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(2L)).thenReturn(0); // Предположим, что возвращается 0 при отсутствии данных

        int FollowingCount = subscriptionService.getFollowingCount(2L);
        assertEquals(0, FollowingCount);
    }
}