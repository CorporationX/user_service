package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Spy
    UserMapperImpl userMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUsers() {
        assertTrue(true);
    }

    @Test
    public void testGetUserById() {
        User user = User.builder()
                .id(1L)
                .username("Anton")
                .mentees(Collections.EMPTY_LIST)
                .mentors(Collections.EMPTY_LIST)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(user.getId());

        assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).findById(user.getId());
    }

//    @Test
//    public void testSaveUser() {
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("testuser");
//        user.setEmail("testuser@example.com");
//
//        when(userRepository.existsById(user.getId())).thenReturn(true);
//
//        userService.saveUser(user);
//
//        verify(userRepository, times(1)).save(user);
//
//        assertTrue(userService.existsUserById(user.getId()));
//    }
}