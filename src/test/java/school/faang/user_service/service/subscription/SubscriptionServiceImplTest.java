package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilterService;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserFilterService userFilterService;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionServiceImpl;

    @Test
    void shouldCreateSubscriptionWhenNotExists() {
        SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto(1L, 2L);

        doNothing().when(subscriptionValidator).validateSubscriptionExistence(subscriptionRequestDto);
        doNothing().when(subscriptionValidator).validateFollowerAndFolloweeIds(subscriptionRequestDto);
        doNothing().when(subscriptionRepository).followUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());

        subscriptionServiceImpl.followUser(subscriptionRequestDto);

        verify(subscriptionValidator, times(1)).validateSubscriptionExistence(subscriptionRequestDto);
        verify(subscriptionRepository, times(1)).followUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());
    }

    @Test
    void shouldThrowDataValidationExceptionWhenExists() {
        SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto(1L, 2L);

        doThrow(DataValidationException.class).when(subscriptionValidator).validateSubscriptionExistence(subscriptionRequestDto);

        assertThrows(DataValidationException.class, () -> subscriptionServiceImpl.followUser(subscriptionRequestDto));
    }

    @Test
    void shouldUnfollowUser() {
        SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto(1L, 2L);

        doNothing().when(subscriptionValidator).validateFollowerAndFolloweeIds(subscriptionRequestDto);
        doNothing().when(subscriptionRepository).unfollowUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());

        subscriptionServiceImpl.unfollowUser(subscriptionRequestDto);

        verify(subscriptionValidator, times(1)).validateFollowerAndFolloweeIds(subscriptionRequestDto);
        verify(subscriptionRepository, times(1)).unfollowUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserHasSameIds() {
        SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto(1L, 1L);

        doThrow(DataValidationException.class).when(subscriptionValidator).validateFollowerAndFolloweeIds(subscriptionRequestDto);

        assertThrows(DataValidationException.class, () -> subscriptionServiceImpl.unfollowUser(subscriptionRequestDto));
    }

    @Test
    void shouldReturnFollowers() {
        Stream<User> users = getUsers().stream();
        UserFilterDto userFilterDto = new UserFilterDto();

        when(subscriptionRepository.findByFolloweeId(anyLong())).thenReturn(users);
        when(userFilterService.applyFilters(users, userFilterDto)).thenReturn(users);

        assertEquals(subscriptionServiceImpl.getFollowers(anyLong(), userFilterDto), getUsers().stream().map(userMapper::toDto).toList());

        verify(subscriptionRepository, times(1)).findByFolloweeId(anyLong());
        verify(userFilterService, times(0)).applyFilters(getUsers().stream(), userFilterDto);
    }

    @Test
    void shouldReturnFollowings() {
        Stream<User> users = getUsers().stream();
        UserFilterDto userFilterDto = new UserFilterDto();

        when(subscriptionRepository.findByFollowerId(anyLong())).thenReturn(users);
        when(userFilterService.applyFilters(users, userFilterDto)).thenReturn(users);

        assertEquals(subscriptionServiceImpl.getFollowings(anyLong(), userFilterDto), getUsers().stream().map(userMapper::toDto).toList());

        verify(subscriptionRepository, times(1)).findByFollowerId(anyLong());
        verify(userFilterService, times(0)).applyFilters(getUsers().stream(), userFilterDto);
    }

    @Test
    void shouldReturnFollowersCount() {
        int followersCount = 10;
        long followeeId = 1L;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(followersCount);

        assertEquals(subscriptionServiceImpl.getFollowersCount(followeeId), followersCount);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    void shouldReturnFollowingsCount() {
        int followeesCount = 10;
        long followerId = 1L;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(followeesCount);

        assertEquals(subscriptionServiceImpl.getFollowingsCount(followerId), followeesCount);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }

    private List<User> getUsers() {
        return new ArrayList<>(List.of(
                User.builder()
                        .id(1L)
                        .username("test1")
                        .build(),
                User.builder()
                        .id(2L)
                        .username("test2")
                        .build(),
                User.builder()
                        .id(3L)
                        .username("test3")
                        .build(),
                User.builder()
                        .id(3L)
                        .username("test4")
                        .build()
        ));
    }
}
