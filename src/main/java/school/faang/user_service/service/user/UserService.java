package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.jira.JiraAccount;
import school.faang.user_service.mapper.jira.JiraAccountMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.jira.JiraAccountRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserValidator userValidator;
    private final JiraAccountMapper jiraAccountMapper;
    private final JiraAccountRepository jiraAccountRepository;

    @Value("${services.dicebear.avatar}")
    private String avatar;

    @Value("${services.dicebear.small_avatar}")
    private String smallAvatar;

    public UserDto create(UserDto userDto) {
        userValidator.validatePassword(userDto.getPassword());
        User user = userMapper.toEntity(userDto);
        user.setActive(true);
        setUpAvatar(user);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getUser(long userId) {
        User user = getUserFromRepository(userId);
        return userMapper.toDto(user);
    }

    public JiraAccountDto getJiraAccountInfo(long userId) {
        User user = getUserFromRepository(userId);
        return jiraAccountMapper.toDto(user.getJiraAccount());
    }

    public User getUserById(Long userId) {
        return getUserFromRepository(userId);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    public List<UserDto> getFollowers(long userId) {
        User user = getUserFromRepository(userId);
        return userMapper.toDto(user.getFollowers());
    }

    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        if (!userFilters.isEmpty()) {
            userFilters.stream()
                    .filter(filter -> filter.isApplicable(filters))
                    .forEach(filter -> filter.apply(premiumUsers, filters));
        }
        return userMapper.toDto(premiumUsers);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = getUserFromRepository(userId);
        user.setBanned(true);
        log.info("User {} banned", userId);
    }

    @Transactional
    public UserDto saveJiraAccountInfo(long userId, JiraAccountDto jiraAccountDto) {
        User user = getUserFromRepository(userId);
        jiraAccountDto.setUserId(userId);
        JiraAccount jiraAccount = jiraAccountRepository.save(jiraAccountMapper.toEntity(jiraAccountDto));
        user.setJiraAccount(jiraAccount);
        return userMapper.toDto(user);
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
    }

    private void setUpAvatar(User user) {
        UUID uuid = UUID.randomUUID();
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(avatar + uuid)
                .smallFileId(smallAvatar + uuid)
                .build());
    }
}