package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.ProfilePicServiceImpl;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.filter.UserFilterService;
import school.faang.user_service.service.user.mentorship.MentorshipService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFilterService userFilterService;
    private final UserMapper userMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final ProfilePicServiceImpl profilePicService;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto){
        User user = userMapper.toEntity(userDto);
        user.setActive(true);
        user.setUserProfilePic(profilePicService.generatePic());
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
    }

    @Override
    public List<UserDto> findPremiumUsers(UserFilterDto filterDto) {
        return userFilterService.applyFilters(userRepository.findPremiumUsers(), filterDto)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deactivateUserById(Long id) {
        User user = findUserById(id);
        List<Goal> userGoals = user.getGoals();

        userGoals.forEach(goal -> {
            List<User> goalUsers = goal.getUsers();
            goalUsers.remove(user);
            if (goalUsers.isEmpty()) {
                goalService.delete(goal);
            }
        });

        eventService.deleteAll(user.getOwnedEvents());
        mentorshipService.deleteMentorFromMentee(user);

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
