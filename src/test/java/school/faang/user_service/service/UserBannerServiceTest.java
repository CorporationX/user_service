package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserBannerServiceTest {
    @Mock
    UserService userService;
    @InjectMocks
    UserBannerService userBannerService;
    User firstUser;
    List<Long> userIdsToBan;
    List<User> usersToBan;
    @BeforeEach
    void setUp(){

        firstUser = User.builder()
                .id(1)
                .banned(false)
                .build();
        userIdsToBan = List.of(firstUser.getId());
        usersToBan = List.of(firstUser);

    }

    @Test
    public void testBanUserById(){
        Mockito.when(userService.getUserEntityById(firstUser.getId())).thenReturn(firstUser);
        userBannerService.banUserById(firstUser.getId());
    }

    @Test
    public void testBanUsersByIds(){
        Mockito.when(userService.getUsersEntityByIds(userIdsToBan)).thenReturn(usersToBan);
        userBannerService.banUsersByIds(userIdsToBan);
    }
}