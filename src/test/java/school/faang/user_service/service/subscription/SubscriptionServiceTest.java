package school.faang.user_service.service.subscription;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private List<UserFilter> userFilters;
    @Mock
    private UserValidator userValidator;
    @Spy
    private UserNameFilter userNameFilter;

    private final long LONG_POSITIVE_USER_ID_IS_ONE = 1L;
    private final long LONG_POSITIVE_USER_ID_IS_TWO = 2L;

    private final int INT_POSITIVE_ONE = 1;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации при подписке если метод принимает 2 одинаковых числа")
        void When_SubscriptionUsersIdAreEquals_Then_ThrowValidationException() {
            assertThrows(ValidationException.class,
                    () -> subscriptionService.followUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_ONE),
                    "User can't subscribe to himself");
        }

        @Test
        @DisplayName("Ошибка валидации при подписке если подписка уже существует")
        void When_SubscriptionAlreadyExists_Then_ThrowValidationException() {
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO))
                    .thenReturn(Boolean.TRUE);

            assertThrows(ValidationException.class,
                    () -> subscriptionService.followUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO),
                    "Already subscribed");
        }

        @Test
        @DisplayName("Ошибка валидации при отписке если метод принимает 2 одинаковых числа")
        void When_UnsubscribeUsersIdAreEquals_Then_ThrowValidationException() {
            assertThrows(ValidationException.class,
                    () -> subscriptionService.unfollowUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_ONE),
                    "User can't unsubscribe to himself");
        }

        @Test
        @DisplayName("Ошибка валидации при отписке если такой подписки не существует")
        void testUnfollowUserIfSubscriptionNotExists() {
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO))
                    .thenReturn(Boolean.FALSE);

            assertThrows(ValidationException.class,
                    () -> subscriptionService.unfollowUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO),
                    "Already unsubscribed");
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех если корректные значения в методе подписки")
        void When_CorrectValuesInFollowUser_Then_Success() {
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO))
                    .thenReturn(Boolean.FALSE);

            subscriptionService.followUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO);

            verify(subscriptionRepository, times(1))
                    .followUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если корректные значения в методе отписки")
        void When_CorrectValuesInUnfollowUser_Then_Success() {
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO))
                    .thenReturn(Boolean.TRUE);

            subscriptionService.unfollowUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO);

            verify(subscriptionRepository, times(1))
                    .unfollowUser(LONG_POSITIVE_USER_ID_IS_ONE, LONG_POSITIVE_USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если корректные значения в методе получения количества подписчиков")
        void When_CorrectValuesInGetFollowersCount_Then_Success() {
            subscriptionService.getFollowersCount(LONG_POSITIVE_USER_ID_IS_TWO);

            verify(subscriptionRepository, times(1))
                    .findFollowersAmountByFolloweeId(LONG_POSITIVE_USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если корректные значения в методе получения количества подписок")
        void When_CorrectValuesInGetFollowingCount_Then_Success() {
            subscriptionService.getFollowingCount(LONG_POSITIVE_USER_ID_IS_ONE);

            verify(subscriptionRepository, times(1))
                    .findFolloweesAmountByFollowerId(LONG_POSITIVE_USER_ID_IS_ONE);
        }

        @Test
        @DisplayName("Успех если положительное значение id и фильтр null в методе получения подписок")
        void When_CorrectValuesAndFilterIsNullInGetFollowers_Then_Success() {
            UserFilterDto userFilterDto = null;

            subscriptionService.getFollowers(LONG_POSITIVE_USER_ID_IS_TWO, userFilterDto);

            verify(subscriptionRepository, times(1))
                    .findByFollowerId(LONG_POSITIVE_USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если передано положительное значение id и фильтр null " +
                "в методе просмотра своих подписчиков")
        void When_CorrectValuesAndFilterIsNullInGetFollowing_Then_Success() {
            UserFilterDto userFilterDto = null;

            subscriptionService.getFollowing(LONG_POSITIVE_USER_ID_IS_TWO, userFilterDto);

            verify(subscriptionRepository, times(1))
                    .findByFolloweeId(LONG_POSITIVE_USER_ID_IS_TWO);
        }

        @Test
        @DisplayName("Успех если передано положительное значение id и фильтр по имени" +
                " в методе просмотра своих подписок")
        void When_CorrectValuesAndFilterIsFilterNameInGetFollowing_Then_Success() {
            userFilters = List.of(userNameFilter);
            userMapper = Mappers.getMapper(UserMapper.class);
            subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilters, userValidator);

            UserFilterDto userFilterNameDto = new UserFilterDto();
            userFilterNameDto.setNamePattern("es");

            User user = new User();
            user.setUsername("user");

            User testUser = new User();
            testUser.setUsername("test");

            List<User> users = List.of(user, testUser);

            when(subscriptionRepository.findByFolloweeId(LONG_POSITIVE_USER_ID_IS_ONE)).thenReturn(users.stream());

            List<UserDto> result = subscriptionService.getFollowing(LONG_POSITIVE_USER_ID_IS_ONE, userFilterNameDto);

            assertEquals(INT_POSITIVE_ONE, result.size());
            assertEquals(testUser.getUsername(), result.get(0).getUsername());

            verify(subscriptionRepository, times(1)).findByFolloweeId(LONG_POSITIVE_USER_ID_IS_ONE);
        }


        @Test
        @DisplayName("Успех если передано положительное значение id и фильтр по имени" +
                " в методе просмотра своих подписчиков")
        void When_CorrectValuesAndFilterIsFilterNameInGetFollowers_Then_Success() {
            userFilters = List.of(userNameFilter);
            userMapper = Mappers.getMapper(UserMapper.class);
            subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilters, userValidator);

            UserFilterDto userFilterNameDto = new UserFilterDto();
            userFilterNameDto.setNamePattern("es");

            User user = new User();
            user.setUsername("user");

            User testUser = new User();
            testUser.setUsername("test");

            List<User> users = List.of(user, testUser);

            when(subscriptionRepository.findByFollowerId(LONG_POSITIVE_USER_ID_IS_ONE)).thenReturn(users.stream());

            List<UserDto> result = subscriptionService.getFollowers(LONG_POSITIVE_USER_ID_IS_ONE, userFilterNameDto);

            assertEquals(INT_POSITIVE_ONE, result.size());
            assertEquals(testUser.getUsername(), result.get(0).getUsername());

            verify(subscriptionRepository, times(1)).findByFollowerId(LONG_POSITIVE_USER_ID_IS_ONE);
        }
    }
}