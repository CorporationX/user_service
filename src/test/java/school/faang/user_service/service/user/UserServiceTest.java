package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.filter.user.UserCityFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private CountryService countryService;
    @Mock
    private UserValidator validator;
    @Mock
    private UserProfilePic profilePic;
    @Spy
    private UserMapperImpl userMapper;

    private static final UserEmailFilter userEmailFilter = new UserEmailFilter();

    private static final UserCityFilter userCityFilter = new UserCityFilter();

    private static UserFilterDto dtoFilter = new UserFilterDto();

    private List<UserFilter> filters = new ArrayList<>();

    private List<User> users;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void init() {
        userService = new UserService(userRepository, countryService, validator, userMapper, eventRepository,
                mentorshipRepository, goalRepository, profilePic, filters);
    }

    @Test
    public void testGetUserByIdFailed() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = new User();
        user.setId(1L);
        Mockito.lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Assertions.assertEquals(user, userService.getUserById(1L));
    }

    @Test
    public void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        when(userRepository.existsById(user.getId())).thenReturn(true);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);

        assertTrue(userService.isOwnerExistById(user.getId()));
    }

    @Test
    void successDeactivationUserById() {
        List<Goal> goals = List.of(Goal.builder()
                .id(1L)
                .users(List.of(User.builder()
                        .id(10L)
                        .active(true)
                        .build()))
                .build());
        List<Event> events = List.of(Event.builder()
                .id(1L)
                .maxAttendees(2)
                .build());
        List<User> mentees = List.of(User.builder()
                .id(2L)
                .active(true)
                .mentors(new ArrayList<>(List.of(User.builder().id(15L).active(true).build())))
                .build());
        User user = User.builder()
                .id(1L)
                .active(true)
                .goals(goals)
                .ownedEvents(events)
                .mentees(mentees)
                .build();
        long userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.deactivationUserById(userId);

        assertFalse(user.isActive());
    }

    @Test
    public void testGetPremiumUsersEmailFilter_Success() {
        users = List.of(
                User.builder().email("r123467@gmail.com").build(),
                User.builder().email("k2jsd@mail.ru").build(),
                User.builder().email("dsjfzn22222@yandex.ru").build()
        );
        dtoFilter.setEmailPattern("mail");
        filters.add(userEmailFilter);
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(users.stream());
        List<UserDto> premiumUsers = userService.getPremiumUsers(dtoFilter);
        Assertions.assertEquals(2, premiumUsers.size());
        Assertions.assertEquals(premiumUsers.get(0).getEmail(), "r123467@gmail.com");
        Assertions.assertEquals(premiumUsers.get(1).getEmail(), "k2jsd@mail.ru");

    }

    @Test
    public void testGetPremiumUsersNameFilter_Success() {
        users = List.of(
                User.builder().city("Moscow").build(),
                User.builder().city("Montreal").build(),
                User.builder().city("Florida").build()
        );
        dtoFilter.setCityPattern("Moscow");
        filters.add(userCityFilter);
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(users.stream());
        List<UserDto> premiumUsers = userService.getPremiumUsers(dtoFilter);
        Assertions.assertEquals(1, premiumUsers.size());
    }
}