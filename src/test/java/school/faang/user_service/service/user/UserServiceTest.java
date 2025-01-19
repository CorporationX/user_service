package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;


public class UserServiceTest {

    private UserService userService;
    private List<UserFilter> filters;
    private UserRepository mockUserRepo;
    private UserMapper mockUserMapper;

    private User mockFirstUser;
    private User mockSecondUser;

    @BeforeEach
    void init(){
        mockUserRepo = Mockito.mock(UserRepository.class);
        mockUserMapper = Mockito.mock(UserMapper.class);
        UserFilter mockFirstUserFilter = Mockito.mock(UserFilter.class);
        UserFilter mockSecondUserFilter = Mockito.mock(UserFilter.class);
        filters = List.of(mockFirstUserFilter,mockSecondUserFilter);

        userService = new UserService(mockUserRepo, filters, mockUserMapper);
    }

    @Test
    public void testGetPremiumUsers() {
        UserFilterDto userFilterDto = new UserFilterDto();

        UserDto firsUserDto = new UserDto();
        UserDto secondUserDto = new UserDto();

        userFilterDto.setCityPattern("Moscow");
        userFilterDto.setNamePattern("Maxim");

        Stream<User> users = Stream.of(mockFirstUser, mockSecondUser);

        when(filters.get(0).isApplicable(userFilterDto)).thenReturn(true);
        when(filters.get(0).apply(mockUserRepo.findPremiumUsers(), userFilterDto)).thenReturn(users);
        when(mockUserMapper.toDto(mockFirstUser)).thenReturn(firsUserDto);

        userService.getPremiumUsers(userFilterDto);

        verify(filters.get(0), times(1)).isApplicable(userFilterDto);
        ArgumentCaptor<Stream<User>> streamCaptor = ArgumentCaptor.forClass(Stream.class);
        verify(filters.get(0)).apply(streamCaptor.capture(), eq(userFilterDto));
        Stream<User> capturedStream = streamCaptor.getValue();

    }
}
