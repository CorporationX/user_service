package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.subscription.UserSubscriptionService;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    public void followUser(long followeeId) {
        ensureUserIdValid(followeeId);
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followeeId) {
        ensureUserIdValid(followeeId);
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    public CountResponse getFollowersCount(long followeeId) {
        ensureUserIdValid(followeeId);
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    public CountResponse getFolloweesCount(long followerId) {
        ensureUserIdValid(followerId);
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFiltersDto userFiltersDto) {
        ensureUserIdValid(followeeId);
        ensureUserFiltersDtoValid(userFiltersDto);
        return userSubscriptionService.getFollowers(followeeId, userFiltersDto);
    }

    public List<UserDto> getFollowees(long followerId, UserFiltersDto userFiltersDto) {
        ensureUserIdValid(followerId);
        ensureUserFiltersDtoValid(userFiltersDto);
        return userSubscriptionService.getFollowees(followerId, userFiltersDto);
    }

    private void ensureUserIdValid(long userId) {
        if (userId <= 0) {
            throw new DataValidationException("user id cannot be equal to or less than zero");
        }
    }

    private void ensureFollowerAndFolloweeNotTheSameUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException("user cannot follow or unfollow himself");
        }
    }

    private void ensureUserFiltersDtoValid(UserFiltersDto userFiltersDto) {
        Objects.requireNonNull(userFiltersDto, "user filters cannot be null");
        String namePattern = userFiltersDto.namePattern();
        String phoneNumber = userFiltersDto.phoneNumber();
        int experienceMin = userFiltersDto.experienceMin();
        int experienceMax = userFiltersDto.experienceMax();

        if (namePattern == null || namePattern.isBlank()) {
            throw new DataValidationException("user filters must include full name pattern");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new DataValidationException("user filters must include full phone number");
        }
        if (experienceMin < 0 || experienceMax < 0) {
            throw new DataValidationException("experience min or experience max cannot be less than zero");
        }
        if (experienceMin > experienceMax) {
            throw new DataValidationException("experience min cannot be greater than experience max");
        }
    }
}
