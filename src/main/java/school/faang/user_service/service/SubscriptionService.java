package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) throws DataValidationException {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already following this account");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        return filterUsers(followers, filter).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<User> filterUsers(List<User> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern()))
                .filter(user -> filter.getAboutPattern() == null || user.getAboutMe().contains(filter.getAboutPattern()))
                .filter(user -> filter.getEmailPattern() == null || user.getEmail().contains(filter.getEmailPattern()))
                .filter(user -> filter.getContactPattern() == null || (user.getPhone() != null && user.getPhone().contains(filter.getContactPattern())))
                .filter(user -> filter.getCountryPattern() == null || user.getCountry().getTitle().contains(filter.getCountryPattern()))
                .filter(user -> filter.getCityPattern() == null || (user.getCity() != null && user.getCity().contains(filter.getCityPattern())))
                .filter(user -> filter.getPhonePattern() == null || (user.getPhone() != null && user.getPhone().contains(filter.getPhonePattern())))
                .filter(user -> filter.getSkillPattern() == null || user.getSkills().stream().anyMatch(skill -> skill.getTitle().contains(filter.getSkillPattern())))
                .filter(user -> user.getExperience() == null || user.getExperience() >= filter.getExperienceMin())
                .filter(user -> user.getExperience() == null || user.getExperience() <= filter.getExperienceMax())
                .skip((long) filter.getPage() * filter.getPageSize())
                .limit(filter.getPageSize())
                .toList();
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        List<User> followings = subscriptionRepository.findByFollowerId(followerId).toList();
        return filterUsers(followings, filter).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
