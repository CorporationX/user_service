package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionService subscriptionService;

    @Spy
    UserMapper userMapper;
    @Mock
    UserFilterDto userFilterDto;
    @Mock
    List<UserFilter> userFilters;



    @Test
    void testUnfollowSuccessfully() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(123, 321)).thenReturn(true).thenReturn(false);

        subscriptionService.unfollowUser(123, 321);

        verify(subscriptionRepository, times(1)).unfollowUser(123, 321);
    }

    @Test
    void testAlreadyFollowedThrowsDataValidateException() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(111, 222))
                .thenReturn(true);

        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(111, 222));
    }

    @Test
    void testFollowedSuccessfully() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(22, 23))
                .thenReturn(false);

        subscriptionService.followUser(22, 23);
        verify(subscriptionRepository, times(1)).followUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testGetFollowers() {
        User user = mock(User.class);
        UserDto userDto = mock(UserDto.class);
        Stream<User> userStream = Stream.of(user);

        when(subscriptionRepository.findByFolloweeId(111)).thenReturn(userStream);
        when(userMapper.toDto(user)).thenReturn(userDto);

        subscriptionService.getFollowers(111, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(111);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetFollowersCount(){
        when(subscriptionRepository.existsById(1122L)).thenReturn(true);
        subscriptionService.getFollowersCount(1122);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(Mockito.anyLong());
    }

    @Test
    void testGetFolloweesCount(){
        when(subscriptionRepository.existsById(2211L)).thenReturn(true);
        subscriptionService.getFolloweesCount(2211);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(Mockito.anyLong());
    }
}
