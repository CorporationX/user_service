package school.faang.user_service.service.user;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.ProfileViewEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.ToJsonMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publishers.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserMapperImpl userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private ToJsonMapper toJsonMapper;
    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;

    @InjectMocks
    private UserService userService;

    User firstUser;
    User secondUser;
    List<Long> userIds;
    List<User> users;
    ProfileViewEventDto eventDto;

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

        eventDto = ProfileViewEventDto.builder()
                .build();
    }

    @Test
    public void testGetUser_UserDoesNotExist() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.empty());

        NoSuchElementException e = Assert.assertThrows(NoSuchElementException.class, () -> userService.getUser(firstUser.getId()));
        Assertions.assertEquals(e.getMessage(), "User not found!");
    }

    @Test
    public void testGetUser() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.ofNullable(firstUser));
        when(userContext.getUserId()).thenReturn(secondUser.getId());
        when(toJsonMapper.toJson(any(ProfileViewEventDto.class))).thenReturn("[]");

        userService.getUser(firstUser.getId());

        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(userMapper, times(1)).toDto(firstUser);
        verify(profileViewEventPublisher, times(1)).publish(anyString());
    }

    @Test
    public void testGetUsers() {
        when(userRepository.findAllById(userIds)).thenReturn(users);

        userService.getUsersByIds(userIds);

        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(1)).toDto(users);
    }
}