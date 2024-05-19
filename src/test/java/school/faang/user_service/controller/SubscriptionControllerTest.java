package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.error.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    @DisplayName("Проверка, что пользователь не может подписаться сам на себя")
    void followUserSameIdTest() {
        Exception exception = assertThrows(DataValidationException.class, () -> {
            subscriptionController.followUser(42, 42);
        });
        assertEquals("Нельзя подписаться на самого себя", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка, что подписка на пользователя работает, в controller")
    void followUserSuccessControllerTest() {
        subscriptionController.followUser(10, 20);
        verify(subscriptionService).followUser(10, 20);
    }

    @Test
    @DisplayName("Проверка, что пользователь не может отписаться от самого себя")
    void unfollowUserSamIdTest() {
        assertThrows(DataValidationException.class, () -> {
            subscriptionController.unfollowUser(42, 42);
        });
    }

    @Test
    @DisplayName("Проверка, что отписка от пользователя работает, в controller")
    void unfollowUserSuccessControllerTest() {
        subscriptionController.unfollowUser(10, 20);
        verify(subscriptionService).unfollowUser(10, 20);
    }

    @Test
    @DisplayName("Проверка, что выводится список подписчиков")
    void testGetFollowers() {

        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();

        List<UserDto> fakeFollowers = new ArrayList<>();
        fakeFollowers.add(new UserDto(1L, "Ivan", "ivan@test.com"));

        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(fakeFollowers);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        verify(subscriptionService).getFollowers(followeeId, filter);

        assertEquals(fakeFollowers, result);
    }

    @Test
    @DisplayName("Проверка, что с список подписчиков не выводится, если ввести неправильный followeeId")
    void testGetFollowersWithInvalidInput() {

        long followeeId = -1L;
        UserFilterDto filter = new UserFilterDto();


        when(subscriptionService.getFollowers(followeeId, filter)).thenThrow(new IllegalArgumentException("Неверный followeeId"));

        assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.getFollowers(followeeId, filter);
        });

        verify(subscriptionService).getFollowers(followeeId, filter);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в controller")
    void testGetFollowersCountPositive() {
        when(subscriptionService.getFollowersCount(1L)).thenReturn(42);

        int followersCount = subscriptionController.getFollowersCount(1L);
        assertEquals(42, followersCount);
    }


    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в controller")
    void testGetFollowingCountPositive() {
        when(subscriptionService.getFollowingCount(1L)).thenReturn(42);

        int followingCount = subscriptionController.getFollowingCount(1L);
        assertEquals(42, followingCount);
    }
}