package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUser_ExistsUser() {
        long id = 1L;
        User user = User.builder()
                .id(id)
                .username("Java")
                .build();
        UserDto correctAnswer = userMapper.toDto(user);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(id);

        assertEquals(correctAnswer, result);
    }

    @Test
    void getUser_NotExistsUser() {
        long id = 1L;
        String correctMessage = "User not found with id " + id;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUser(id));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void getUsersByIds_ExistsUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<User> users = List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                User.builder().id(3L).build()
        );
        List<UserDto> correctUserDtos = userMapper.toDto(users);
        when(userRepository.findAllById(ids)).thenReturn(users);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(correctUserDtos, result);
    }

    @Test
    void getUsersByIds_NotExistsUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<User> users = Collections.emptyList();
        List<UserDto> correctUserDtos = Collections.emptyList();
        when(userRepository.findAllById(ids)).thenReturn(users);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(correctUserDtos, result);
    }
}