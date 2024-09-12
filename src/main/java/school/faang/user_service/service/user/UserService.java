package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final DeactivateUserService deactivateUserService;

    public UserDto deactivateUser(UserDto userDto) {
        log.info("Деактивация пользователя с ID: {}", userDto.getId());
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new DataValidationException("Пользователь с ID " + userDto.getId() + " не найден"));
        deactivateUserService.stopUserActivities(user);
        user.setActive(false);
        mentorshipService.stopMentorship(user);
        userRepository.save(user);
        log.info("Пользователь с ID: {} был успешно деактивирован", userDto.getId());
        return userMapper.toDto(user);
    }
}