package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.kafka.FollowerEvent;
import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.kafka.producer.FollowerProducer;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.service.user.UserSubscriptionServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserSubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;

    @Mock
    private FollowerProducer followerProducer;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private UserSubscriptionServiceImpl userSubscriptionServiceImpl;

    @Test
    void followUser_success() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertDoesNotThrow(() -> userSubscriptionServiceImpl.followUser(followerId, followeeId));

        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
        verify(followerProducer, times(1)).sendToKafka(any(FollowerEvent.class));
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void followUser_selfFollow_forbidden() {
        long id = 1L;
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> userSubscriptionServiceImpl.followUser(id, id)
        );
        assertEquals("Self-following is not allowed.", exception.getMessage());
        verifyNoInteractions(subscriptionRepository);
    }

    @Test
    void followUser_alreadyFollowing_validationError() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> userSubscriptionServiceImpl.followUser(followerId, followeeId)
        );
        assertEquals("You already follow this user.", exception.getMessage());
        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void unfollowUser_success_callsRepositoryWithCorrectArgs() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        userSubscriptionServiceImpl.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void getFollowersCount_returnsDto() {
        long followeeId = 111L;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(42);

        CountResponseDto dto = userSubscriptionServiceImpl.getFollowersCount(followeeId);

        assertNotNull(dto);
        assertEquals(42, dto.count());
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void getFolloweesCount_returnsDto() {
        long followerId = 222L;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(7);

        CountResponseDto dto = userSubscriptionServiceImpl.getFolloweesCount(followerId);

        assertNotNull(dto);
        assertEquals(7, dto.count());
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void getFollowers_mapsEntitiesToDtos() {
        User u1 = new User();
        u1.setId(3L);
        u1.setUsername("lily");
        User u2 = new User();
        u2.setId(4L);
        u2.setUsername("tina");

        long followeeId = 300L;

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(u1, u2));

        List<UserDto> result = userSubscriptionServiceImpl.getFollowers(followeeId);

        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(d -> d.id() == 3L && "lily".equals(d.username())));
        assertTrue(result.stream().anyMatch(d -> d.id() == 4L && "tina".equals(d.username())));

        verify(subscriptionRepository).findByFolloweeId(followeeId);
        verify(userMapper).toUserDto(u1);
        verify(userMapper).toUserDto(u2);
        verifyNoMoreInteractions(subscriptionRepository, userMapper);
    }

    @Test
    void getFollowees_mapsEntitiesToDtos() {
        User u1 = new User();
        u1.setId(3L);
        u1.setUsername("lily");
        User u2 = new User();
        u2.setId(4L);
        u2.setUsername("tina");

        long followerId = 400L;

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.of(u1, u2));

        List<UserDto> result = userSubscriptionServiceImpl.getFollowees(followerId);

        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(d -> d.id() == 3L && "lily".equals(d.username())));
        assertTrue(result.stream().anyMatch(d -> d.id() == 4L && "tina".equals(d.username())));

        verify(subscriptionRepository).findByFollowerId(followerId);
        verify(userMapper).toUserDto(u1);
        verify(userMapper).toUserDto(u2);
        verifyNoMoreInteractions(subscriptionRepository, userMapper);
    }

    @Test
    public void followNonExistentProjectOrByNonExistentfollower() {
        long anyLong = 1L;
        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(anyLong, anyLong)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> userSubscriptionServiceImpl.followProject(anyLong, anyLong));
    }

    @Test
    public void followProjectSuccessfullyFollows() {
        long anyLong = 1L;

        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(anyLong, anyLong)).thenReturn(true);

        userSubscriptionServiceImpl.followProject(anyLong, anyLong);

        verify(projectSubscriptionRepository, times(1))
                .existsByFollowerIdAndProjectId(anyLong, anyLong);

        verify(projectSubscriptionRepository, times(1)).followProject(anyLong, anyLong);

        verify(followerProducer, times(1)).sendToKafka(any(FollowerEvent.class));
    }
}
