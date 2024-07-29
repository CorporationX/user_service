package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String MESSAGE_USER_NOT_EXIST = "User does not exist";
    private static final String MESSAGE_USER_ALREADY_DEACTIVATED = "User is already deactivated";
    private static final int VALID_ID = 1;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        //Arrange
        user = new User();
        user.setId(VALID_ID);
        user.setActive(true);
        Goal goal = new Goal();
        goal.setId((long) VALID_ID);
        goal.setUsers(List.of(new User()));
        user.setGoals(List.of(goal));
        user.setOwnedEvents(List.of(new Event(), new Event()));
    }

    @Test
    void getUserTest_whenException() {
        assertThrows(RuntimeException.class, () -> userService.getUser(1L));
    }

    @Test
        public void testUserIsNotInDb() {
        //Act
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        //Assert
        assertEquals(
                MESSAGE_USER_NOT_EXIST,
                assertThrows(
                        RuntimeException.class,
                        () -> userService.deactivatesUserProfile(user.getId())).getMessage());
    }

    @Test
    public void testUserAlreadyDeactivated() {
        //Arrange
        user.setActive(false);
        //Act
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        //Assert
        assertEquals(
                MESSAGE_USER_ALREADY_DEACTIVATED,
                assertThrows(
                        RuntimeException.class,
                        () -> userService.deactivatesUserProfile(user.getId())).getMessage());
    }

    @Test
    public void testGoalDeletedById() {
        //Act
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        //Assert
        userService.deactivatesUserProfile(user.getId());
        Mockito.verify(goalRepository).deleteById(Mockito.anyLong());
    }

    @Test
    public void testEventDeletedById() {
        //Act
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        //Assert
        userService.deactivatesUserProfile(user.getId());
        Mockito.verify(eventRepository, Mockito.times(user.getOwnedEvents().size())).deleteById(Mockito.anyLong());
    }

    @Test
    public void testUserSave() {
        //Act
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(mentorshipService.stopMentorship(Mockito.any())).thenReturn(user);
        //Assert
        userService.deactivatesUserProfile(user.getId());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    public void testUserToUserDto() {
        //Act
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(mentorshipService.stopMentorship(Mockito.any())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        //Assert
        userService.deactivatesUserProfile(user.getId());
        Mockito.verify(mapper).toDto(user);
    }

    @Test
    @DisplayName("Тест получаем пользователя")
    public void getUser() {
        Long userId = 1L;
        userService.getUser(userId);
        Mockito.verify(userRepository,Mockito.times(1))
                .findById(userId);
    }

    @Test
    @DisplayName("Тест получаем всех пользователей")
    public void getUsersByIds() {
        User firstUser = new User();
        User secondUser = new User();
        firstUser.setId(1L);
        secondUser.setId(2L);
        List<Long> ids = List.of(firstUser.getId(), secondUser.getId());
        List<UserDto> dtoList = ids.stream()
                .map(num -> userRepository.findById(num))
                .map(result -> mapper.toDto(result))
                .toList();
        assertEquals(dtoList , userService.getUsersByIds(ids));
    }

}