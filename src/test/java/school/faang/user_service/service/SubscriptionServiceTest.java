package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.FollowerResponse;
import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private final UserFilter phoneFilter = new UserPhoneFilterTest();
    private final UserFilter nameFilter = new UserNameFilterTest();
    private final UserFilter minExperienceFilter = new UserMinExperienceFilterTest();
    private final UserFilter maxExperienceFilter = new UserMaxExperienceFilterTest();

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Spy
    private UserMapperImpl userMapper = new UserMapperImpl();
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        this.subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, List.of(
                phoneFilter, nameFilter, minExperienceFilter, maxExperienceFilter
        ));
    }

    @Test
    public void testFollowUserWithEqualsId() {
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(1L, 1L));
    }

    @Test
    public void testFollowUserWithExistSubscription() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);

        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(1L, 2L));
    }

    @Test
    public void testFollowUser() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(false);

        subscriptionService.followUser(1L, 2L);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(1L, 2L);
    }

    @Test
    public void testUnfollowUserWithEqualsId() {
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(1L, 1L));
    }

    @Test
    public void testUnfollowUserWithNotExistSubscription() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(false);

        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(1L, 2L));
    }

    @Test
    public void testUnfollowUser() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);

        subscriptionService.unfollowUser(1L, 2L);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(1L, 2L);
    }

    @Test
    public void testGetFollowing() {
        User appliedForFilter = User.builder().phone("+79999999999").username("Takewqa").experience(222).build();
        User notAppliedForFilter = User.builder().phone("+79999999997").username("Aqwekat").experience(222).build();

        Mockito.when(subscriptionRepository.findByFollowerId(1L))
                .thenReturn(Stream.of(appliedForFilter, notAppliedForFilter));

        List<FollowerResponse> actualFollowings = subscriptionService.getFollowing(1L,
                new UserFilterRequest(
                        "Takewqa",
                        "+79999999999",
                        2,
                        333
                ));

        Assertions.assertEquals(1, actualFollowings.size());
        Assertions.assertEquals("Takewqa", actualFollowings.get(0).username());
    }

    @Test
    public void testGetFollowingCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(1L)).thenReturn(5);

        Integer actualFollowingCount = subscriptionService.getFollowingCount(1L);

        Assertions.assertEquals(5, actualFollowingCount);
    }

    @Test
    public void testGetFollowers() {
        User appliedForFilter = User.builder().phone("+79999999999").username("Takewqa").experience(222).build();
        User notAppliedForFilter = User.builder().phone("+79999999997").username("Aqwekat").experience(222).build();

        Mockito.when(subscriptionRepository.findByFolloweeId(1L))
                .thenReturn(Stream.of(appliedForFilter, notAppliedForFilter));

        List<FollowerResponse> actualFollowers = subscriptionService.getFollowers(1L,
                new UserFilterRequest(
                        "Takewqa",
                        "+79999999999",
                        2,
                        333
                ));

        Assertions.assertEquals(1, actualFollowers.size());
        Assertions.assertEquals("Takewqa", actualFollowers.get(0).username());
    }

    @Test
    public void testGetFollowersCount() {
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(1L)).thenReturn(4);

        Integer actualFollowersCount = subscriptionService.getFollowersCount(1L);

        Assertions.assertEquals(4, actualFollowersCount);
    }
}
