package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceValidatorTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceValidator subscriptionServiceValidator;

    private long followerId;
    private long followeeId;

    @BeforeEach
    public void setUp() {
        followerId = 1L;
        followeeId = 2L;
    }

    @Test
    @DisplayName("Test validateFollowUnfollowUser: User not found")
    public void testValidateFollowUnfollowUser_UserNotFound() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            subscriptionServiceValidator.validateFollowUnfollowUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test validateFollowUnfollowUser: User following himself")
    public void testValidateFollowUnfollowUser_UserFollowingHimself() {
        long sameId = 1L;
        when(subscriptionRepository.existsById(sameId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            subscriptionServiceValidator.validateFollowUnfollowUser(sameId, sameId);
        });
    }

    @Test
    @DisplayName("Test validateFollowUnfollowUser: User already followed")
    public void testValidateFollowUnfollowUser_UserAlreadyFollowed() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);
        when(subscriptionRepository.existsById(followeeId)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            subscriptionServiceValidator.validateFollowUnfollowUser(followerId, followeeId);
        });
    }

    @Test
    @DisplayName("Test validateGetFollowers: User not found")
    public void testValidateGetFollowers_UserNotFound() {
        when(subscriptionRepository.existsById(followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            subscriptionServiceValidator.validateGetFollowers(followeeId, new UserFilterDto());
        });
    }

    @Test
    @DisplayName("Test validateGetFollowers: Filter DTO is null")
    public void testValidateGetFollowers_FilterDtoIsNull() {
        when(subscriptionRepository.existsById(followeeId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            subscriptionServiceValidator.validateGetFollowers(followeeId, null);
        });
    }

    @Test
    @DisplayName("Test validateGetFollowing: User not found")
    public void testValidateGetFollowing_UserNotFound() {
        when(subscriptionRepository.existsById(followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            subscriptionServiceValidator.validateGetFollowing(followeeId, new UserFilterDto());
        });
    }

    @Test
    @DisplayName("Test validateGetFollowing: Filter DTO is null")
    public void testValidateGetFollowing_FilterDtoIsNull() {
        when(subscriptionRepository.existsById(followeeId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            subscriptionServiceValidator.validateGetFollowing(followeeId, null);
        });
    }

    @Test
    @DisplayName("Test validateExistsById: User not found")
    public void testValidateExistsById_UserNotFound() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            subscriptionServiceValidator.validateExistsById(followerId);
        });
    }
}
