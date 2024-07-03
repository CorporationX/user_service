package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        boolean isAlreadyFollow = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isAlreadyFollow) {
            throw new DataValidationException("Following already exists");
        }
        // нужно ли проверять существуют ли вообще такие пользователи? типо userRepository.existsById

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        boolean isAlreadyFollow = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!isAlreadyFollow) {
            throw new DataValidationException("Following does not exist");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followersStream = subscriptionRepository.findByFolloweeId(followeeId);
        List<User> filteredFollowers = filterUsers(followersStream, filter);

        return userMapper.toDtoList(filteredFollowers);
    }

    private List<User> filterUsers(Stream<User> userStream, UserFilterDto filter) {
        // мне такой вариант реализации фильтров не нравится, но так же и не нравится как Влад
        // предлагает реализовать фильтр, есть другие варианты?
        return userStream
                .filter(user -> filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern()))
                .filter(user -> filter.getAboutPattern() == null || user.getAboutMe().contains(filter.getAboutPattern()))
                .filter(user -> filter.getEmailPattern() == null || user.getEmail().contains(filter.getEmailPattern()))
                .filter(user -> filter.getContactPattern() == null
                        || user.getContacts().stream()
                        .anyMatch(contact -> contact.getContact().contains(filter.getContactPattern())))
                .filter(user -> filter.getCountryPattern() == null || user.getCountry().getTitle().contains(filter.getCountryPattern()))
                .filter(user -> filter.getCityPattern() == null || user.getCity().contains(filter.getCityPattern()))
                .filter(user -> filter.getPhonePattern() == null || user.getPhone().contains(filter.getPhonePattern()))
                .filter(user -> filter.getSkillPattern() == null
                        || user.getSkills().stream()
                        .anyMatch(skill -> skill.getTitle().contains(filter.getSkillPattern())))
                .filter(user -> filter.getExperienceMin() == null || user.getExperience() >= filter.getExperienceMin())
                .filter(user -> filter.getExperienceMax() == null || user.getExperience() <= filter.getExperienceMax())
                // полагаю что номер страницы приходит в пользовательком формате (начиная с 1)
                // default - 1
                .skip( (long) (filter.getPage() - 1) * filter.getPageSize())
                // default - 10
                .limit(filter.getPageSize())
                .toList();
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public long getFollowessCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
