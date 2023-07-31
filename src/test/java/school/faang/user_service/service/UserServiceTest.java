package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.filter.user.ActiveUserFilter;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user1;
    private User user2;
    private User user3;

    List<User> userList;
    private UserFilterDto filter;


    @BeforeEach
    void setUp(){
        user1 = User.builder().id(1).active(true).premium(new Premium()).build();
        user2 = User.builder().id(2).active(false).premium(null).build();
        user3 = User.builder().id(3).active(true).premium(new Premium()).build();
        UserFilter userFilter = new ActiveUserFilter();
        List<UserFilter> userFilters = List.of(userFilter);
        userService = new UserService(userRepository, userFilters, userMapper);
        userList = List.of(user1, user2, user3);
        filter = new UserFilterDto();
        filter.setActive(true);
    }

    @Test
    public void getAllPremiumUsersTest(){
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(userList.stream());

        int expected = 2;
        int actual = userService.getPremiumUsers(filter).size();

        Assertions.assertEquals(expected, actual);

    }
}
