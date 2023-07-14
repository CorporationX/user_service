package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.SubscriptionMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @InjectMocks
    SubscriptionService subscriptionService;
    @Mock
    SubscriptionRepository subscriptionRepository;
    @Mock
    UserFilterDto userFilterDto;
    @Mock
    SubscriptionMapper subscriptionMapper;
    @Mock
    UserFilter userFilter;

    long followerId;
    long followeeId;

    @BeforeEach
    public void setUp() {
        followerId = 2;
        followeeId = 1;
    }

    @Test
    public void testAssertThrowsDataValidationExceptionForMethodFollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);

        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testMessageThrowForMethodFollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        try {
            subscriptionService.followUser(followerId, followeeId);
        } catch (DataValidationException e) {
            assertEquals("This subscription already exists", e.getMessage());
        }
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    public void testUnfollowUser() {
        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

//    @Test
//    public void testFollowers() {
//
//
//        List<User> userListFromRepository = List.of(
//                new User(1L, "user1", "about1", "email1", Collections.emptyList(), new Country(1L, "country1", Collections.emptyList()), "city1", "phone1", Collections.emptyList(), 0),
//                new User(2L, "user2", "about2", "email2", Collections.emptyList(), new Country("country2"), "city2", "phone2", Collections.emptyList(), 0)
//        );
//        List<UserDto> userDtoList = List.of(
//                new UserDto(1L, "user1", "email1"),
//                new UserDto(2L, "user2", "email2")
//        );
//
//        // Mock the repository call
//        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(userListFromRepository.toArray(new User[0])));
//
//        // Mock the user filter
//        when(userFilter.filterUsers(any(Stream.class), any(UserFilterDto.class))).thenReturn(userListFromRepository);
//
//        // Mock the mapper
//        when(subscriptionMapper.toListUserDto(userListFromRepository)).thenReturn(userDtoList);
//
//        // Call the method under test
//        List<UserDto> result = subscriptionService.getFollowers(followeeId, userFilterDto);
//
//        // Verify the results
//        assertEquals(userDtoList, result);
//
//        // Verify the interactions with mocked objects
//        verify(subscriptionRepository).findByFolloweeId(followeeId);
//        verify(userFilter).filterUsers(any(Stream.class), eq(userFilterDto));
//        verify(subscriptionMapper).toListUserDto(userListFromRepository);
//    }
}