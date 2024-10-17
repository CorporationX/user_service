package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUser() {
        long id = 1L;
        User userEntity = new User();
        userEntity.setId(id);
        userEntity.setUsername("Name");
        userEntity.setEmail("mail.ru");

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        UserDto result = userService.getUser(id);

        assertEquals(userEntity.getUsername(), result.getUsername());
        assertEquals(userEntity.getEmail(), result.getEmail());

        verify(userRepository).findById(id);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testGetUserWithLocaleAndContactPreference() {
        long id = 1L;
        User userEntity = new User();
        userEntity.setId(id);
        userEntity.setUsername("Name");
        userEntity.setEmail("mail.ru");
        when(userRepository.findByIdWithCountryAndContactPreference(id)).thenReturn(Optional.of(userEntity));

        UserDto result = userService.getUserWithLocaleAndContactPreference(id);

        assertEquals(userEntity.getUsername(), result.getUsername());
        assertEquals(userEntity.getEmail(), result.getEmail());

        verify(userRepository).findByIdWithCountryAndContactPreference(id);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testGetUsersByIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);

        List<User> users = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        users.add(user1);
        users.add(user2);

        when(userRepository.findAllById(ids)).thenReturn(users);

        assertDoesNotThrow(() -> userService.getUsersByIds(ids));

    }
}