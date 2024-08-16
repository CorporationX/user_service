package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private EventRepository eventRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserService userService;

    private User user;
    private long userId;
    private UserDto userDto;
    private User mentee;
    private Goal mentorAssignedGoal;
    private long id;
    private List<Long> ids;
    private List<UserDto> userDtoList;

    @BeforeEach
    public void setUp() {
        UserMapperImpl userMapperImpl = new UserMapperImpl();
        userId = 1L;

        userDto = UserDto.builder()
                .id(userId)
                .username("username")
                .password("password")
                .country(1L)
                .email("test@mail.com")
                .phone("123456")
                .build();

        user = userMapperImpl.toEntity(userDto);
        List<Goal> goalList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<User> menteesList = new ArrayList<>();
        List<Event> ownedEvents = new ArrayList<>();

        id = 10L;
        ids = List.of(id);

        userDto = new UserDto();

        userDtoList = List.of(userDto);
        user = User.builder()
                .id(1L)
                .goals(goalList)
                .ownedEvents(ownedEvents)
                .mentees(menteesList).build();
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
        verify(userRepository, times(2)).save(user);
        verify(avatarService, times(1)).setRandomAvatar(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("testing updateUserAvatar method with null multipartFile")
    public void testUpdateUser() {
        when(userValidator.validateUserExistence(user.getId())).thenReturn(user);
        userService.updateUserAvatar(userId, null);
        verify(avatarService, times(1)).setRandomAvatar(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("test that getUser calls all methods correctly + return test")
    public void testGetUser() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(id);

        verify(userValidator).validateUserExistence(id);
        verify(userRepository).findById(id);
        verify(userMapper).toDto(user);

        assertEquals(result, userDto);
    }

    @Test
    @DisplayName("test that getUsersByIds calls all methods correctly + return test")
    public void testGetUsersByIds() {
        when(userRepository.findAllById(ids)).thenReturn(List.of(user));
        when(userMapper.toDtoList(List.of(user))).thenReturn(userDtoList);

        List<UserDto> result = userService.getUsersDtoByIds(ids);

        verify(userValidator).validateUserExistence(id);
        verify(userRepository).findAllById(ids);
        verify(userMapper).toDtoList(List.of(user));

        assertEquals(result, userDtoList);
    }

    @Test
    @DisplayName("testing deactivateUser by providing non existing user id")
    public void testDeactivateUserWithNonExistingUserId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(user.getId()));
    }

    @Test
    @DisplayName("testing deactivateUser by goalRepository deleteAll method execution")
    public void testDeactivateUserWithRepositoryGoalDeletion() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deactivateUser(user.getId());
        verify(goalRepository, times(1)).deleteAll(any());
    }

    @Test
    @DisplayName("testing deactivateUser by eventRepository saveAll method execution")
    public void testDeactivateUserWithEventRepositorySaveExecution() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deactivateUser(user.getId());
        verify(eventRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("testing deactivateUser by deleteMentor method execution")
    public void testDeactivateUserWithDeleteMethodExecution() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deactivateUser(user.getId());
        verify(mentorshipService, times(1)).deleteMentor(mentee.getId(), user.getId());
        assertEquals(mentorAssignedGoal.getMentor(), mentee);
    }
}
