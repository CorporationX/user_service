package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import school.faang.user_service.dto.SubscriptionDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.util.validator.SubscriptionValidator;
@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @Mock
    private SubscriptionService service;
    @Spy
    private SubscriptionValidator validator;
    @InjectMocks
    private SubscriptionController controller;
    private SubscriptionDto dto;
    private UserFilterDto filterDto;

    @BeforeEach
    void setUp() {
        dto = new SubscriptionDto(1, 2);
        filterDto = new UserFilterDto();
    }

    @Test
    void followUserOkResponseTest() {
        var res = controller.followUser(dto);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void followUserSameIdExceptionTest() {
        dto.setFolloweeId(1);
        Assertions.assertThrows(DataValidationException.class,
                () -> controller.followUser(dto));
    }

    @Test
    void unfollowUserOkResponseTest() {
        var res = controller.unfollowUser(dto);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void unfollowUserSameIdExceptionTest() {
        dto.setFolloweeId(1);
        Assertions.assertThrows(DataValidationException.class,
                () -> controller.unfollowUser(dto));
    }

    @Test
    void GetByFolloweeTest() {
        var res = controller.getFollowers(1,filterDto);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void GetByFolloweeInvalidIdExceptionTest() {
        Assertions.assertThrows(DataValidationException.class,
                () -> controller.getFollowers(0,filterDto));
    }

    @Test
    void GetFollowersCountTest() {
        var res = controller.getFollowersCount(1);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void GetFollowersCountInvalidIdExceptionTest() {
        Assertions.assertThrows(DataValidationException.class,
                () -> controller.getFollowers(0,filterDto));
    }
    @Test
    void GetFolloweesTest() {
        var res = controller.getFollowing(1, filterDto);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void GetFolloweesCountTest() {
        var res = controller.getFollowing(1, filterDto);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void GetFolloweesCountInvalidIdTest() {
        Assertions.assertThrows(DataValidationException.class,
                () -> controller.getFollowing(-1, filterDto));
    }
}
