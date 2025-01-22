package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserService userService;

    private List<UserFilter> filters;

    @Mock
    private UserRepository mockUserRepo;

    @Mock
    private UserMapper mockUserMapper;

    @Mock
    private User mockFirstUser;

    @Mock
    private User mockSecondUser;

    @BeforeEach
    void init() {

        UserFilter mockFirstUserFilter = Mockito.mock(UserFilter.class);
        UserFilter mockSecondUserFilter = Mockito.mock(UserFilter.class);
        filters = List.of(mockFirstUserFilter, mockSecondUserFilter);

        userService = new UserService(mockUserRepo, filters, mockUserMapper);
    }

    @Test
    public void testGetPremiumUsers() {
        UserFilterDto userFilterDto = new UserFilterDto();

        UserDto firstUserDto = new UserDto();

        userFilterDto.setCityPattern("Moscow");
        userFilterDto.setNamePattern("Maxim");

        List<User> users = List.of(mockFirstUser, mockSecondUser);

        when(filters.get(0).apply(mockUserRepo.findPremiumUsers().toList(), userFilterDto)).thenReturn(users);
        when(filters.get(0).isApplicable(userFilterDto)).thenReturn(true);
        when(mockUserMapper.toDto(mockFirstUser)).thenReturn(firstUserDto);

        userService.getPremiumUsers(userFilterDto);

        verify(filters.get(0), times(1)).isApplicable(userFilterDto);
        ArgumentCaptor<List<User>> listUsers = ArgumentCaptor.forClass(List.class);
        verify(filters.get(0), times(1)).apply(listUsers.capture(), eq(userFilterDto));


    }
}
