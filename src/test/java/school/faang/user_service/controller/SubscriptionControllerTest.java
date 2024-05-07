package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.error.DataValidationException;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.dto.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
public class SubscriptionControllerTest {
    @Autowired
    private SubscriptionController controller;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    @DisplayName("Проверка, что пользователь не может подписаться сам на себя")
    public void followUserSameIdTest() {
//        try {
//            controller.followUser(42, 42);
//        } catch (DataValidationException dve) {
//            System.out.println("DataValidationException успешно обработана.");
//        }
        Exception exception = assertThrows(DataValidationException.class, () -> {
            controller.followUser(42, 42);
        });
        assertEquals("Нельзя подписаться на самого себя", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка, что подписка на пользователя работает, в controller")
    public void followUserSuccessControllerTest() {
        subscriptionController.followUser(10, 20);
        verify(subscriptionService, Mockito.times(1)).followUser(10, 20);
    }

    @Test
    @DisplayName("Проверка, что пользователь не может отписаться от самого себя")
    public void unfollowUserSamIdTest() {
        assertThrows(DataValidationException.class, () -> {
            subscriptionController.unfollowUser(42, 42);
        });
    }

    @Test
    @DisplayName("Проверка, что отписка от пользователя работает, в controller")
    public void unfollowUserSuccessControllerTest() {
        subscriptionController.unfollowUser(10, 20);
        verify(subscriptionService).unfollowUser(10, 20);
    }

    @Test
    @DisplayName("Проверка, что выводится список подписчиков")
    public void testGetFollowers() {

        SubscriptionService service = mock(SubscriptionService.class);

        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();

        List<UserDto> fakeFollowers = new ArrayList<>();
        fakeFollowers.add(new UserDto(1L, "Ivan", "ivan@test.com"));

        when(service.getFollowers(followeeId, filter)).thenReturn(fakeFollowers);

        List<UserDto> result = service.getFollowers(followeeId, filter);

        verify(service).getFollowers(followeeId, filter);

        assertEquals(fakeFollowers, result);
    }

    @Test
    @DisplayName("Проверка, что с список подписчиков не выводится, если ввести неправильный followeeId")
    public void testGetFollowersWithInvalidInput() {

        SubscriptionService service = mock(SubscriptionService.class);


        long followeeId = -1L;
        UserFilterDto filter = new UserFilterDto();


        when(service.getFollowers(followeeId, filter)).thenThrow(new IllegalArgumentException("Неверный followeeId"));

        assertThrows(IllegalArgumentException.class, () -> {
            service.getFollowers(followeeId, filter);
        });

        verify(service).getFollowers(followeeId, filter);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в controller")
    public void testGetFollowersCountPositive() {
        SubscriptionService service = mock(SubscriptionService.class);
        // Устанавливаем поведение заглушки для позитивного теста
        when(service.getFollowersCount(1L)).thenReturn(42);

        // Вызываем метод и проверяем результат
        int followersCount = controller.getFollowersCount(1L);
        assertEquals(42, followersCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в controller, если передать  несуществующее ID")
    public void testGetFollowersCountNegative() {
        SubscriptionService service = mock(SubscriptionService.class);
        when(service.getFollowersCount(2L)).thenThrow(new RuntimeException("User not found"));

        try {
            controller.getFollowersCount(2L);
        } catch (RuntimeException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в controller")
    public void testGetFollowingCountPositive() {
        SubscriptionService service = mock(SubscriptionService.class);
        // Устанавливаем поведение заглушки для позитивного теста
        when(service.getFollowingCount(1L)).thenReturn(42);

        // Вызываем метод и проверяем результат
        int followingCount = controller.getFollowingCount(1L);
        assertEquals(42, followingCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в controller, если передать  несуществующее ID")
    public void testGetFollowingCountNegative() {
        SubscriptionService service = mock(SubscriptionService.class);
        when(service.getFollowingCount(2L)).thenThrow(new RuntimeException("User not found"));

        try {
            controller.getFollowingCount(2L);
        } catch (RuntimeException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

}
