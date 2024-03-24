package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserMapperImpl userMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    User firstUser;
    User secondUser;
    List<Long> userIds;
    List<User> users;

    @BeforeEach
    void setUp() {
        firstUser = User.builder()
                .id(1L)
                .username("Petya")
                .build();
        secondUser = User.builder()
                .id(2L)
                .username("Vanya")
                .build();
        userIds = List.of(firstUser.getId(), firstUser.getId());
        users = List.of(firstUser, secondUser);
    }

    @Test
    public void testGetUser() {
        Mockito.when(userRepository.findById(firstUser.getId())).thenReturn(Optional.ofNullable(firstUser));

        userService.getUser(firstUser.getId());

        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(userMapper, times(1)).toDto(firstUser);
    }

    @Test
    public void testGetUsers() {
        Mockito.when(userRepository.findAllById(userIds)).thenReturn(users);

        userService.getUsersByIds(userIds);

        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(1)).toDto(users);
    }

}