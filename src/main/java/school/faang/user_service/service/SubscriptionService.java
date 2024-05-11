package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.error.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Вы не подписаны на пользователя.");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionRepository.findByFolloweeId(followeeId)
                .filter(user -> isUserMatchFiltration(user, filter))
                .skip((long) (filter.getPage() - 1) * filter.getPageSize())
                .limit(filter.getPageSize())
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }


    public boolean isUserMatchFiltration(User user, UserFilterDto filter) {
        if (filter.getUsernamePattern() != null && !user.getUsername().matches(filter.getUsernamePattern())) {
            return false;
        }
        if (filter.getAboutPattern() != null && !user.getAboutMe().matches(filter.getAboutPattern())) {
            return false;
        }
        if (filter.getEmailPattern() != null && !user.getEmail().matches(filter.getEmailPattern())) {
            return false;
        }
        if (filter.getCityPattern() != null && !user.getCity().matches(filter.getCityPattern())) {
            return false;
        }
        if (filter.getPhonePattern() != null && !user.getPhone().matches(filter.getPhonePattern())) {
            return false;
        }
        if (filter.getExperienceMin() != null && user.getExperience() < filter.getExperienceMin()) {
            return false;
        }
        if (filter.getExperienceMax() != null && user.getExperience() > filter.getExperienceMax()) {
            return false;
        }
        return true;
    }


    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return subscriptionRepository.findByFollowerId(followerId)
                .filter(user -> isUserMatchFiltration(user, filter))
                .skip((long) (filter.getPage() - 1) * filter.getPageSize())
                .limit(filter.getPageSize())
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }
}