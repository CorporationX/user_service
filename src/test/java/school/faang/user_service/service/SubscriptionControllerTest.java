package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
