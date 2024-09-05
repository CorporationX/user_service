package school.faang.user_service.service.subscription;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.FollowerRepository;
import school.faang.user_service.service.publisher.FollowerEventPublisher;
import school.faang.user_service.service.user.filters.UserFilter;
import school.faang.user_service.service.user.filters.UserNameFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionValidator validator;

    @Spy
    private UserMapperImpl mapper;

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    List<UserFilter<UserFilterDto, User>> userFilters;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User user;
    private UserFilterDto filters;

    @BeforeEach
    void setUp() {
        UserFilter<UserFilterDto, User> usernameFilter = Mockito.mock(UserNameFilter.class);
        userFilters = List.of(usernameFilter);
        user = User.builder()
                .id(2).username("vlad").email("vlad@email.ru")
                .phone("+79659363636").aboutMe("About me")
                .country(Country.builder().title("Russia").build())
                .city("Moscow").contacts(List.of(Contact.builder().contact("contact").build()))
                .skills(List.of(Skill.builder().title("My skill 2").build()))
                .experience(5)
                .build();
        filters = new UserFilterDto(
                "vlad",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                5
        );
        subscriptionService = new SubscriptionService(subscriptionRepository, mapper, validator, userFilters, followerEventPublisher);
    }

    @Test
    void followUserSuccessfully() {
        Mockito.doNothing().when(followerEventPublisher).publish(Mockito.any(FollowerEvent.class));
        subscriptionService.followUser(1, 2);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(1, 2);
        Mockito.verify(followerEventPublisher, Mockito.times(1)).publish(Mockito.any(FollowerEvent.class));
    }

    @Test
    void followUserAlreadyFollowed() {
        Mockito.doThrow(new DataValidationException("Подписка уже существует")).when(validator).validateExistingSubscription(1, 2);
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(1, 2));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).followUser(1, 2);
    }

    @Test
    void unfollowUser() {
        subscriptionService.unfollowUser(1, 2);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(1, 2);
    }

    @Test
    void getFollowers() {
        Mockito.when(subscriptionRepository.findByFolloweeId(1)).thenReturn(Stream.<User>builder()
                .add(user).build());
        Mockito.when(userFilters.get(0).isApplicable(Mockito.any())).thenReturn(true);
        Mockito.when(userFilters.get(0).apply(Mockito.any(), Mockito.any())).thenReturn(Stream.<User>builder().add(user).build());
        Assertions.assertEquals(subscriptionService.getFollowers(1, filters), List.of(mapper.toDto(user)));
    }

    @Test
    void getFollowersCount() {
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(1)).thenReturn(1);
        Assertions.assertEquals(1, subscriptionService.getFollowersCount(1));
    }

    @Test
    void getFollowing() {
        Mockito.when(subscriptionRepository.findByFollowerId(1)).thenReturn(Stream.<User>builder()
                .add(user).build());
        Mockito.when(userFilters.get(0).isApplicable(Mockito.any())).thenReturn(true);
        Mockito.when(userFilters.get(0).apply(Mockito.any(), Mockito.any())).thenReturn(Stream.<User>builder().add(user).build());
        Assertions.assertEquals(subscriptionService.getFollowing(1, filters), List.of(mapper.toDto(user)));
    }

    @Test
    void getFollowingCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(1)).thenReturn(0);
        Assertions.assertEquals(0, subscriptionService.getFollowingCount(1));
    }
}