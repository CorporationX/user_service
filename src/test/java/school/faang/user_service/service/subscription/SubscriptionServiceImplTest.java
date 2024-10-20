package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.impl.subscription.SubscriptionServiceImpl;
import school.faang.user_service.util.TestDataFactory;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceImplTest {

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private List<UserFilter> filters;

    @Spy
    private SubscriptionValidator validator;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    Long firstUserId;
    Long secondUserId;
    int amount;

    @BeforeEach
    void setUp() {
        firstUserId = 1L;
        secondUserId = 2L;
        amount = 2;
    }

    @Test
    public void usingMethodFollowUser() {
        subscriptionService.followUser(firstUserId, secondUserId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(firstUserId, secondUserId);
    }

    @Test
    public void givenWrongIdWhenGetFollowersThenThrowException() {
        firstUserId = -1L;

        var filter = TestDataFactory.createFilterDto();
        filter.setNamePattern("petr");

        assertThrows(DataValidationException.class, () ->
                subscriptionService.getFollowers(firstUserId, filter));
    }

    @Test
    public void givenValidIdWhenGetFollowersThenReturnList() {
        var user = TestDataFactory.createUser();
        var filter = TestDataFactory.createFilterDto();
        filter.setNamePattern("petr");

        Mockito.when(subscriptionRepository.findByFolloweeId(firstUserId))
                .thenReturn(Stream.of(user));
        List<UserDto> result = subscriptionService.getFollowers(firstUserId, filter);

        assertEquals(userMapper.toDto(List.of(user)), result);
    }

    @Test
    public void givenValidIdWhenGetFollowersCountThenReturnInt() {
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(firstUserId))
                .thenReturn(amount);
        int result = subscriptionService.getFollowersCount(firstUserId);

        assertEquals(amount, result);
    }

    @Test
    public void givenValidIdWhenGetFollowingWhenReturnList() {
        var user = TestDataFactory.createUser();
        var filter = TestDataFactory.createFilterDto();
        filter.setNamePattern("petr");

        Mockito.when(subscriptionRepository.findByFollowerId(firstUserId))
                .thenReturn(Stream.of(user));
        List<UserDto> result = subscriptionService.getFollowing(firstUserId, filter);

        assertEquals(userMapper.toDto(List.of(user)), result);
    }

    @Test
    public void givenValidIdWhenGetFollowingCountThenReturnInt() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(firstUserId))
                .thenReturn(amount);
        int result = subscriptionService.getFollowingCount(firstUserId);

        assertEquals(amount, result);
    }


}
