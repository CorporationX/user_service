package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilterService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserFilterService userFilterService;
    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void findPremiumUsers() {
        Stream<User> users = getUsers().stream();
        UserFilterDto userFilterDto = new UserFilterDto();

        when(userRepository.findPremiumUsers()).thenReturn(users);
        when(userFilterService.applyFilters(users, userFilterDto)).thenReturn(users);

        assertEquals(userServiceImpl.findPremiumUsers(userFilterDto), getUsers().stream().map(userMapper::toDto).toList());
    }

    private List<User> getUsers() {
        return new ArrayList<>(List.of(
                User.builder().id(1L).premium(new Premium()).build(),
                User.builder().id(2L).premium(new Premium()).build(),
                User.builder().id(3L).premium(new Premium()).build()
        ));
    }
}
