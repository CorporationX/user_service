package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> userStreamFromRepository = subscriptionRepository.findByFolloweeId(followeeId);
        List<User> userListFromRepository = filterUsers(userStreamFromRepository, filter);
        return subscriptionMapper.toListUserDto(userListFromRepository);
    }

    public List<User> filterUsers(Stream<User> userStream, UserFilterDto filter) {
        return userStream.filter(user -> {
                    if (filter.getNamePattern().isBlank()) {
                        return true;
                    }
                    return user.getUsername().equals(filter.getNamePattern());
                })
                .filter(user -> {
                    if (filter.getAboutPattern().isBlank()) {
                        return true;
                    }
                    return user.getAboutMe().equals(filter.getAboutPattern());
                })
                .filter(user -> {
                    if (filter.getEmailPattern().isBlank()) {
                        return true;
                    }
                    return user.getEmail().equals(filter.getEmailPattern());
                })
                .filter(user -> {
                    if (filter.getContactPattern().isBlank()) {
                        return true;
                    }
                    return user.getContacts().stream()
                            .anyMatch(contact -> contact.getContact().equals(filter.getContactPattern()));
                })
                .filter(user -> {
                    if (filter.getCountryPattern().isBlank()) {
                        return true;
                    }
                    return user.getCountry().getTitle().equals(filter.getCountryPattern());
                })
                .filter(user -> {
                    if (filter.getCityPattern().isBlank()) {
                        return true;
                    }
                    return user.getCity().equals(filter.getCityPattern());
                })
                .filter(user -> {
                    if (filter.getPhonePattern().isBlank()) {
                        return true;
                    }
                    return user.getPhone().equals(filter.getPhonePattern());
                })
                .filter(user -> {
                    if (filter.getSkillPattern().isBlank()) {
                        return true;
                    }
                    return user.getSkills().stream()
                            .anyMatch(skill -> skill.getTitle().equals(filter.getSkillPattern()));
                })
                .filter(user -> {
                    if (Integer.toString(filter.getExperienceMin()).isBlank()) {
                        return true;
                    }
                    return user.getExperience() > filter.getExperienceMin();
                })
                .filter(user -> {
                    if (Integer.toString(filter.getExperienceMax()).isBlank()) {
                        return true;
                    }
                    return user.getExperience() < filter.getExperienceMax();
                })
                .collect(Collectors.toList());

    }
}
