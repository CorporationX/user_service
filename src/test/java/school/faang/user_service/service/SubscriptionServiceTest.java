package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.TestUser.FOLLOWER_ID;
import static school.faang.user_service.util.TestUser.USER_LIST;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionService subscriptionService;

    @Test
    public void testGetFollowers() {
        subscriptionRepository.findByFolloweeId(FOLLOWER_ID);
        verify(subscriptionRepository, times(1)).findByFolloweeId(FOLLOWER_ID);

        when(subscriptionRepository.findByFolloweeId(FOLLOWER_ID)).thenReturn(USER_LIST.stream());

        Stream<User> users =  subscriptionRepository.findByFolloweeId(FOLLOWER_ID);

        // Осталось позвать стрим по фильтру и вернуть отфильтрованных пользакв

    }
}