package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipRequestService mentorshipRequestService;
    @InjectMocks
    public UserServiceImpl userService;

    private static final long USER_ID = 1L;
    private static final long USER_TWO_ID = 2L;
    private static final long COUNTRY_ID = 77L;
    private static final long GOAL_ID = 1L;
    private static final long GOAL_TWO_ID = 2L;
    private static final long EVENT_ID = 1L;
    private static final String COUNTRY = "USA";
    private static final String USER_NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "JOn1234!";
    private static final String ABOUT_ME = "aboutMe";

    @Test
    @DisplayName("Should get the user by ID")
    public void testGetUser() {
        User user = createUser(USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto dto = userService.getUser(USER_ID);
        UserDto result = createUserDto(USER_ID);

        assertEquals(dto, result);

        verify(userMapper).toUserDto(user);
    }

    @Test
    @DisplayName("Should get users from the list of IDs")
    public void testGetUsersByIds() {
        List<Long> userIds = List.of(USER_ID, USER_TWO_ID);
        User userOne = createUser(USER_ID);
        User userTwo = createUser(USER_TWO_ID);
        List<User> users = List.of(userOne, userTwo);

        when(userRepository.findAllById(userIds)).thenReturn(users);

        List<UserDto> listDto = userService.getUsersByIds(userIds);
        List<UserDto> result = List.of(createUserDto(USER_ID), createUserDto(USER_TWO_ID));

        assertEquals(listDto, result);

        verify(userMapper).toUserDto(userOne);
        verify(userMapper).toUserDto(userTwo);
    }

    @Test
    @DisplayName("Should create a new user")
    public void testCreateUser() {
        CreateUserDto createUserDto = createCreateDto();
        User user = createUser(USER_ID);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(countryRepository.getByIdOrThrow(createUserDto.countryId())).thenReturn(createCountry());

        UserDto result = userService.create(createUserDto);

        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USER_NAME, result.username());
        assertEquals(EMAIL, result.email());

        verify(userMapper).toUser(createUserDto);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDto(user);
    }

    @Test
    @DisplayName("Should delete user from goals")
    public void deleteUserFromGoals() {
        User user = createUser(USER_ID);
        List<Goal> goals = List.of(createGoal(GOAL_ID));
        List<Goal> setGoals = List.of(createGoal(GOAL_TWO_ID));
        user.setGoals(goals);
        user.setSetGoals(setGoals);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);

        userService.deactivateUserById(USER_ID);
        verify(goalRepository).deleteUserFromGoal(USER_ID, GOAL_ID);
        verify(goalRepository).deleteUserFromGoal(USER_ID, GOAL_TWO_ID);
    }

    @Test
    @DisplayName("Should delete user from event")
    public void deleteUserFromEvent() {
        User user = createUser(USER_ID);
        user.setParticipatedEvents(List.of(createEvent(EVENT_ID)));

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);

        userService.deactivateUserById(USER_ID);
        verify(eventRepository).deleteById(USER_ID, EVENT_ID);
    }

    @Test
    @DisplayName("Should set active to false for user")
    public void setFalseActiveForUser() {
        User user = createUser(USER_ID);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);

        userService.deactivateUserById(USER_ID);
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("Should deactivate mentor from mentorship")
    public void deactivateMentor() {
        User user = createUser(USER_ID);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);

        userService.deactivateUserById(USER_ID);
        verify(mentorshipRequestService).deactivateMentor(USER_ID);
    }

    private CreateUserDto createCreateDto() {
        return new CreateUserDto(USER_NAME, EMAIL, PASSWORD, COUNTRY_ID);
    }

    private User createUser(long id) {
        return User.builder()
                .id(id)
                .username(USER_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .aboutMe(ABOUT_ME)
                .country(createCountry())
                .goals(new ArrayList<>())
                .setGoals(new ArrayList<>())
                .participatedEvents(new ArrayList<>())
                .build();
    }

    private UserDto createUserDto(long id) {
        return new UserDto(id, USER_NAME, EMAIL, null, ABOUT_ME);
    }

    private Country createCountry() {
        return Country.builder()
                .id(COUNTRY_ID)
                .title(COUNTRY)
                .build();
    }

    private Goal createGoal(long id) {
        return Goal.builder()
                .id(id)
                .build();
    }

    private Event createEvent(long id) {
        return Event.builder()
                .id(id)
                .build();
    }
}