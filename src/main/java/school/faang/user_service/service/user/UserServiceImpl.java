package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.AuthUserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.kafka.producer.UserUpdateProducer;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final AuthUserContext authUserContext;
    private final UserUpdateProducer userUpdateProducer;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipRequestService mentorshipRequestService;

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long requesterId, UpdateUserDto userDto) {
        long userId = authUserContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        user = updateUserFields(user, userDto);
        log.info("User {} updated", user.getId());

        userUpdateProducer.onUserUpdate(userMapper.toUserUpdate(user));

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateProfile(UpdateUserDto userDto) {
        long userId = authUserContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);
        user = updateUserFields(user, userDto);
        log.info("Update me. User {} updated", user.getId());

        userUpdateProducer.onUserUpdate(userMapper.toUserUpdate(user));

        return userMapper.toUserDto(user);
    }

    private User updateUserFields(User user, UpdateUserDto dto) {
        userMapper.update(dto, user);
        Country country = countryRepository.getByIdOrThrow(dto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        return user;
    }

    @Override
    public UserDto getUser(long userId) {
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId)));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userMapper.toUserDtos(userRepository.findAllById(userIds));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDto deactivateUserById(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);

        deletedUserFromGoals(userId, user.getGoals());
        deletedUserFromGoals(userId, user.getSetGoals());
        deletedUsersFromEvents(userId, user);
        user.setActive(false);

        userRepository.save(user);
        mentorshipRequestService.deactivateMentor(userId);

        log.info("User {} was deactivated", userId);
        return userMapper.toUserDto(user);
    }

    private void deletedUsersFromEvents(Long userId, User user) {
        user.getParticipatedEvents().forEach(event -> eventRepository.deleteById(event.getId(), userId)
        );
    }

    private void deletedUserFromGoals(Long userId, List<Goal> goals) {
        goals.forEach(goal -> goalRepository.deleteUserFromGoal(userId, goal.getId()));
    }

}
