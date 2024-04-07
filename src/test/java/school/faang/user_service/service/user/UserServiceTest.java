package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.SearchAppearanceEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.user.UserCityFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private CountryService countryService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private CsvPersonParser csvPersonParser;
    @Mock
    private PersonService personService;
    @Mock
    private UserProfilePic generatedUserProfilePic;
    @Mock
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;
    private UserService userService;

    private final UserFilterDto dtoFilter = new UserFilterDto();

    private final List<UserFilter> userFilters = new ArrayList<>();

    private final UserEmailFilter userEmailFilter = new UserEmailFilter();

    private final UserCityFilter userCityFilter = new UserCityFilter();
    private List<User> users;
    private final long EXISTENCE_USER_ID = 1L;
    private final long NOT_EXISTENCE_USER_ID = 2L;

    @BeforeEach
    public void init() {

        userService = new UserService(userRepository, eventRepository, mentorshipRepository, goalRepository,
                countryService, personService,
                userValidator,
                userMapper,
                csvPersonParser, generatedUserProfilePic,
                userFilters,
                searchAppearanceEventPublisher, profileViewEventPublisher);
    }

    @Test
    public void testGetUsersWhenFilterApplicable() {
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("Kate");

        User user = new User();
        user.setId(1L);
        user.setUsername("Kate");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("Kate");

        SearchAppearanceEventDto searchAppearanceEventDto = new SearchAppearanceEventDto(
                1L,
                1L,
                LocalDateTime.now()
        );

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getUsers(filter, 1L);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    public void testGetUserByIdFailed() {
        mockRepoUserNotExists();
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(NOT_EXISTENCE_USER_ID));
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = new User();
        user.setId(EXISTENCE_USER_ID);
        mockRepoUserExists(user);
        Assertions.assertEquals(user, userService.getUserById(EXISTENCE_USER_ID));
    }

    @Test
    void getUserDtoByIdSuccess() {
        mockRepoUserExists(new User());
        UserDto userDto = userService.getUserDtoById(EXISTENCE_USER_ID);

        verify(profileViewEventPublisher, times(1)).publish(EXISTENCE_USER_ID);
        assertNotNull(userDto);
    }

    @Test
    void getUserDtoByIdFailed() {
        mockRepoUserNotExists();

        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserDtoByIdUtility(NOT_EXISTENCE_USER_ID)
        );
    }

    @Test
    public void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        mockRepoUserExists(user);

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
                .id(EXISTENCE_USER_ID)
                .active(true)
                .goals(goals)
                .ownedEvents(events)
                .mentees(mentees)
                .build();

        mockRepoUserExists(user);
        userService.deactivationUserById(EXISTENCE_USER_ID);

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
        userFilters.add(userEmailFilter);
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
        userFilters.add(userCityFilter);
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(users.stream());
        List<UserDto> premiumUsers = userService.getPremiumUsers(dtoFilter);
        Assertions.assertEquals(1, premiumUsers.size());
    }

    @Test
    public void testGetUsersWhenFilterApplicableAndUserListEmpty() {
        UserFilterDto filter = new UserFilterDto();
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers(filter, 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUsersWhenFilterNotApplicable() {
        UserFilterDto filter = new UserFilterDto();
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers(filter, 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void banUserById() {
        User user = User.builder().id(EXISTENCE_USER_ID).build();
        mockRepoUserExists(user);

        userService.banUserById(EXISTENCE_USER_ID);

        assertTrue(user.isBanned());
    }

    private void mockRepoUserNotExists() {
        when(userRepository.findById(NOT_EXISTENCE_USER_ID)).thenReturn(Optional.empty());
    }

    private void mockRepoUserExists(User user) {
        when(userRepository.findById(EXISTENCE_USER_ID)).thenReturn(Optional.of(user));
    }
}