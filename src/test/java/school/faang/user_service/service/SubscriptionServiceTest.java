package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Test
    void testFollowUser_valid() {
        long followerId = 1;
        long followeeId = 2;
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void testFollowUser_followingExists() {
        long followerId = 1;
        long followeeId = 2;
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId)
        );
        assertEquals("Following already exists", exception.getMessage());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
    }


    @Test
    void testUnfollowUser_valid() {
        long followerId = 1;
        long followeeId = 2;
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void testUnfollowUser_followingNotExists() {
        long followerId = 1;
        long followeeId = 2;
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(followerId, followeeId)
        );
        assertEquals("Following does not exist", exception.getMessage());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void testGetFollowers_valid_noFilters() {
        UserMapper userMapper = new UserMapperImpl();
        ReflectionTestUtils.setField(subscriptionService, "userMapper", userMapper);

        long followeeId = 1;
        UserFilterDto filter = new UserFilterDto();
        User user1 = User.builder()
                .id(1L)
                .username("name1")
                .email("mail1.ru")
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("name2")
                .email("mail2.ru")
                .build();
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user1, user2));

        List<UserDto> resultList = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(userMapper.toDto(user1), resultList.get(0));
        assertEquals(userMapper.toDto(user2), resultList.get(1));
        assertEquals(2, resultList.size());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void testGetFollowers_valid_nameFilter() {
        UserMapper userMapper = new UserMapperImpl();
        ReflectionTestUtils.setField(subscriptionService, "userMapper", userMapper);

        long followeeId = 1;
        UserFilterDto filter = UserFilterDto.builder()
                .namePattern("1")
                .build();
        User user1 = User.builder()
                .id(1L)
                .username("name1")
                .email("mail1.ru")
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("name2")
                .email("mail2.ru")
                .build();
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user1, user2));

        List<UserDto> resultList = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(userMapper.toDto(user1), resultList.get(0));
        assertEquals(1, resultList.size());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void testGetFollowersCount() {
        long followeeId = 1;
        int expectedCount = 10;
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(expectedCount);

        int followersCount = subscriptionService.getFollowersCount(followeeId);

        assertEquals(expectedCount, followersCount);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void testGetFolloweesCount() {
        long followerId = 1;
        int expectedCount = 5;
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(expectedCount);

        int followeesCount = subscriptionService.getFolloweesCount(followerId);

        assertEquals(expectedCount, followeesCount);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
        Mockito.verifyNoMoreInteractions(subscriptionRepository);
    }
}