package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private long validFollowerId;
    private long validFolloweeId;

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;

    @BeforeEach
    void init() {
        validFollowerId = 5L;
        validFolloweeId = 2L;
    }

    @Test
    void shouldReturnTrueWhenUnfollowIsSuccessful() {
        Map.Entry<String, Boolean> result = subscriptionController.unfollowUser(validFollowerId, validFolloweeId);

        assertEquals(
                Map.entry("isUnfollowed", true),
                result);

        verify(subscriptionService).unfollowUser(validFollowerId, validFolloweeId);
    }

    @Test
    void shouldReturnTrueWhenFollowIsSuccessful() {
        Map.Entry<String, Boolean> result = subscriptionController.followUser(validFollowerId, validFolloweeId);

        assertEquals(
                Map.entry("isFollowed", true),
                result);

        verify(subscriptionService).followUser(validFollowerId, validFolloweeId);
    }

    @Test
    void shouldReturnFollowingCountWhenFollowerIdIsValid() {
        when(subscriptionService.getFollowingCount(validFollowerId))
                .thenReturn(5);

        Map.Entry<String, Integer> result = subscriptionController.getFollowingCount(validFollowerId);

        assertEquals(
                Map.entry("followingCount", 5),
                result);

        verify(subscriptionService).getFollowingCount(validFollowerId);
    }

    @Test
    void shouldReturnFollowersCountWhenFolloweeIdIsValid() {
        when(subscriptionService.getFollowersCount(validFolloweeId))
                .thenReturn(5);

        Map.Entry<String, Integer> result = subscriptionController.getFollowersCount(validFolloweeId);

        assertEquals(
                Map.entry("followersCount", 5),
                result);

        verify(subscriptionService).getFollowersCount(validFolloweeId);
    }

    @Test
    void shouldReturnFollowingsWhenFollowerIdAndFilterAreValid() {
        List<UserDto> testUsers = getTestUsers();
        UserFilterDto testFilterDto = getTestFilterDto();
        when(subscriptionService.getFollowings(validFollowerId, testFilterDto))
                .thenReturn(testUsers);

        Map.Entry<String, List<UserDto>> result = subscriptionController.getFollowings(validFollowerId, testFilterDto);

        assertEquals(
                Map.entry("following", testUsers),
                result);

        verify(subscriptionService).getFollowings(validFollowerId, testFilterDto);
    }

    @Test
    void shouldReturnFollowersWhenFolloweeIdAndFilterAreValid() {
        List<UserDto> testUsers = getTestUsers();
        UserFilterDto testFilterDto = getTestFilterDto();
        when(subscriptionService.getFollowers(validFolloweeId, testFilterDto))
                .thenReturn(testUsers);

        Map.Entry<String, List<UserDto>> result = subscriptionController.getFollowers(validFolloweeId, testFilterDto);

        assertEquals(
                Map.entry("followers", testUsers),
                result);

        verify(subscriptionService).getFollowers(validFolloweeId, testFilterDto);
    }

    private UserFilterDto getTestFilterDto() {
        return UserFilterDto.builder().id(1L).username("user1").build();
    }

    List<UserDto> getTestUsers() {
        return new ArrayList<>(List.of(
                UserDto.builder()
                        .id(1L)
                        .username("user1")
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .username("user2")
                        .build()
        ));
    }

}