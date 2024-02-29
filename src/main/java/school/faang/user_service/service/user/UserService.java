package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.stream.Stream;

@Component
public class UserService {

    public UserService(UserRepository userRepository, CountryService countryService, UserValidator userValidator, UserMapper userMapper, EventRepository eventRepository, MentorshipRepository mentorshipRepository, GoalRepository goalRepository, UserProfilePic generatedUserProfilePic, List<UserFilter> userFilters) {
        this.userRepository = userRepository;
        this.countryService = countryService;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.eventRepository = eventRepository;
        this.mentorshipRepository = mentorshipRepository;
        this.goalRepository = goalRepository;
        this.generatedUserProfilePic = generatedUserProfilePic;
        this.userFilters = userFilters;
    }

    private final UserRepository userRepository;
    private final CountryService countryService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;
    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;
    private final UserProfilePic generatedUserProfilePic;
    private final List<UserFilter> userFilters;

    public UserRegistrationDto createUser(UserRegistrationDto userDto) {
        User user = userMapper.toEntity(userDto);

        if (user.getUserProfilePic() == null) {
            user.setUserProfilePic(generatedUserProfilePic);
        }
        Country country = countryService.getCountryByTitle(userDto.getCountry());
        user.setCountry(country);

        User savedUser = userRepository.save(user);
        return userMapper.toRegDto(savedUser);
    }

    public UserDto getUserDtoById(long id) {
        userValidator.validateAccessToUser(id);
        return userMapper.toDto(getUserById(id));
    }

    public UserProfilePic getUserPicUrlById(long id) {
        userValidator.validateAccessToUser(id);
        return getUserById(id).getUserProfilePic();
    }

    public void deactivationUserById(long userId) {
        User user = getUserById(userId);
        stopGoalsAndDeleteEventsAndDeleteMentor(user);

        user.setActive(false);
        saveUser(user);
    }

    public boolean isOwnerExistById(Long id) {
        return userRepository.existsById(id);
    }

    public void saveUser(User savedUser) {
        if (isOwnerExistById(savedUser.getId())) {
            userRepository.save(savedUser);
        }
    }

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d not found", id)));
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        List<User> users = userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .flatMap(filter -> filter.apply(premiumUsers, userFilterDto)).toList();
        return userMapper.toDtoList(users);
    }

    private void stopGoalsAndDeleteEventsAndDeleteMentor(User user) {
        List<Goal> goals = user.getGoals();
        for (Goal goal : goals) {
            if (goal.getUsers().size() == 1) {
                goalRepository.deleteById(goal.getId());
            }
        }

        List<Event> events = user.getOwnedEvents();
        for (Event event : events) {
            eventRepository.deleteById(event.getId());
        }

        List<User> mentees = user.getMentees();
        for (User mentee : mentees) {
            List<User> menteeMentors = mentee.getMentors();
            User mentor = getUserById(user.getId());
            if (menteeMentors.remove(mentor)) {
                mentorshipRepository.save(mentee);
            }
        }
    }
}