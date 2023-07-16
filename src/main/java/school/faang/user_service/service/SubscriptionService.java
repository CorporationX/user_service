package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        validateUserId(followeeId);

        Stream<User> allUsers = subscriptionRepository.findByFollowerId(followeeId);
        Stream<User> usersOnPage = getPage(allUsers.collect(Collectors.toList()), filter.getPage(), filter.getPageSize()).stream();

        return usersOnPage
                .filter(user -> filterUser(user, filter))
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
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


    private void validateUserId(long userId){
        if(userId <= 0){
            throw new IllegalArgumentException("User id cannot be negative");
        }
    }

    public static <T> List<T> getPage(List<T> records, int page, int pageSize) {
        if (page < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page or pageSize");
        }
        int startIdx = page * pageSize;
        int endIdx = Math.min(startIdx + pageSize, records.size());
        return records.subList(startIdx, endIdx);
    }
}
