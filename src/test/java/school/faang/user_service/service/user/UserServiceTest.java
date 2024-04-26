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
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publishers.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
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
    private EventMapper eventMapper;
    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;
    @Mock
    private S3Service s3Service;


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

        UserNotFoundException e = Assert.assertThrows(UserNotFoundException.class, () -> userService.getUser(firstUser.getId()));
        Assertions.assertEquals(e.getMessage(), MessageError.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    public void testGetUser() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.ofNullable(firstUser));
        when(userContext.getUserId()).thenReturn(secondUser.getId());
        when(eventMapper.toJson(any(ProfileViewEventDto.class))).thenReturn("[]");

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

    @Test
    public void testCreateSuccess() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("John Doe");


        User user = new User();
        user.setId(1L);
        user.setUsername("John Doe");

        UserProfilePic userProfilePic = UserProfilePic.builder()
                .smallFileId("smallFileId")
                .fileId("fileId")
                .build();
        user.setUserProfilePic(userProfilePic);

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto createdUserDto = userService.create(userDto);

        assertNotNull(createdUserDto);
        assertEquals(userDto.getId(), createdUserDto.getId());
        assertEquals(userDto.getUsername(), createdUserDto.getUsername());

    }

    @Test
    public void testCreate_UserAlreadyExists_ExceptionThrown() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(new User()));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> userService.create(userDto));
        assertEquals("User with id 1 exists", exception.getMessage());

    }
}