package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserTransportDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.handler.EntityHandler;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private AvatarService avatarService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityHandler entityHandler;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserService userService;

    private long userId;
    private User user;
    private UserDto userDto;
    private UserTransportDto userTransportDto;
    private User mentee;
    private Goal mentorAssignedGoal;
    private List<UserTransportDto> userTransportDtoList;
    private List<User> userFollowers;
    private List<Long> userIds;

    @BeforeEach
    public void setUp() {
        List<Goal> goalList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<User> menteesList = new ArrayList<>();
        List<Event> ownedEvents = new ArrayList<>();

        userId = 1L;
        userIds = List.of(userId);
        long countryId = 2L;
        userFollowers = List.of(new User());

        user = User.builder()
                .id(userId)
                .username("username")
                .password("password")
                .country(Country.builder()
                        .id(countryId).build())
                .followers(userFollowers)
                .email("test@mail.com")
                .phone("123456")
                .goals(goalList)
                .ownedEvents(ownedEvents)
                .mentees(menteesList).build();
        userDto = UserDto.builder()
                .id(userId)
                .username("username")
                .country(1L)
                .email("test@mail.com")
                .phone("123456")
                .build();
        userTransportDto = UserTransportDto.builder()
                .id(userId)
                .username("username")
                .email("test@mail.com")
                .phone("123456")
                .build();

        userTransportDtoList = List.of(userTransportDto);

        Goal goal = Goal.builder()
                .id(1L)
                .users(userList).build();
        mentorAssignedGoal = Goal.builder()
                .mentor(user).build();
        mentee = User.builder()
                .id(2L)
                .goals(List.of(mentorAssignedGoal)).build();
        Event event = Event.builder().id(1L)
                .status(EventStatus.PLANNED).build();
        menteesList.add(mentee);
        goalList.add(goal);
        userList.add(user);
        ownedEvents.add(event);
    }

    @Test
    @DisplayName("testing createUser method with null multipartFile")
    public void testCreateUser() {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.createUser(userDto, null);

        verify(userMapper, times(1)).toEntity(userDto);
        verify(userRepository, times(2)).save(user);
        verify(avatarService, times(1)).setRandomAvatar(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("testing updateUserAvatar method with null multipartFile")
    public void testUpdateUserAvatar() {
        when(entityHandler.getOrThrowException(eq(User.class), eq(userId), any())).thenReturn(user);

        userService.updateUserAvatar(userId, null);

        verify(entityHandler, times(1)).getOrThrowException(eq(User.class), eq(userId), any());
        verify(avatarService, times(1)).setRandomAvatar(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("test that getUser calls all methods correctly + return test")
    public void testGetUser() {
        when(entityHandler.getOrThrowException(eq(User.class), eq(userId), any())).thenReturn(user);
        when(userMapper.toTransportDto(user)).thenReturn(userTransportDto);

        UserTransportDto result = userService.getUser(userId);

        verify(entityHandler, times(1)).getOrThrowException(eq(User.class), eq(userId), any());
        verify(userMapper).toTransportDto(user);

        assertEquals(result, userTransportDto);
    }

    @Test
    @DisplayName("test that getUsersByIds calls all methods correctly + return test")
    public void testGetUsersByIds() {
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user));
        when(userMapper.toTransportDto(user)).thenReturn(userTransportDto);

        List<UserTransportDto> result = userService.getUsersByIds(userIds);

        verify(userRepository).findAllById(userIds);
        verify(userMapper).toTransportDto(user);

        assertEquals(result, userTransportDtoList);
    }

    @Test
    @DisplayName("testing deactivateUser by providing non existing user id")
    public void testDeactivateUserWithNonExistingUserId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(user.getId()));
    }

    @Test
    @DisplayName("testing deactivateUser deleteAll methods execution")
    public void testDeactivateUserWithRepositoryGoalDeletion() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deactivateUser(user.getId());
        verify(goalRepository, times(1)).deleteAll(any());
        verify(eventRepository, times(1)).saveAll(any());
        verify(mentorshipService, times(1)).deleteMentor(mentee.getId(), user.getId());
        assertEquals(mentorAssignedGoal.getMentor(), mentee);
    }

    @Test
    @DisplayName("testing checkUserExistence with non existing value")
    public void testCheckUserExistenceWithNonAppropriateValue() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.checkUserExistence(userId));
    }

    @Test
    @DisplayName("testing checkUserExistence with existing value")
    public void testCheckUserExistenceWithAppropriateValue() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertFalse(userService.checkUserExistence(userId));
    }

    @Test
    @DisplayName("testing getUserFollowers method")
    public void testGetUserFollowers() {
        when(entityHandler.getOrThrowException(eq(User.class), eq(userId), any())).thenReturn(user);
        userService.getUserFollowers(userId);
        verify(entityHandler, times(1)).getOrThrowException(eq(User.class), eq(userId), any());
        verify(userMapper, times(userFollowers.size())).toDto(any());
    }

    @Test
    @DisplayName("testing checkAllFollowersExist method")
    public void testCheckAllFollowersExist() {
        userService.checkAllFollowersExist(List.of(1L, 2L));
        verify(userValidator, times(1)).doAllUsersExist(anyList());
    }
}
