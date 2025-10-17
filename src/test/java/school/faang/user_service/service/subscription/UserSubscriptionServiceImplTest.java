package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSubscriptionServiceImplTest {
    @InjectMocks
    private UserSubscriptionServiceImpl subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Test
    void followUserShouldCallRepositoryWithCorrectIds() {
        long followerId = 1L;
        long followeeId = 2L;

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    void followUserShouldThrowWhenFollowerAlreadyFollowedToFollowee() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        assertThrows(ForbiddenException.class, () ->
                subscriptionService.followUser(followerId, followeeId)
        );
    }

    @Test
    void unfollowUserShouldCallRepositoryWithCorrectIds() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    void unfollowUserShouldThrowWhenFollowerNotSubscribedToFollowee() {
        long followerId = 1L;
        long followeeId = 2L;

        assertThrows(ForbiddenException.class, () ->
                subscriptionService.unfollowUser(followerId, followeeId));
    }

    @Test
    void getFollowersCountShouldReturnCountOfFollowersFromRepository() {
        long followeeId = 1L;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(1000);

        CountResponse followersCount = subscriptionService.getFollowersCount(followeeId);

        assertEquals(new CountResponse(1000L), followersCount);
    }

    @Test
    void getFolloweesCountShouldReturnCountOfFollowees() {
        long followerId = 1L;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(15);

        CountResponse followeesCount = subscriptionService.getFolloweesCount(followerId);

        assertEquals(new CountResponse(15L), followeesCount);
    }

    @Test
    void getFollowersShouldReturnFilteredAndMappedFollowers() {
        long followeeId = 203L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "Cillian", "124555999", 40, 70);

        User firstFollower = User.builder()
                .id(674L)
                .username("Johnnny112")
                .phone("12434343999")
                .experience(55)
                .build();
        User secondFollower = User.builder()
                .id(596L)
                .username("cILLIan77")
                .phone("2287731124555999")
                .experience(65)
                .build();
        User thirdFollower = User.builder()
                .id(395L)
                .username("mr.Cillian007")
                .phone("4761245559998127")
                .experience(57)
                .build();

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(firstFollower, secondFollower, thirdFollower));

        List<UserDto> exceptedFollowers = List.of(
                userMapper.toUserDto(secondFollower),
                userMapper.toUserDto(thirdFollower)
        );

        List<UserDto> actualFollowers = subscriptionService.getFollowers(followeeId, userFiltersDto);

        assertEquals(exceptedFollowers, actualFollowers);
    }

    @Test
    void getFolloweesShouldReturnFilteredAndMappedFollowees() {
        long followerId = 1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "Helen", "100200300", 50, 60);

        User firstFollowee = User.builder()
                .id(2L)
                .username("helenn070")
                .phone("3310020030069")
                .experience(55)
                .build();
        User secondFollowee = User.builder()
                .id(3L)
                .username("helen")
                .phone("78910020030065")
                .experience(50)
                .build();
        User thirdFollowee = User.builder()
                .id(4L)
                .username("helen11")
                .phone("121343656")
                .experience(65)
                .build();

        when(subscriptionRepository.findByFollowerId(followerId))
                .thenReturn(Stream.of(firstFollowee, secondFollowee, thirdFollowee));

        List<UserDto> exceptedFollowees = List.of(
                userMapper.toUserDto(firstFollowee),
                userMapper.toUserDto(secondFollowee)
        );

        List<UserDto> actualFollowees = subscriptionService.getFollowees(followerId, userFiltersDto);

        assertEquals(exceptedFollowees, actualFollowees);
    }
}
