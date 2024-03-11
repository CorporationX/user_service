package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private final long followerId = 2;
    private final long followeeId = 1;

    @Test
    public void shouldAddNewFollowerById() {
        Mockito.when(userRepository.existsById(followerId)).thenReturn(true);
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfUserSubscribed() {
        Mockito.when(userRepository.existsById(followerId)).thenReturn(true);
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfFollowerDoesNotExist() {
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfFolloweeDoesNotExist() {
        Mockito.lenient().when(userRepository.existsById(followerId)).thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldDeleteFollowerById() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> subscriptionService.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfSubscriptionDoesNotExist() {
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).unfollowUser(followerId, followeeId);
    }
}
