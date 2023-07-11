package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId){
        validateFollower(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId){
        validateFollower(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter){
        validateUserId(followeeId);
        return subscriptionRepository.findByFolloweeId(followeeId)
                .filter(user -> filterUser(user, filter))
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }

    public Boolean filterUser(User user, UserFilterDto filter){
        return user.getUsername().matches(filter.getNamePattern()) &&
                user.getEmail().matches(filter.getEmailPattern()) &&
                user.getPhone().matches(filter.getPhonePattern()) &&
                user.getAboutMe().matches(filter.getAboutPattern()) &&
                user.getCountry().getTitle().matches(filter.getCountryPattern()) &&
                user.getCity().matches(filter.getCityPattern()) &&
                user.getExperience() > filter.getExperienceMin() &&
                user.getExperience() < filter.getExperienceMax() &&
                user.getContacts().stream().allMatch(contact -> contact.getContact().matches(filter.getContactPattern())) &&
                user.getSkills().stream().allMatch(skill -> skill.getTitle().matches(filter.getSkillPattern()));
    }

    private void validateFollower(long followerId, long followeeId){
        validateUserId(followerId);
        validateUserId(followeeId);
        if(followerId == followeeId){
            throw new DataValidationException("Пользователь пытается подписаться сам на себя");
        }
    }

    private void validateUserId(long userId){
        if(userId <= 0){
            throw new IllegalArgumentException("Пользователя с отрицательным Id не может быть");
        }
    }
}
