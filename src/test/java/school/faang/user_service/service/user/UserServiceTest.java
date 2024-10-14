package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.AvatarStyle;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.user.UserDeactivatedException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserEmailFilter;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.filter.UserUsernameFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends AbstractUserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private AvatarService avatarService;
    @Mock  
    private PremiumRepository premiumRepository;
    @InjectMocks
    private UserService userService;

    private User user;
    private static List<UserFilter> userFilters;

    @BeforeAll
    static void setupAll() {
        var userUsernameFilter = new UserUsernameFilter();
        var userEmailFilter = new UserEmailFilter();
        userFilters = Arrays.asList(userUsernameFilter, userEmailFilter);
    }

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userService, "userFilters", userFilters);

        user = new User();
        user.setId(1L);
        user.setActive(true);

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Goal 1");
        goal1.setUsers(new ArrayList<>(List.of(user)));

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Goal 2");
        goal2.setUsers(new ArrayList<>(List.of(user, new User())));
        user.setGoals(new ArrayList<>(List.of(goal1, goal2)));

        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Webinar Java");
        event1.setStatus(EventStatus.PLANNED);
        event1.setOwner(user);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Webinar Spring");
        event2.setStatus(EventStatus.COMPLETED);
        event2.setOwner(user);
        user.setOwnedEvents(new ArrayList<>(List.of(event1, event2)));
    }

    @Test
    public void deactivateUserSuccess() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<Goal> userGoals = new ArrayList<>(user.getGoals());
        List<Event> userEvents = new ArrayList<>(user.getOwnedEvents());

        userService.deactivateUser(userId);

        verify(userRepository).findById(userId);
        verify(goalService).deleteGoalAndUnlinkChildren(userGoals.get(0));
        verify(eventRepository).save(userEvents.get(0));
        verify(eventRepository).delete(userEvents.get(0));
        verify(mentorshipService).deleteMentorFromMentees(userId, user.getMentees());
        verify(userRepository).save(user);

        assertEquals(user.getGoals().size(), 0);
    }

    @Test
    public void deactivateUserFailed() {
        Long userId = 1L;
        user.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserDeactivatedException.class, () -> userService.deactivateUser(userId));
    }
  
    @Test
    public void testRegisterUser() {
        User user = new User();
        user.setUsername("testuser");

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("avatar.png");
        userProfilePic.setSmallFileId("avatar_small.png");

        when(avatarService.generateAndSaveAvatar(AvatarStyle.BOTTTTS)).thenReturn(userProfilePic);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNotNull(result.getUserProfilePic());
        assertEquals("avatar.png", result.getUserProfilePic().getFileId());

        verify(userRepository, times(1)).save(user);
        verify(avatarService, times(1)).generateAndSaveAvatar(AvatarStyle.BOTTTTS);
    }

    @Test
    void testGetPremiumUsers() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .build();

        Premium premiumToFind = Premium.builder()
                .user(createUser(USERNAME, EMAIL))
                .build();
        Premium premiumToNotFind = Premium.builder()
                .user(createUser("", ""))
                .build();
        List<Premium> premiums = List.of(premiumToFind, premiumToNotFind);

        when(premiumRepository.findAll()).thenReturn(premiums);

        List<User> result = userService.getPremiumUsers(userFilterDto);

        assertEquals(1, result.size());
        assertEquals(USERNAME, result.get(0).getUsername());
        assertEquals(EMAIL, result.get(0).getEmail());
    }


    @Test
    void testGetUser() {
        long foundId = 1L;
        long notFoundId = 100L;
        User user = createUser(USERNAME, EMAIL);

        when(userRepository.findById(foundId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(notFoundId)).thenReturn(Optional.empty());

        User resultFound = userService.getUser(foundId);
        assertEquals(USERNAME, resultFound.getUsername());
        assertEquals(EMAIL, resultFound.getEmail());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(notFoundId));
    }

    @Test
    void testGetUsers() {
        List<Long> idsFound = Arrays.asList(1L, 2L);
        List<Long> idsNotFound = Arrays.asList(100L, 200L);
        List<User> users = List.of(createUser(USERNAME, EMAIL), createUser(USERNAME, EMAIL));

        when(userRepository.findAllById(idsFound)).thenReturn(users);
        when(userRepository.findAllById(idsNotFound)).thenReturn(Collections.emptyList());

        List<User> resultFound = userService.getUsers(idsFound);
        List<User> resultNotFound = userService.getUsers(idsNotFound);

        assertEquals(2, resultFound.size());
        assertEquals(USERNAME, resultFound.get(0).getUsername());
        assertEquals(EMAIL, resultFound.get(0).getEmail());

        assertEquals(0, resultNotFound.size());
    }

    @Test
    void testBannedUser() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.bannedUser(userId);

        assertTrue(user.isBanned());
        verify(userRepository).save(user);
    }
}
