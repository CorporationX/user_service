package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void getFollowersThrowIllegalException() {
        int idUser = -10;
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowers(idUser, new UserFilterDto()));
    }

    @Test
    public void testGetFollowersInvokesFindByFolloweeId() {
        long followeeId = 1L;
        UserFilterDto filterDto = new UserFilterDto();
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.empty());

        subscriptionService.getFollowers(followeeId, filterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    void getFollowingThrowIllegalException() {
        int idUser = -10;
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowing(idUser, new UserFilterDto()));
    }

    @Test
    public void testGetFollowingInvokesFindByFolloweeId() {
        long followeeId = 1L;
        UserFilterDto filterDto = new UserFilterDto();
        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(Stream.empty());

        subscriptionService.getFollowing(followeeId, filterDto);

        verify(subscriptionRepository, times(1)).findByFollowerId(followeeId);
    }


}


