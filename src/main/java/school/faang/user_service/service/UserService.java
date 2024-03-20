package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PremiumRepository premiumRepository;
    private final S3Service s3Service;
    private final RestTemplate restTemplate;
    private final EventService eventService;
    private GoalService goalService;
    private MentorshipService mentorshipService;

    @Autowired
    public void setGoalService(@Lazy GoalService goalService) {
        this.goalService = goalService;
    }

    @Autowired
    public void setMentorshipService(@Lazy MentorshipService mentorshipService) {
        this.mentorshipService = mentorshipService;
    }

    @Value("${services.random_avatar.url}")
    private String url;

    public void validateUserDoesNotHavePremium(long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("Пользователь уже имеет премиум подписку");
        }
    }

    public UserDto getUser(long id) {
        return userMapper.toDto(findById(id));
    }

    public boolean isUserExists(long id) {
        return userRepository.existsById(id);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User entity = userMapper.toEntity(userDto);
        entity.setActive(true);
        try {
            UserProfilePic userProfilePic = saveAvatar(entity.getUsername());
            entity.setUserProfilePic(userProfilePic);
        } catch (Exception e) {
            log.error("Ошибка генерации аватара", e);
        }
        return userMapper.toDto(userRepository.save(entity));
    }

    private UserProfilePic saveAvatar(String userName) {
        byte[] avatar = getAvatar(userName);
        if (avatar != null) {
            return s3Service.uploadFile(avatar, userName);
        } else {
            throw new RuntimeException("Аватар не заполнен и не сохранен для User = " + userName);
        }
    }

    private byte[] getAvatar(String userName) {
        String urlUser = url + userName;
        return restTemplate.getForObject(urlUser, byte[].class);
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userMapper.toDto(userRepository.findAllById(ids));
    }

    @Transactional(readOnly = true)
    public List<Long> getUserIds() {
        return userRepository.findUserIds();
    }

    @Transactional
    public void deactivateProfile(long userId) {
        User user = findById(userId);
        user.getParticipatedEvents().clear();
        eventService.deleteALLEventByUserId(userId);
        user.getGoals().stream()
                .filter(goal -> goal.getUsers().size() == 1 || goal.getInvitations().isEmpty())
                .forEach(goal -> goalService.deleteGoal(goal.getId()));
        user.setActive(false);
        List<User> mentees = user.getMentees();
        if (!mentees.isEmpty()) {
            mentees.forEach(mentee -> mentorshipService.stopMentoring(user, mentee));
        }
    }
}