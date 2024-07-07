package school.faang.user_service.service.controller_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;

    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        subscriptionController = new SubscriptionController(subscriptionService);
    }

    //Positive Tests

    @Test
    @DisplayName("controller good follow")
    public void followTest_goodID() throws DataFormatException {
        //arrange
        long followerId = 0L;
        long followeeId = 1L;

        subscriptionController.followUser(followerId, followeeId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("controller good unfollow")
    public void unfollowTest_goodID() throws DataFormatException {
        //arrange
        long followerId = 0L;
        long followeeId = 1L;

        subscriptionController.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    //Negative Tests
    @Test
    @DisplayName("Invalid Id follow")
    public void followUser_sameId() {
        //arrange
        long followerId = 0L;
        long followeeId = 0L;

        DataFormatException ex = assertThrows(DataFormatException.class, () -> subscriptionController.followUser(followerId, followeeId));
        assertEquals("Follow: Invalid id users", ex.getMessage());
    }

    @Test
    @DisplayName("Invalid Id unfollow")
    public void unfollowUser_sameId() {
        //arrange
        long followerId = 0L;
        long followeeId = 0L;

        DataFormatException ex = assertThrows(DataFormatException.class, () -> subscriptionController.unfollowUser(followerId, followeeId));
        assertEquals("Unfollow: Invalid id users", ex.getMessage());
    }

}
