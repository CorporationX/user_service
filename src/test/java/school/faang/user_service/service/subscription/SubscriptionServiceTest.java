package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SubscribeEventDto;
import school.faang.user_service.dto.subscribe.UserDTO;
import school.faang.user_service.dto.subscribe.UserFilterDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.InvalidUserIdException;
import school.faang.user_service.exceptions.SubscriptionNotFoundException;
import school.faang.user_service.publisher.UnfollowEventPublisher;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UnfollowEventPublisher unfollowEventPublisher;

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void followUser_ShouldFollowUser_WhenIdsAreValid() {
        Long followerId = 1L;
        Long followeeId = 2L;

        InvalidUserIdException exception = assertThrows(InvalidUserIdException.class, () -> {
            subscriptionService.followUser(followerId, null);
        });

        assertEquals("Некорректные ID: ID не должны быть null и не должны совпадать.", exception.getMessage());
    }

    @Test

    @DisplayName("Успешная отписка: Пользователь отписывается от другого пользователя")
    void unfollowUser_ShouldUnfollowUser_WhenSubscriptionExists() {
        Long followerId = 1L;
        Long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
        verify(unfollowEventPublisher).publish(any(SubscribeEventDto.class));
    }

    @DisplayName("Успешная подписка: Пользователь подписывается на другого пользователя")
    void followUser_ShouldFollowUser_WhenSubscriptionDoesNotExist() {
        Long followerId = 1L;
        Long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository).followUser(followerId, followeeId);
        verify(followerEventPublisher).publish(any(SubscribeEventDto.class));
        verifyNoMoreInteractions(followerEventPublisher);
    }

    @Test
    void unfollowUser_ShouldThrowException_WhenSubscriptionDoesNotExist() {
        Long followerId = 1L;
        Long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        Exception exception = assertThrows(SubscriptionNotFoundException.class, () -> {
            subscriptionService.unfollowUser(followerId, followeeId);
        });

        assertEquals("Подписка не существует.", exception.getMessage());
    }


    @Test
    void getFollowers_ShouldReturnFilteredFollowers_WhenFilterIsValid() {
        Long userId = 1L;
        UserFilterDTO filter = new UserFilterDTO();

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testUser1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testUser2");

        when(subscriptionRepository.findByFolloweeId(userId)).thenReturn(Arrays.asList(user1, user2).stream());

        List<UserDTO> result = subscriptionService.getFollowers(userId, filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testUser1", result.get(0).getUsername());
        assertEquals("testUser2", result.get(1).getUsername());
    }

    @Test
    void getFollowers_ShouldThrowException_WhenFilterIsNull() {
        Long userId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.getFollowers(userId, null);
        });

        assertEquals("Фильтр не может быть null.", exception.getMessage());
    }

    @Test
    void countFollowers_ShouldReturnCorrectCount() {
        Long userId = 1L;
        int expectedCount = 5;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(userId)).thenReturn(expectedCount);

        long actualCount = subscriptionService.countFollowers(userId);

        assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowing_ShouldReturnFilteredFollowing_WhenFilterIsValid() {
        Long followeeId = 1L;
        UserFilterDTO filter = new UserFilterDTO();

        User user1 = new User();
        user1.setId(2L);
        user1.setUsername("testUser1");

        User user2 = new User();
        user2.setId(3L);
        user2.setUsername("testUser2");

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Arrays.asList(user1, user2).stream());

        List<UserDTO> result = subscriptionService.getFollowing(followeeId, filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testUser1", result.get(0).getUsername());
        assertEquals("testUser2", result.get(1).getUsername());
    }

    @Test
    void filterUsers_ShouldReturnFilteredUsers_WhenFilterIsApplied() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("Alice");
        user1.setExperience(5);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Bob");
        user2.setExperience(3);

        UserFilterDTO filter = new UserFilterDTO();
        filter.setExperienceMin(4);

        List<UserDTO> result = subscriptionService.filterUsers(Arrays.asList(user1, user2), filter);

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getUsername());
    }

    @Test
    void followUser_ShouldThrowException_WhenFollowerIdIsNull() {
        Long followeeId = 2L;

        Exception exception = assertThrows(InvalidUserIdException.class, () -> {
            subscriptionService.followUser(null, followeeId);
        });
        assertEquals("Некорректные ID: ID не должны быть null и не должны совпадать.", exception.getMessage());
    }

    @Test
    void followUser_ShouldThrowException_WhenFollowerIdEqualsFolloweeId() {
        Long userId = 1L;

        Exception exception = assertThrows(InvalidUserIdException.class, () -> {
            subscriptionService.followUser(userId, userId);
        });
        assertEquals("Некорректные ID: ID не должны быть null и не должны совпадать.", exception.getMessage());
    }
}
