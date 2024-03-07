package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.follower.FollowerEventDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filter.UserFilterService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    private long validFollowerId;
    private long validFolloweeId;
    private long invalidFollowerId;
    private long invalidFolloweeId;
    private long sameUserId;

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserFilterService userFilter;
    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @BeforeEach
    void init() {
        validFollowerId = 5L;
        validFolloweeId = 2L;
        invalidFollowerId = -1L;
        invalidFolloweeId = -2L;
        sameUserId = 1L;
    }

    @Test
    void shouldDeleteSubscriptionWhenFollowerAndFolloweeIdsAreDifferent() {
        subscriptionService.unfollowUser(validFollowerId, validFolloweeId);

        verify(subscriptionRepository).unfollowUser(validFollowerId, validFolloweeId);
    }

    @Test
    void shouldCreateSubscriptionWhenNotExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(validFollowerId, validFolloweeId))
                .thenReturn(Boolean.FALSE);

        subscriptionService.followUser(validFollowerId, validFolloweeId);

        verify(subscriptionRepository).followUser(validFollowerId, validFolloweeId);
        verify(followerEventPublisher, times(1)).publish(any(FollowerEventDto.class));
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(validFollowerId, validFolloweeId))
                .thenReturn(Boolean.TRUE);

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(validFollowerId, validFolloweeId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserIdsAreInvalid() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(invalidFollowerId, 1L));
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(1L, invalidFolloweeId));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserIdsAreTheSame() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(sameUserId, sameUserId));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenFollowUserIdsAreInvalid() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(invalidFollowerId, 1L));
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(1L, invalidFolloweeId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenFollowUserIdsAreTheSame() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(sameUserId, sameUserId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldReturnFollowingCountWhenFollowerIdIsValid() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(validFollowerId))
                .thenReturn(5);

        int followingCount = subscriptionService.getFollowingCount(validFollowerId);

        assertEquals(5, followingCount);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(validFollowerId);
    }

    @Test
    void shouldThrowExceptionWhenFollowerIdIsInvalidInGetFollowingCount() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowingCount(invalidFollowerId));

        verify(subscriptionRepository, never()).findFolloweesAmountByFollowerId(anyLong());
    }

    @Test
    void shouldReturnFollowersCountWhenFolloweeIdIsValid() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(validFolloweeId))
                .thenReturn(5);

        int followersCount = subscriptionService.getFollowersCount(validFolloweeId);

        assertEquals(5, followersCount);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(validFolloweeId);
    }

    @Test
    void shouldThrowExceptionWhenFolloweeIdIsInvalidInGetFollowersCount() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowersCount(invalidFolloweeId));

        verify(subscriptionRepository, never()).findFollowersAmountByFolloweeId(anyLong());
    }

    @Test
    void shouldReturnFilteredFollowingsWhenGetValidIdAndFilter() {
        Stream<User> userStream = getTestUsers().stream();
        UserFilterDto filterDto = getTestFilterDto();
        Stream<UserDto> filteredDtosStream = getTestUserDtoStream();

        when(subscriptionRepository.findByFollowerId(validFollowerId))
                .thenReturn(userStream);
        when(userFilter.applyFilters(any(), eq(filterDto)))
                .thenReturn(filteredDtosStream);

        List<UserDto> following = subscriptionService.getFollowings(validFollowerId, filterDto);

        InOrder inOrder = inOrder(subscriptionRepository, userFilter);
        inOrder.verify(subscriptionRepository).findByFollowerId(validFollowerId);
        inOrder.verify(userFilter).applyFilters(any(), eq(filterDto));
        assertEquals(1, following.size());
    }

    @Test
    void shouldReturnFilteredFollowersWhenGetValidIdAndFilter() {
        Stream<User> userStream = getTestUsers().stream();
        UserFilterDto filterDto = getTestFilterDto();
        Stream<UserDto> filteredDtosStream = getTestUserDtoStream();

        when(subscriptionRepository.findByFolloweeId(validFollowerId))
                .thenReturn(userStream);
        when(userFilter.applyFilters(any(), eq(filterDto)))
                .thenReturn(filteredDtosStream);

        List<UserDto> following = subscriptionService.getFollowers(validFollowerId, filterDto);

        InOrder inOrder = inOrder(subscriptionRepository, userFilter);
        inOrder.verify(subscriptionRepository).findByFolloweeId(validFollowerId);
        inOrder.verify(userFilter).applyFilters(any(), eq(filterDto));
        assertEquals(1, following.size());
    }


    @Test
    void shouldThrowExceptionWhenFollowerIdIsInvalidInGetFollowings() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowings(invalidFollowerId, null));

        verify(subscriptionRepository, never()).findByFollowerId(anyLong());
        verify(userFilter, never()).applyFilters(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenFolloweeIdIsInvalidInGetFollowers() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowers(invalidFollowerId, null));

        verify(subscriptionRepository, never()).findByFolloweeId(anyLong());
        verify(userFilter, never()).applyFilters(any(), any());
    }

    private Stream<UserDto> getTestUserDtoStream() {
        return Stream.of(UserDto.builder().id(1L).username("user1").build());
    }

    private UserFilterDto getTestFilterDto() {
        return UserFilterDto.builder().id(1L).username("user1").build();
    }

    List<User> getTestUsers() {
        return new ArrayList<>(List.of(
                User.builder()
                        .id(1L)
                        .username("user1")
                        .build(),
                User.builder()
                        .id(2L)
                        .username("user2")
                        .build()
        ));
    }

}