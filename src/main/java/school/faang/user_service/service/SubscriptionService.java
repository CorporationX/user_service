package school.faang.user_service.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @SneakyThrows
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Validation exception");
        } else {
            subscriptionRepository.followUser(followerId, followeeId);
        }
    }

    public void unFollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    /*public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return filterUsers(subscriptionRepository.findByFolloweeId(followeeId), filter)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    private Stream<User> filterUsers(Stream<User> users, UserFilterDto filter) {
        return users
                .filter(user -> (filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern())))
                .filter(user -> (filter.getEmailPattern() == null || user.getEmail().contains(filter.getEmailPattern())))
                .filter(user -> (filter.getAboutPattern() == null || user.getAboutMe() != null && user.getAboutMe().contains(filter.getAboutPattern())))
                .filter(user -> (filter.getContactPattern() == null || user.getContacts() != null && String.valueOf(user.getContacts()).contains(filter.getContactPattern())))
                .filter(user -> (filter.getCountryPattern() == null || user.getCountry() != null && String.valueOf(user.getCountry()).equals(filter.getCountryPattern())))
                .filter(user -> (filter.getCityPattern() == null || user.getCity() != null && user.getCity().contains(filter.getCityPattern())))
                .filter(user -> (filter.getPhonePattern() == null || user.getPhone() != null && user.getPhone().contains(filter.getPhonePattern())))
                .filter(user -> (filter.getSkillPattern() == null || user.getSkills() != null && String.valueOf(user.getSkills()).contains(filter.getSkillPattern())))
                .filter(user -> (filter.getExperienceMin() <= 0 || user.getExperience() >= filter.getExperienceMin()))
                .filter(user -> (filter.getExperienceMax() <= 0 || user.getExperience() <= filter.getExperienceMax()));
    }*/

    public void getFollowersCount(long followeeId){
        subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return subscriptionRepository.findByFollowerId(followerId)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public void getFollowingCount(long followerId) {
        subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
