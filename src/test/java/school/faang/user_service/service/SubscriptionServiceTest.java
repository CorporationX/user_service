package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exceptions.ExceptionMessage.REPEATED_SUBSCRIPTION_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepo;

    @InjectMocks
    private SubscriptionService subscriptionService;

    ArgumentCaptor<Long> followerArgumentCaptor;
    ArgumentCaptor<Long> followeeArgumentCaptor;

    Long followerId;
    Long followeeId;

    @BeforeEach
    void init() {
        followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        followerId = 1L;
        followeeId = 2L;
    }

    @Test
    void followNewUserTest() {
        //before
        when(subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);


        //when
        subscriptionService.followUser(followerId, followeeId);

        //then
        verify(subscriptionRepo, times(1)).existsByFollowerIdAndFolloweeId(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());

        verify(subscriptionRepo, times(1)).followUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @Test
    void followFollowedUserTest() {
        //before
        when(subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);


        //when
        var actualException = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId));

        //then
        assertEquals(REPEATED_SUBSCRIPTION_EXCEPTION.getMessage(), actualException.getMessage());
        verify(subscriptionRepo, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepo, times(0)).followUser(followerId, followeeId);
    }

    @Test
    void unfollowUserTest() {
        //when
        subscriptionService.unfollowUser(followerId, followeeId);

        //then
        verify(subscriptionRepo, times(1)).unfollowUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
        assertEquals(followerId, followerArgumentCaptor.getValue());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
    }

    @Test
    void getFollowersTest() {
        //before
        Stream<User> allFollowers = new ArrayList<User>().stream();
        when(subscriptionRepo.findByFolloweeId(followeeId)).thenReturn(allFollowers);

        //when
        var actualFollowers = subscriptionService.getFollowers(followeeId, new UserFilterDto());

        //then
        verify(subscriptionRepo, times(1)).findByFolloweeId(followeeArgumentCaptor.capture());
        assertEquals(followeeId, followeeArgumentCaptor.getValue());
        assertEquals(new ArrayList<UserDto>(), actualFollowers);
    }


    @Test
    void filterUsersTest() {
        //before
        var filter = new UserFilterDto();
        filter.setCityPattern("Moscow");

        var expectedFollowers = new ArrayList<UserDto>();
        expectedFollowers.add(new UserDto(1L, "nadir", "nadir@gmail.com"));
        expectedFollowers.add(new UserDto(2L, "cianid", "cianid@gmail.com"));

        var allFollowers = new ArrayList<User>();

        var nadir = new User();
        nadir.setId(1L);
        nadir.setUsername("nadir");
        nadir.setEmail("nadir@gmail.com");
        nadir.setCity("Moscow");
        allFollowers.add(nadir);

        var cianid = new User();
        cianid.setId(2L);
        cianid.setUsername("cianid");
        cianid.setEmail("cianid@gmail.com");
        cianid.setCity("Moscow");
        allFollowers.add(cianid);

        var zenith = new User();
        zenith.setId(3L);
        zenith.setUsername("zenith");
        zenith.setEmail("zenith@gmail.com");
        zenith.setCity("Kirov");
        allFollowers.add(zenith);


        //when
        var actualFollowers = subscriptionService.filterUsers(allFollowers.stream(), filter);

        //then
        assertEquals(expectedFollowers, actualFollowers);
    }
}