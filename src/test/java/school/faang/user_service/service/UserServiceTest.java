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
import school.faang.user_service.filter.goal.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.filter.filtersForUserFilterDto.DtoUserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<DtoUserFilter> userFilters;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user1;
    private User user2;
    private User user3;
    private UserFilterDto userFilterDto;
    List<User> userList = List.of(user1, user2, user3);

    @BeforeEach
    void setUp(){
        user1 = User.builder().id(1).active(true).premium(new Premium()).build();
        user2 = User.builder().id(2).active(true).premium(null).build();
        user3 = User.builder().id(3).active(true).premium(new Premium()).build();
    }

    @Test
    public void getAllPremiumUsersTest(){
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(userList.stream());

        int expected = 2;
        int actual = userService.getPremiumUsers(userFilterDto).size();

        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void getAllPremiumFilteredUsersTest(){

    }


}
