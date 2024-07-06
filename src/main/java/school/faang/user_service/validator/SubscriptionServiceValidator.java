package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionServiceValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void validFollowUser(long followerId, long followeeId) {
        validExistsById(followerId);
        validExistsById(followeeId);
        validExistsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public void validUnfollowUser(long followerId, long followeeId) {
        validExistsById(followerId);
        validExistsById(followeeId);
        validExistsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public void validGetFollowers(long followeeId, UserFilterDto filterDto) {
        validExistsById(followeeId);
        validUserFilterDtoByNull(filterDto);
    }

    public void validGetFollowersCount(long followeeId) {
        validExistsById(followeeId);
    }
    public void validGetFollowing(long followeeId, UserFilterDto filterDto) {
        validExistsById(followeeId);
        validUserFilterDtoByNull(filterDto);
    }

    public void validGetFollowingCount(long followerId) {
        validExistsById(followerId);
    }
    private void validUserFilterDtoByNull(UserFilterDto filterDto) {
        if (filterDto == null) {
            throw new IllegalArgumentException("UserFilterDto cannot be null");
        }
    }
    private void validExistsByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User id: " + followerId
                    + " already followed to the user id: " + followeeId);
        }
    }

    private void validExistsById(long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new DataValidationException("User " + id + " not found");
        }
    }
}
