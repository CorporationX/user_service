package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserAboutFilter;
import school.faang.user_service.service.user.filter.UserCityFilter;
import school.faang.user_service.service.user.filter.UserContactFilter;
import school.faang.user_service.service.user.filter.UserCountryFilter;
import school.faang.user_service.service.user.filter.UserEmailFilter;
import school.faang.user_service.service.user.filter.UserExperienceFilter;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.filter.UserNameFilter;
import school.faang.user_service.service.user.filter.UserPhoneFilter;
import school.faang.user_service.service.user.filter.UserSkillFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.ExceptionMessage.REPEATED_SUBSCRIPTION_EXCEPTION;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    SubscriptionRepository subscriptionRepo;

    @Mock
    UserMapper userMapper;

    @Spy
    @InjectMocks
    SubscriptionService subscriptionService;

    ArgumentCaptor<Long> followerArgumentCaptor;
    ArgumentCaptor<Long> followeeArgumentCaptor;

    Long followerId;
    Long followeeId;
    List<UserFilter> filters;
    UserFilterDto filterDto;

    @BeforeEach
    void init() {
        followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        followerId = 1L;
        followeeId = 2L;

        filterDto = new UserFilterDto();

        var userNameFilter = mock(UserNameFilter.class);
        var userAboutFilter = mock(UserAboutFilter.class);
        var userEmailFilter = mock(UserEmailFilter.class);
        var userContactFilter = mock(UserContactFilter.class);
        var userCountryFilter = mock(UserCountryFilter.class);
        var userCityFilter = mock(UserCityFilter.class);
        var userPhoneFilter = mock(UserPhoneFilter.class);
        var userSkillFilter = mock(UserSkillFilter.class);
        var userExperienceFilter = mock(UserExperienceFilter.class);

        filters = List.of(userNameFilter, userAboutFilter, userEmailFilter, userContactFilter,
                userCountryFilter, userCityFilter, userPhoneFilter, userSkillFilter, userExperienceFilter);

        subscriptionService.setFilters(filters);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should follow new user")
        @Test
        void shouldFollowNewUser() {
            when(subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);


            subscriptionService.followUser(followerId, followeeId);


            verify(subscriptionRepo).existsByFollowerIdAndFolloweeId(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());

            verify(subscriptionRepo).followUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
        }

        @DisplayName("should unfollow user")
        @Test
        void shouldUnfollowUser() {
            subscriptionService.unfollowUser(followerId, followeeId);


            verify(subscriptionRepo).unfollowUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
        }

        @DisplayName("should return followers")
        @Test
        void shouldReturnAllFollowers() {
            var allFollowers = Stream.<User>of();

            when(subscriptionRepo.findByFolloweeId(followeeId)).thenReturn(allFollowers);
            filters.forEach(filter -> {
                when(filter.isApplicable(filterDto)).thenReturn(true);
                when(filter.apply(allFollowers, filterDto)).thenReturn(Stream.of());
            });


            var actualFollowers = subscriptionService.getFollowers(followeeId, filterDto);


            verify(subscriptionRepo).findByFolloweeId(followeeArgumentCaptor.capture());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
            assertEquals(List.of(), actualFollowers);
        }

        @DisplayName("should call subscriptionRepo.findFollowersAmountByFolloweeId()")
        @Test
        void shouldReturnFollowersCount() {
            subscriptionService.getFollowersCount(followeeId);


            verify(subscriptionRepo).findFollowersAmountByFolloweeId(followeeArgumentCaptor.capture());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
        }

        @DisplayName("should return subscriptions")
        @Test
        void shouldReturnAllSubscriptions() {
            when(subscriptionRepo.findByFollowerId(followerId)).thenReturn(Stream.of());


            var actualFollowing = subscriptionService.getFollowing(followerId, new UserFilterDto());


            verify(subscriptionRepo).findByFollowerId(followerArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
            assertEquals(new ArrayList<UserDto>(), actualFollowing);
        }

        @DisplayName("should call subscriptionRepo.findFolloweesAmountByFollowerId()")
        @Test
        void shouldReturnSubscriptionCount() {
            subscriptionService.getFollowingCount(followerId);


            verify(subscriptionRepo).findFolloweesAmountByFollowerId(followerArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when such followee already exists")
        @Test
        void shouldThrowExceptionWhenSuchFolloweeExists() {
            when(subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);


            var actualException = assertThrows(DataValidationException.class,
                    () -> subscriptionService.followUser(followerId, followeeId));


            assertEquals(REPEATED_SUBSCRIPTION_EXCEPTION.getMessage(), actualException.getMessage());
            verify(subscriptionRepo).existsByFollowerIdAndFolloweeId(followerId, followeeId);
            verify(subscriptionRepo, times(0)).followUser(followerId, followeeId);
        }
    }
}