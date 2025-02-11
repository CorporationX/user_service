package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.s3.AvatarS3Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private static MentorshipService mentorshipService;

    @Mock
    private static UserRepository userRepository;

    @Mock
    private static EventRepository eventRepository;

    @Mock
    private static GoalRepository goalRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private AvatarS3Service avatarS3Service;

    @InjectMocks
    private UserService userService;

    private long userId;
    private User user;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = User.builder()
                .id(userId)
                .active(true)
                .build();
    }

    @Test
    public void testGetUserSuccess() {
        User expectedUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUser(userId);

        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUserExists() {
        boolean expectedResult = true;

        when(userRepository.existsById(userId))
                .thenReturn(expectedResult);

        boolean actualResult = userService.userExists(userId);

        verify(userRepository, times(1))
                .existsById(eq(userId));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetUserThrowExceptionWhenNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserByIds() {
        List<User> users = List.of(user);
        when(userRepository.findAllById(anyList()))
                .thenReturn(users);

        List<User> actualUserList = userService.getUsersByIds(users);

        assertEquals(users.size(), actualUserList.size());
        assertEquals(users, actualUserList);
        verify(userRepository, times(1)).findAllById(anyList());
    }

    @Test
    public void testDeactivateUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deactivateUser(userId));
    }

    @Test
    public void testDeactivateUser() {
        Goal goal = new Goal();
        goal.setUsers(Collections.singletonList(user));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Collections.emptyList());

        userService.deactivateUser(userId);

        verify(userRepository).findById(userId);
        verify(goalRepository).findGoalsByUserId(userId);
        verify(eventRepository).findAllByUserId(userId);
        verify(eventRepository).findParticipatedEventsByUserId(userId);
        verify(userRepository).save(user);
        verify(mentorshipService).stopUserMentorship(userId);

        assertFalse(user.isActive());
    }

    @org.junit.Test
    public void testRemoveUserFromGoals() {
        Goal goal = new Goal();
        goal.setUsers(Collections.singletonList(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));

        userService.deactivateUser(userId);

        verify(goalRepository).delete(goal);
    }

    @Test
    public void testBanUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.banUser(userId);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertTrue(captured.isBanned());
    }

    @org.junit.Test
    public void testRemoveUserEvents() {
        Event event = new Event();
        event.setStatus(EventStatus.PLANNED);
        event.setAttendees(Collections.singletonList(user));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.singletonList(event));
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Collections.singletonList(event));

        userService.deactivateUser(userId);

        verify(eventRepository).save(event);
        verify(eventRepository, times(2)).save(event);
    }

    @Test
    void uploadAvatar_ShouldUpdateProfileAndDeleteOldAvatars() {
        Long userId = 1L;
        User user = new User();
        UserProfilePic oldProfile = new UserProfilePic("old-large", "old-small");
        user.setUserProfilePic(oldProfile);

        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        String size = "large";
        UserProfilePic newProfile = new UserProfilePic("new-large", "new-small");
        String expectedUrl = "new-url";
        Pair<UserProfilePic, String> uploadResult = Pair.of(newProfile, expectedUrl);

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarS3Service.uploadAvatar(file, size)).thenReturn(uploadResult);

        String result = userService.uploadAvatar(file, size);

        verify(avatarS3Service).deleteAvatar("old-large");
        verify(avatarS3Service).deleteAvatar("old-small");
        verify(userRepository).save(user);
        assertThat(user.getUserProfilePic()).isEqualTo(newProfile);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void downloadAvatar_WhenSizeIsLarge_ShouldUseSmallFileId() {
        Long userId = 1L;
        User user = new User();
        UserProfilePic profile = new UserProfilePic("large-key", "small-key");
        user.setUserProfilePic(profile);
        String expectedUrl = "expected-url";

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarS3Service.downloadAvatar("small-key")).thenReturn(expectedUrl);

        String result = userService.downloadAvatar("large");

        verify(avatarS3Service).downloadAvatar("small-key");
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void deleteAvatar_ShouldDeleteBothImageVersions() {
        Long userId = 1L;
        User user = new User();
        UserProfilePic profile = new UserProfilePic("large-key", "small-key");
        user.setUserProfilePic(profile);

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteAvatar();

        verify(avatarS3Service).deleteAvatar("large-key");
        verify(avatarS3Service).deleteAvatar("small-key");
        verify(userRepository).save(user);
        assertThat(user.getUserProfilePic()).isNull();
    }
}
