package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Spy
    private UserMapperImpl userMapper;
    private long USER_ID = 1L;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .username("Nikita")
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .username("Nikita")
                .build();
    }

    @Test
    void getUserWithoutUserInDataBase() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(USER_ID));
    }

    @Test
    void getUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));
        UserDto actualUser = userService.getUser(USER_ID);
        assertEquals(userDto, actualUser);
    }

    @Test
    public void testFindUserThrowEntityNotFoundExc() {
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(null));
    }
  
    public void testFindUserCallFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        userService.findUserById(1L);
        verify(userRepository, times(1)).findById(1L);
    }
}