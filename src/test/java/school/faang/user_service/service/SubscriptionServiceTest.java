package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.NoSuchElementException;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionService subscriptionService;

    @Test
    void testRepositoryFollowUser() {
        subscriptionService.followUser(22, 23);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testRepositoryUnfollowUser() {
        subscriptionService.unfollowUser(22, 23);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testAlreadyFollowed() {
        try {
            User user = subscriptionRepository.findById(22L)
                    .stream().findFirst().get();
            List<User> followers = user.getFollowers();
            boolean isExist = false;
            for (User folower : followers) {
                if (folower.getId() == 23) {
                    isExist = true;
                    break;
                }
            }
            Assertions.assertTrue(isExist);
        } catch (NoSuchElementException e) {
            System.out.println("User with this Id does not exist");
        }
    }

    @Test
    void testGetFollowers() {

        // Assertions.asser
    }
}
