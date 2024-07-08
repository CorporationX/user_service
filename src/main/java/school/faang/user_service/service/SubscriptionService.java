package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.zip.DataFormatException;

@Component
@RequiredArgsConstructor
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) throws DataFormatException {
        validateUsersSubs(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataFormatException {
        validateUsersSubs(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public void validateUsersSubs(long followerId, long followeeId) throws DataFormatException {
        if (followerId == followeeId) {
            throw new DataFormatException("Same id users");
        }

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataFormatException("There are no users with this id");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) throws DataFormatException {
        validateUser(followeeId);
        List<User> followees = subscriptionRepository.findByFolloweeId(followeeId).toList();

        return filterUsers(filter, followees);

    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) throws DataFormatException {
        validateUser(followerId);
        List<User> followers = subscriptionRepository.findByFollowerId(followerId).toList();

        return filterUsers(filter, followers);
    }

    public int getFollowersCount(long followeeId) throws DataFormatException {
        validateUser(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) throws DataFormatException {
        validateUser(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    public void validateUser(long followeeId) throws DataFormatException {
        if (!subscriptionRepository.existsById(followeeId)) {
            throw new DataFormatException("there are no user with this id");
        }
    }

    public List<UserDto> filterUsers(UserFilterDto filter, List<User> users) throws DataFormatException {
        if (users == null) {
            throw new NullPointerException("empty followers");
        }
        return users.stream()
                .filter(u -> filterUserField(u, filter))
                .map(UserDto::usetToUserDto)
                .toList();
    }

    public boolean filterUserField(User user, UserFilterDto filter) {
        if (filter.getNamePattern() != null && !filter.getNamePattern().equals(user.getUsername())) return false;
        if (filter.getAboutPattern() != null && !filter.getAboutPattern().equals(user.getAboutMe())) return false;
        if (filter.getEmailPattern() != null && !filter.getEmailPattern().equals(user.getEmail())) return false;
        if (filter.getContactPattern() != null && !filter.getContactPattern().equals(user.getContacts())) return false;
        if (filter.getCountryPattern() != null && !filter.getCountryPattern().equals(user.getCountry())) return false;
        if (filter.getCityPattern() != null && !filter.getCityPattern().equals(user.getCity())) return false;
        if (filter.getPhonePattern() != null && !filter.getPhonePattern().equals(user.getPhone())) return false;
        if (filter.getSkillPattern() != null && !filter.getSkillPattern().equals(user.getSkills())) return false;
        if (filter.getExperienceMin() != 0 && filter.getExperienceMin() > user.getExperience()) return false;
        if (filter.getExperienceMax() != 0 && filter.getExperienceMin() < user.getExperience()) return false;
        else return true;
    }
}