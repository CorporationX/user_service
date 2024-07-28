package school.faang.user_service.service.service_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.service.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserFilter aboutFilter;

    @Mock
    private UserFilter nameFilter;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        List<UserFilter> userFilters = List.of(nameFilter, aboutFilter);
        subscriptionService = new SubscriptionService(subscriptionRepository, userFilters, userMapper);
    }

    //Positive test

    @Test
    @DisplayName("успешная подписка")
    public void followTest_successfulTest() throws DataFormatException {
        //arrange
        long followerId = 0L;
        long followeeId = 1L;

        //when
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        //then
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("успешная отписка")
    public void unfollowTest_successfulTest() throws DataFormatException {
        //arrange
        long followerId = 0L;
        long followeeId = 1L;

        //when
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);
        //then
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Пустой список подписчиков (подписок)")
    public void filterUser_emptyTest() throws DataFormatException {
        //arrange
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("John");

        Stream<User> users = Stream.of();

        //expect

        List<UserDto> userDtos = List.of();

        //assert
        Assertions.assertEquals(userDtos.hashCode(), subscriptionService.filterUsers(filter, users).hashCode());
    }

    @Test
    @DisplayName("Успех список подписчиков (подписок)")
    public void filterUser_successfulTest() throws DataFormatException {
        //arrange
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("John");
        filter.setAboutPattern("Smile");

        User fuser = new User();
        fuser.setId(0);
        fuser.setUsername("John");
        fuser.setAboutMe("Smile");
        User suser = new User();
        suser.setId(1);
        suser.setUsername("John");
        suser.setAboutMe("Cloud");
        User tuser = new User();
        tuser.setId(2);
        tuser.setUsername("Anna");

        Stream<User> users = Stream.of(fuser, suser, tuser);

        //then
        when(nameFilter.isApplicable(filter)).thenReturn(true);
        when(aboutFilter.isApplicable(filter)).thenReturn(true);

        when(nameFilter.apply(users, filter)).thenReturn(Stream.of(fuser));
        when(aboutFilter.apply(users, filter)).thenReturn(Stream.of());

        //expect
        UserDto fuserDto = new UserDto(0L, "John", null, null);
        List<UserDto> userDtos = List.of(fuserDto);

        //assert
        Assertions.assertEquals(userDtos.hashCode(), subscriptionService.filterUsers(filter, users).hashCode());
    }


    //Negative tests
    @Test
    @DisplayName("подписки (отписки): одинаковые id")
    public void followUser_throwIDTest() {
        Assertions.assertThrows(DataFormatException.class,
                () -> subscriptionService.validateUsersSubs(0L, 0L)
        );
    }

    @Test
    @DisplayName("подписки (отписки): нет user в бд")
    public void followUser_exitUserTest() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(0L, 1L)).thenReturn(false);
        Assertions.assertThrows(DataFormatException.class,
                () -> subscriptionService.validateUsersSubs(0L, 1L)
        );
    }

    @Test
    @DisplayName("Подписчики, нет юзера в бд")
    public void validate_exitUserTest() {
        when(subscriptionRepository.existsById(0L)).thenReturn(false);
        Assertions.assertThrows(DataFormatException.class,
                () -> subscriptionService.validateUser(0L)
        );
    }

    @Test
    @DisplayName("filter users nullable")
    public void filterUsers_nullTest() {
        UserFilterDto filter = new UserFilterDto();

        Assertions.assertThrows(NullPointerException.class, () -> subscriptionService.filterUsers(filter, null));
    }
}