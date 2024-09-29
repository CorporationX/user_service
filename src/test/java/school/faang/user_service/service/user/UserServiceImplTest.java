package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.upload.CsvLoader;
import school.faang.user_service.service.user.upload.UserUploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;
    
    @Spy
    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void getUser_Success() {
        User user = createUser();
        UserDto userDto = new UserDto(1L, "user", "email");
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = service.getUser(user.getId());

        assertEquals(result, userDto);
        verify(repository, times(1)).findById(user.getId());
    }

    @Test
    public void getUser_UserNotFound() {
        long id = 1L;

        doThrow(new EntityNotFoundException("User not found")).when(repository).findById(id);

        assertThrows(EntityNotFoundException.class, () -> service.getUser(id));
    }

    @Test
    public void getUsersByIds_Success() {
        List<Long> ids = List.of(1L, 2L, 3L, 4L);
        List<User> users = createUsers();
        UserDto userDto1 = new UserDto(1L, "user1", "email1");
        UserDto userDto2 = new UserDto(2L, "user2", "email2");
        List<UserDto> userDtos = List.of(userDto1, userDto2);

        when(repository.findAllById(ids)).thenReturn(users);

        List<UserDto> result = service.getUsersByIds(ids);

        assertEquals(result, userDtos);
        verify(repository, times(1)).findAllById(ids);
    }

    @Test
    public void getUsersByIds_SuccessButUsersNotFound() {
        List<Long> ids = new ArrayList<>();
        List<UserDto> userDtos = new ArrayList<>();

        when(repository.findAllById(ids)).thenReturn(new ArrayList<>());

        List<UserDto> result = service.getUsersByIds(ids);

        assertEquals(result, userDtos);
        verify(repository, times(1)).findAllById(ids);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("email");
        return user;
    }

    private List<User> createUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("email1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("email2");
        return List.of(user1, user2);
    }
}