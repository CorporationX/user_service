package school.faang.user_service.service.SubscriptionTest;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository repository;
    @InjectMocks
    private SubscriptionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void FollowsSuccessfullyTest() {
        long followerId = 1;
        long followeeId = 2;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        service.followUser(followerId, followeeId);
        verify(repository).followUser(followerId, followeeId);
    }

    @Test
    void alreadyFollowExceptionTest() {
        long followerId = 1;
        long followeeId = 2;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        Assert.assertThrows(DataValidationException.class,
                () -> service.followUser(followerId, followeeId));
    }

    @Test
    void unfollowTest() {
        long followerId = 1;
        long followeeId = 2;

        repository.unfollowUser(followerId,followeeId);
        verify(repository).unfollowUser(followerId, followeeId);
    }
}
