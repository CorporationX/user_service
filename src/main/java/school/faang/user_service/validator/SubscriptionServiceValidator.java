package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionServiceValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void validateFollowUnfollowUser(long followerId, long followeeId) {
        validateExistsById(followerId);
        validateExistsById(followeeId);
        validateIdenticalUserIDs(followerId, followeeId);
        validateExistsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public void validateGetFollowers(long followeeId, UserFilterDto filterDto) {
        validateExistsById(followeeId);
        validateUserFilterDtoByNull(filterDto);
    }

    public void validateGetFollowing(long followeeId, UserFilterDto filterDto) {
        validateExistsById(followeeId);
        validateUserFilterDtoByNull(filterDto);
    }

    public void validateExistsById(long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new DataValidationException("User " + id + " not found");
        }
    }

    private void validateUserFilterDtoByNull(UserFilterDto filterDto) {
        if (filterDto == null) {
            throw new IllegalArgumentException("UserFilterDto cannot be null");
        }
    }

    private void validateExistsByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User id: " + followerId
                    + " already followed to the user id: " + followeeId);
        }
    }

    private void validateIdenticalUserIDs(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("User cannot follow to himself");
        }
    }
}
