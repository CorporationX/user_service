package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    private final long followerId = 1L;
    private final long followeeId = 2L;

    @Mock
    SubscriptionRepository repository;

    @InjectMocks
    SubscriptionService service;

    @Test
    public void testFollowUserToAnotherUser() {
        repository.followUser(followerId, followeeId);
        verify(repository).followUser(followerId, followeeId);
    }

    @Test
    public void testIsFollowerUserAndFolloweeUserExist() {
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> service.followUser(followerId, followeeId));

        assertEquals("User " + followerId + " subscription to user " +
                followeeId + " exist", dataValidationException.getMessage());
    }
}