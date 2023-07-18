package school.faang.user_service.service.SubscriptionTest;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.SubscriberFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository repository;
    @Spy
    private SubscriberFilter filter;
    @Spy
    private UserMapper mapper;
    @InjectMocks
    private SubscriptionService service;
    private UserFilterDto filterDto;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filterDto = new UserFilterDto();
    }

    @Test
    void FollowsSuccessfullyTest() {
        long followerId = 1;
        long followeeId = 2;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        service.followUser(followerId, followeeId);
        verify(repository).followUser(followerId, followeeId);
    }

    @Test
    void alreadyFollowExceptionTest() {
        long followerId = 1;
        long followeeId = 2;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        Assert.assertThrows(DataValidationException.class,
                () -> service.followUser(followerId, followeeId));
    }

    @Test
    void unfollowTest() {
        long followerId = 1;
        long followeeId = 2;

        repository.unfollowUser(followerId, followeeId);
        verify(repository).unfollowUser(followerId, followeeId);
    }

    @Test
    void GetByFolloweeTest() {
        var res = service.getFollowers(1, filterDto);
        verify(repository).findByFolloweeId(Mockito.anyLong());
    }

    @Test
    void GetFollowersCountTest() {
        var res = service.getFollowers(1, filterDto);
        verify(repository).findByFolloweeId(Mockito.anyLong());
    }
}
