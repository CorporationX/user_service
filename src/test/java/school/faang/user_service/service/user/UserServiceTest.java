package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.s3.AvatarS3Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

    @Spy
    private UserMapperImpl userMapperImpl;

    @Mock
    private UserAvatarService userAvatarService;

    @InjectMocks
    private UserService userService;

    private long userId;
    private User user;

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

    @Test
    public void testRemoveUserFromGoals() {
        Goal goal = new Goal();
        goal.setUsers(Collections.singletonList(user));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));

        userService.deactivateUser(userId);

        verify(goalRepository).deleteAll(List.of(goal));
    }

    @Test
    public void testRemoveUserEvents() {
        Event event = new Event();
        event.setOwner(user);
        event.setStatus(EventStatus.PLANNED);
        event.setAttendees(Collections.singletonList(user));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.singletonList(event));
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Collections.singletonList(event));

        userService.deactivateUser(userId);

        verify(eventRepository).saveAll(List.of(event));
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

    @Test
    void testGetUsersByIds() {
        List<Long> userIds = List.of(userId);
        List<User> users = List.of(user);
        UserDto userDto = userMapperImpl.toDto(user);
        List<UserDto> userDtos = List.of(userDto);

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByIdIn(anyList(), any(Pageable.class))).thenReturn(users);
        when(userMapperImpl.toDto(any(User.class))).thenReturn(userDto);
        when(userRepository.countByIdIn(anyList())).thenReturn(1L);

        Page<UserDto> result = userService.getUsersByIds(userIds, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(userDtos, result.getContent());
    }

    @Test
    void testGetUsersByIds_EmptyList() {
        List<Long> userIds = Collections.emptyList();
        List<User> users = Collections.emptyList();
        List<UserDto> userDtos = Collections.emptyList();

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByIdIn(anyList(), any(Pageable.class))).thenReturn(users);
        when(userRepository.countByIdIn(anyList())).thenReturn(0L);

        Page<UserDto> result = userService.getUsersByIds(userIds, pageable);

        assertEquals(0, result.getTotalElements());
        assertEquals(userDtos, result.getContent());
    }

    @Test
    void addUserRatingScore_EmptyRating() {
        Double scoreToAdd = 10.0;
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        userService.addUserRatingScore(1L, scoreToAdd);

        verify(userRepository, times(1)).save(user);
        assertEquals(scoreToAdd, user.getRatingScore());
    }

    @Test
    void addUserRatingScore_NotEmptyRating() {
        user.setRatingScore(5.0);
        Double scoreToAdd = 10.0;
        Double expectedScore = user.getRatingScore() + scoreToAdd;
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        userService.addUserRatingScore(1L, scoreToAdd);

        verify(userRepository, times(1)).save(user);
        assertEquals(expectedScore, user.getRatingScore());
    }

    @Test
    void addUserRatingScore_NegativeRating() {
        Double scoreToAdd = -10.0;
        Double expectedScore = 0.0;
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        userService.addUserRatingScore(1L, scoreToAdd);

        verify(userRepository, times(1)).save(user);
        assertEquals(expectedScore, user.getRatingScore());
    }

    @Test
    void findAllUsersWithRatingScores() {
        int topUsersLimit = 10;
        Pageable pageable = PageRequest.ofSize(topUsersLimit)
                .withSort(Sort.by("ratingScore").descending());
        user.setRatingScore(10.0);
        List<User> users = new ArrayList<>();
        users.add(user);

        Map<UserDto, Double> expectedUsersWithRatingScores = users.stream().collect(Collectors.toMap(userMapperImpl::toDto, User::getRatingScore));
        when(userRepository.findAllUsersWithRatingScores(any(Pageable.class))).thenReturn(users);

        Map<UserDto, Double> usersWithRatingScores = userService.findAllUsersWithRatingScores(topUsersLimit);

        verify(userRepository, times(1)).findAllUsersWithRatingScores(pageable);
        assertEquals(expectedUsersWithRatingScores, usersWithRatingScores);
    }

    @Test
    void resetAllRatingScores() {
        doNothing().when(userRepository).resetAllRatingScores();

        userService.resetAllRatingScores();

        verify(userRepository, times(1)).resetAllRatingScores();
    }
}
