package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.service.user.UserSubscriptionServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserSubscriptionServiceImpl service;

    private long followerId;
    private long followeeId;

    @BeforeEach
    void setUp() {
        followerId = 1L;
        followeeId = 2L;
    }

    // -------------------------------------------------
    // followUser()
    // -------------------------------------------------
    @Test
    void followUser_shouldThrowException_whenSelfAction() {
        assertThrows(DataValidationException.class,
                () -> service.followUser(1L, 1L));
    }

    @Test
    void followUser_shouldThrowException_whenAlreadySubscribed() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> service.followUser(followerId, followeeId));
    }

    @Test
    void followUser_shouldThrowException_whenUserOwnAction() {
        assertThrows(ForbiddenException.class,
                () -> service.followUser(1L, 3L));
    }

    // -------------------------------------------------
    // unfollowUser()
    // -------------------------------------------------
    @Test
    void unfollowUser_shouldThrowException_whenNotSubscribed() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> service.unfollowUser(followerId, followeeId));
    }

    // -------------------------------------------------
    // getFollowersCount()
    // -------------------------------------------------
    @Test
    void getFollowersCount_shouldReturnCountResponse() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(Math.toIntExact(5L));

        CountResponse response = service.getFollowersCount(followeeId);

        assertThat(response.count()).isEqualTo(5);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);
    }

    // -------------------------------------------------
    // getFolloweesCount()
    // -------------------------------------------------
    @Test
    void getFolloweesCount_shouldReturnCountResponse() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(Math.toIntExact(3L));

        CountResponse response = service.getFolloweesCount(followerId);

        assertThat(response.count()).isEqualTo(3);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
    }

    // -------------------------------------------------
    // getFollowers()
    // -------------------------------------------------
    @Test
    void getFollowers_shouldReturnMappedAndFilteredUsers() {
        User user = new User();
        UserDto dto = new UserDto(1L, "John", "email", "+420123", "just a guy", 5);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user));
        when(userMapper.toUserDto(user)).thenReturn(dto);

        UserFiltersDto filters = new UserFiltersDto("John", "420", 0, 10);
        List<UserDto> result = service.getFollowers(followeeId, filters);

        assertThat(result).hasSize(1);
    }

    // -------------------------------------------------
    // getFollowees()
    // -------------------------------------------------
    @Test
    void getFollowees_shouldReturnMappedAndFilteredUsers() {
        User user = new User();
        UserDto dto = new UserDto(1L, "Jane", "EMAIL", "+420999", "girl", 2);

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.of(user));
        when(userMapper.toUserDto(user)).thenReturn(dto);

        UserFiltersDto filters = new UserFiltersDto("Jane", "420", 0, 5);
        List<UserDto> result = service.getFollowees(followerId, filters);

        assertThat(result).hasSize(1);
    }
}