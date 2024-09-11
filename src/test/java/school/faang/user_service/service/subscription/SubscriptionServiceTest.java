package school.faang.user_service.service.subscription;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.subscription.SubscriptionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionValidator subscriptionValidator;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserFilter userFilter;

    private final static long USER_ID_IS_ONE = 1L;
    private final static long USER_ID_IS_TWO = 2L;

    private User user;
    private List<User> users;
    private UserFilterDto userFilterNameDto;
    private List<UserFilter> userFilters;

    @Nested
    class NegativeTests {

        @Nested
        class FollowUserMethod {

            @Test
            @DisplayName("Ошибка валидации при подписке, если метод принимает 2 одинаковых id пользователей")
            void whenSubscriptionUsersIdAreEqualsThenThrowValidationException() {
                String exceptionMsg = "User can't subscribe to himself";

                doThrow(new ValidationException(exceptionMsg)).
                        when(userValidator).validateFirstUserIdAndSecondUserIdNotEquals(
                                USER_ID_IS_ONE,
                                USER_ID_IS_ONE,
                                exceptionMsg);

                assertThrows(ValidationException.class,
                        () -> subscriptionService.followUser(USER_ID_IS_ONE, USER_ID_IS_ONE),
                        exceptionMsg);
            }

            @Test
            @DisplayName("Ошибка валидации при подписке, если подписка уже существует")
            void whenSubscriptionAlreadyExistsThenThrowValidationException() {
                String exceptionMsg = "Already subscribed";

                doThrow(new ValidationException(exceptionMsg)).
                        when(subscriptionValidator).validateSubscriptionExistsAndEqualsShouldExistVariable(
                                USER_ID_IS_ONE,
                                USER_ID_IS_TWO,
                                true,
                                exceptionMsg);

                assertThrows(ValidationException.class,
                        () -> subscriptionService.followUser(USER_ID_IS_ONE, USER_ID_IS_TWO),
                        exceptionMsg);
            }
        }

        @Nested
        class UnfollowUserMethod {

            @Test
            @DisplayName("Ошибка валидации при отписке, если метод принимает 2 одинаковых id пользователей")
            void whenUnsubscribeUsersIdAreEqualsThenThrowValidationException() {
                String exceptionMsg = "User can't unsubscribe to himself";

                doThrow(new ValidationException(exceptionMsg)).
                        when(userValidator).validateFirstUserIdAndSecondUserIdNotEquals(
                                USER_ID_IS_ONE,
                                USER_ID_IS_ONE,
                                exceptionMsg);

                assertThrows(ValidationException.class,
                        () -> subscriptionService.unfollowUser(USER_ID_IS_ONE, USER_ID_IS_ONE),
                        exceptionMsg);
            }

            @Test
            @DisplayName("Ошибка валидации при отписке, если такой подписки не существует")
            void whenSubscriptionNotExistsThenThrowValidationException() {
                String exceptionMsg = "Already unsubscribed";

                doThrow(new ValidationException(exceptionMsg)).
                        when(subscriptionValidator).validateSubscriptionExistsAndEqualsShouldExistVariable(
                                USER_ID_IS_ONE,
                                USER_ID_IS_TWO,
                                false,
                                exceptionMsg);

                assertThrows(ValidationException.class,
                        () -> subscriptionService.unfollowUser(USER_ID_IS_ONE, USER_ID_IS_TWO),
                        exceptionMsg);
            }
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех если передали корректные значения в методе подписки")
        void whenCorrectValuesInFollowUserThenSuccess() {
            subscriptionService.followUser(USER_ID_IS_ONE, USER_ID_IS_TWO);

            verifyUsers(USER_ID_IS_ONE, USER_ID_IS_TWO);
            verifyIfFirstUserIdAndSecondUserIdNotEqualsOrElseThrowException(USER_ID_IS_ONE,
                    USER_ID_IS_TWO,
                    "User can't subscribe to himself");
            verifyIfSubscriptionExistsAndIfEqualsShouldExistThenThrowException(USER_ID_IS_ONE,
                    USER_ID_IS_TWO,
                    true,
                    "Already subscribed");
            verify(subscriptionRepository)
                    .followUser(USER_ID_IS_ONE, USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если передали корректные значения в методе отписки")
        void whenCorrectValuesInUnfollowUserThenSuccess() {
            subscriptionService.unfollowUser(USER_ID_IS_ONE, USER_ID_IS_TWO);

            verifyUsers(USER_ID_IS_ONE, USER_ID_IS_TWO);
            verifyIfFirstUserIdAndSecondUserIdNotEqualsOrElseThrowException(USER_ID_IS_ONE,
                    USER_ID_IS_TWO,
                    "User can't unsubscribe to himself");
            verifyIfSubscriptionExistsAndIfEqualsShouldExistThenThrowException(USER_ID_IS_ONE,
                    USER_ID_IS_TWO,
                    false,
                    "Already unsubscribed");
            verify(subscriptionRepository)
                    .unfollowUser(USER_ID_IS_ONE, USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если передали корректные значения в методе получения количества подписчиков")
        void whenCorrectValuesInGetFollowersCountThenSuccess() {
            subscriptionService.getFollowersCount(USER_ID_IS_TWO);

            verifyUser(USER_ID_IS_TWO);
            verify(subscriptionRepository)
                    .findFollowersAmountByFolloweeId(USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если передали корректные значения в методе получения количества подписок")
        void whenCorrectValuesInGetFollowingCountThenSuccess() {
            subscriptionService.getFollowingCount(USER_ID_IS_ONE);

            verifyUser(USER_ID_IS_ONE);
            verify(subscriptionRepository)
                    .findFolloweesAmountByFollowerId(USER_ID_IS_ONE);
        }

        @Nested
        class GetFollowersMethod {

            @BeforeEach
            void init() {
                customInitSubscriptionService();
            }

            @Test
            @DisplayName("Успех если передали положительное значение id и null фильтр в методе получения подписок")
            void whenCorrectValuesAndFilterIsNullInGetFollowersThenSuccess() {
                List<User> userList = List.of(User.builder()
                        .id(USER_ID_IS_TWO)
                        .build());

                when(subscriptionRepository.findByFollowerId(USER_ID_IS_TWO))
                        .thenReturn(userList.stream());

                subscriptionService.getFollowers(USER_ID_IS_TWO, null);

                verifyUser(USER_ID_IS_TWO);
                verify(subscriptionRepository)
                        .findByFollowerId(USER_ID_IS_TWO);
                verify(userMapper)
                        .toDtos(userList);
            }

            @Test
            @DisplayName("Успех если передано положительное значение id и фильтр по имени" +
                    " в методе просмотра своих подписчиков")
            void whenCorrectValuesAndFilterIsFilterNameInGetFollowersThenSuccess() {
                when(subscriptionRepository.findByFollowerId(USER_ID_IS_ONE))
                        .thenReturn(users.stream());
                when(userFilters.get(0).isApplicable(userFilterNameDto)).thenReturn(true);
                when(userFilters.get(0).apply(any(), eq(userFilterNameDto)))
                        .thenReturn(users.stream().filter(user ->
                                user.getUsername().equals(userFilterNameDto.getNamePattern())));

                subscriptionService.getFollowers(USER_ID_IS_ONE, userFilterNameDto);

                verifyUser(USER_ID_IS_ONE);
                verify(subscriptionRepository)
                        .findByFollowerId(USER_ID_IS_ONE);
                verify(userMapper)
                        .toDtos(List.of(user));
            }
        }

        @Nested
        class GetFollowingMethod {

            @BeforeEach
            void init() {
                customInitSubscriptionService();
            }

            @Test
            @DisplayName("Успех если передано положительное значение id и фильтр null " +
                    "в методе просмотра своих подписчиков")
            void whenCorrectValuesAndFilterIsNullInGetFollowingThenSuccess() {
                when(subscriptionRepository.findByFolloweeId(USER_ID_IS_TWO))
                        .thenReturn(users.stream());

                subscriptionService.getFollowing(USER_ID_IS_TWO, null);

                verifyUser(USER_ID_IS_TWO);
                verify(subscriptionRepository)
                        .findByFolloweeId(USER_ID_IS_TWO);
                verify(userMapper)
                        .toDtos(users);
            }

            @Test
            @DisplayName("Успех если передано положительное значение id и фильтр по имени" +
                    " в методе просмотра своих подписок")
            void whenCorrectValuesAndFilterIsFilterNameInGetFollowingThenSuccess() {
                when(subscriptionRepository.findByFolloweeId(USER_ID_IS_TWO))
                        .thenReturn(users.stream());
                when(userFilters.get(0).isApplicable(userFilterNameDto)).thenReturn(true);
                when(userFilters.get(0).apply(any(), eq(userFilterNameDto)))
                        .thenReturn(users.stream().filter(user ->
                                user.getUsername().equals(userFilterNameDto.getNamePattern())));

                subscriptionService.getFollowing(USER_ID_IS_TWO, userFilterNameDto);

                verifyUser(USER_ID_IS_TWO);
                verify(subscriptionRepository)
                        .findByFolloweeId(USER_ID_IS_TWO);
                verify(userMapper)
                        .toDtos(List.of(user));
            }
        }
    }

    private void verifyUser(long userId) {
        verify(userValidator)
                .validateUserIdIsPositiveAndNotNull(userId);
        verify(userValidator)
                .validateUserIsExisted(userId);
    }

    private void verifyUsers(long userId1, long userId2) {
        verifyUser(userId1);
        verifyUser(userId2);
    }

    private void verifyIfFirstUserIdAndSecondUserIdNotEqualsOrElseThrowException(long userId1,
                                                                                 long userId2,
                                                                                 String messageError) {
        verify(userValidator)
                .validateFirstUserIdAndSecondUserIdNotEquals(userId1,
                        userId2,
                        messageError);
    }

    private void verifyIfSubscriptionExistsAndIfEqualsShouldExistThenThrowException(long userId1,
                                                                                    long userId2,
                                                                                    boolean shouldExist,
                                                                                    String messageError) {
        verify(subscriptionValidator)
                .validateSubscriptionExistsAndEqualsShouldExistVariable(userId1,
                        userId2,
                        shouldExist,
                        messageError);
    }

    private void customInitSubscriptionService() {
        user = User.builder()
                .id(USER_ID_IS_ONE)
                .username("User1")
                .build();

        users = List.of(
                user,
                User.builder()
                        .id(USER_ID_IS_TWO)
                        .username("User2")
                        .build());

        userFilterNameDto = UserFilterDto.builder()
                .namePattern("User1")
                .build();

        userFilters = List.of(userFilter);
        subscriptionService = new SubscriptionService(subscriptionRepository,
                userMapper,
                userFilters,
                userValidator,
                subscriptionValidator);
    }
}