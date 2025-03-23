package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EventService eventService;
    private final GoalService goalService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    @Override
    public boolean doesUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "User with id %d is not found".formatted(userId)));
    }

    @Override
    @Transactional
    public UserDto deactivateUser(long userId) {

        eventService.deleteEventByUserId(userId);
        eventService.deleteParticipationFromEvent(userId);
        goalService.deleteUserFromGoals(userId);
        mentorshipService.deleteMentorShipByDeactivatedUser(userId);
        mentorshipService.deleteMenteeByDeactivatedUser(userId);
        User user = getUserById(userId);
        user.setActive(false);
        User deactivatedUser = userRepository.save(user);

        return userMapper.toDto(deactivatedUser);
    }

    @Override
    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    @Override
    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User with ID {} does not exist", userId);
            throw new DataValidationException("User does not exist.");
        }
    }

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);

    }

    @Override
    public UserDto findUserAndReturnDto(Long userId) {

        User user = getUserById(userId);
        return userMapper.toDto(user);
    }
}
