package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.mentorship.MentorshipService;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private UserFilterService userFilterService;
    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void findPremiumUsers() {
        Stream<User> users = getUsers().stream();
        UserFilterDto userFilterDto = new UserFilterDto();

        when(userRepository.findPremiumUsers()).thenReturn(users);
        when(userFilterService.applyFilters(users, userFilterDto)).thenReturn(users);

        assertEquals(userServiceImpl.findPremiumUsers(userFilterDto), getUsers().stream().map(userMapper::toDto).toList());
    }

    @Test
    void deactivateUserById() {
        User deactivatedUser = User.builder().id(1L).build();
        User anotherUser = User.builder().id(2L).build();
        List<Goal> deactivatedUserGoals = getGoals(new ArrayList<>(List.of(deactivatedUser, anotherUser)));
        deactivatedUser.setGoals(deactivatedUserGoals);

        when(userRepository.findById(1L)).thenReturn(Optional.of(deactivatedUser));
        doNothing().when(eventService).deleteAll(any());
        doNothing().when(mentorshipService).deleteMentorFromMentee(any());
        when(userRepository.save(deactivatedUser)).thenReturn(deactivatedUser);

        userServiceImpl.deactivateUserById(1L);

        assertFalse(deactivatedUser.isActive());
        assertEquals(1, deactivatedUser.getGoals().get(0).getUsers().size());
        assertEquals(0, deactivatedUser.getGoals().get(1).getUsers().size());
        assertEquals(1, deactivatedUser.getGoals().get(2).getUsers().size());
    }

    private List<Goal> getGoals(List<User> users) {
        return new ArrayList<>(List.of(
                Goal.builder().id(1L).users(users).build(),
                Goal.builder().id(2L).users(new ArrayList<>(List.of(users.get(0)))).build(),
                Goal.builder().id(3L).users(users).build()
        ));
    }


    @Test
    public void testGetUser() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        userServiceImpl.getUser(userId);
        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Mockito.when(userRepository.findAllById(ids)).thenReturn(List.of(new User(), new User()));
        userServiceImpl.getUsersByIds(ids);
        Mockito.verify(userRepository).findAllById(ids);
    }


    private List<User> getUsers() {
        return new ArrayList<>(List.of(
                User.builder().id(1L).premium(new Premium()).build(),
                User.builder().id(2L).premium(new Premium()).build(),
                User.builder().id(3L).premium(new Premium()).build()
        ));
    }
}
