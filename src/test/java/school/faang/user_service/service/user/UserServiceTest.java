package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.dto.event.UserEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.filter.CreatingTestData;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.filter.UserCountryFilter;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapperImpl userMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    @InjectMocks
    private UserService userService;

    private final UserCountryFilter userCountryFilter = new UserCountryFilter();

    private final List<UserFilter> userFilters = List.of(userCountryFilter);

    @BeforeEach
    public void init() {
        userService = new UserService(userMapper, userValidator, userRepository, mentorshipService, goalService
                , eventRepository, userFilters, searchAppearanceEventPublisher);
    }

    @Test
    void test_GetUser_NotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(userId));
    }

    @Test
    void test_GetUser_ReturnsUser() {
        Long userId = 1L;
        User user = User.builder().id(1L).email("buk@mail.ru").username("buk").build();
        UserDto userExpected = userMapper.toDto(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertEquals(userExpected, userService.getUserDtoById(userId));
        verify(userRepository).findById(userId);

    }

    @Test
    void test_getUsersByIds_ReturnsUsers() {
        List<Long> ids = List.of(1L, 2L);
        User firstUser = User.builder().id(1L).email("buk@mail.ru").username("buk").build();
        User secondUser = User.builder().id(2L).email("duk@mail.ru").username("buk").build();
        List<UserDto> usersExpected = userMapper.toDto(List.of(firstUser, secondUser));

        when(userRepository.findAllById(ids)).thenReturn(List.of(firstUser, secondUser));

        assertEquals(usersExpected, userService.getUsersDtoByIds(ids));
        verify(userRepository).findAllById(ids);
    }

    @Test
    void testCreateUserSuccess() {
        UserDto userDto = new UserDto();
        User user = new User();


        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        assertDoesNotThrow(() -> userService.create(userDto));

        verify(userValidator).validatePassword(userDto);
        verify(userMapper).toEntity(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void testCreateUserFailure() {
        UserDto userDto = null;

        assertThrows(NullPointerException.class, () -> userService.create(userDto));

    }

    @Test
    void deactivateUserById() {
        User u = new User();
        u.setId(1L);
        u.setUsername("user");
        u.setActive(true);

        Goal g = new Goal();
        g.setId(1L);
        g.setStatus(GoalStatus.ACTIVE);
        g.setUsers(new ArrayList<>(List.of(u)));
        g.setMentor(u);

        User ment = new User();
        ment.setId(2L);
        ment.setMentors(new ArrayList<>(List.of(u)));

        Event ev = new Event();
        ev.setId(1L);
        ev.setOwner(u);
        ev.setStatus(EventStatus.PLANNED);

        u.setGoals(new ArrayList<>(List.of(g)));
        u.setMentees(new ArrayList<>(List.of(ment)));
        u.setOwnedEvents(new ArrayList<>(List.of(ev)));


        UserDto excepted = new UserDto();
        excepted.setId(1L);
        excepted.setUsername("user");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userRepository.save(u)).thenReturn(u);
        when(userMapper.toDto(u)).thenReturn(excepted);


        UserDto userDto = userService.deactivationUserById(u.getId());

        verify(userRepository, times(1)).findById(1L);
        verify(goalService, times(1)).deleteGoal(g.getId());
        verify(eventRepository, times(1)).deleteById(ev.getId());
        verify(mentorshipService, times(1)).deleteMentorForHisMentees(u.getId(), List.of(ment));
        verify(userRepository, times(1)).save(u);
        verify(userMapper, times(1)).toDto(u);
        assertFalse(u.isActive());
        assertEquals(excepted, userDto);
    }

    @Test
    void searchUser() {
        List<User> userList = CreatingTestData.createListUsers();
        UserDto secondUserDto = UserDto.builder()
                .username("Женя")
                .id(2L).countryId(2L).build();
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .countryName("Germany").build();

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toDto(userList.get(1))).thenReturn(secondUserDto);

        List<UserDto> correctReturnListUser = List.of(secondUserDto);
        assertEquals(correctReturnListUser, userService.searchUsersByFilter(userFilterDto, 5L));
    }
    @Test
    void testBanUserNotFound(){
        UserEvent userEvent = new UserEvent(1L);

        when(userRepository.findById(userEvent.getUserId())).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.banUser(userEvent));
        verify(userRepository).findById(userEvent.getUserId());
    }

    @Test
    void testBanUserSuccessful(){
        UserEvent userEvent = new UserEvent(1L);
        User user = new User();
        user.setBanned(true);

        when(userRepository.findById(userEvent.getUserId())).thenReturn(Optional.of(new User()));
        when(userRepository.save(user)).thenReturn(user);

        Assertions.assertDoesNotThrow(() -> userService.banUser(userEvent));
        verify(userRepository).findById(userEvent.getUserId());
        verify(userRepository).save(user);
    }
}

