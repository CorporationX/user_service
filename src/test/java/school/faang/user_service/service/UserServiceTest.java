package school.faang.user_service.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.events.BanUserEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CreateUserMapperImpl;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.UserServiceValidator;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private PersonMapper personMapper;

    @Spy
    private CreateUserMapperImpl createUserMapper;

    @Spy
    private UserServiceValidator validator;

    @Mock
    private CountryService countryService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private S3Service s3Service;

    @Spy
    private CsvMapper csvMapper;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Mock
    private UserContext userContext;

    @Mock
    private GoalService goalService;

    @Mock
    private MentorshipService mentorshipService;

    private long userId;

    private User user;
    private UserDto userDto;

    private CreateUserDto createUserDto;

    private UserProfilePic userProfilePic;

    private BanUserEvent banUserEvent;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto();
        createUserDto.setCountryId(1L);

        userId = 1L;
        user = new User();
        userId = 1L;
        user.setId(userId);

        userDto = new UserDto();
        userDto.setId(1L);

        userProfilePic = new UserProfilePic();

        banUserEvent = new BanUserEvent();
        banUserEvent.setUserId(1);
        banUserEvent.setCommentCount(3);
    }

    @Test
    public void testExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.existsById(userId));
    }

    @Test
    public void testNotExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertFalse(userService.existsById(userId));
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(userId));
    }

    @Test
    public void testThrowExceptionGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUserByIdNotfound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUsersByIds() {
        List<User> users = List.of(new User(), new User());
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(users);
        userService.getUsersByIds(ids);
    }

    @Test
    void findById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        assertEquals(Optional.of(new User()), userService.findById(userId));
    }

    @Test
    void getUserDtoByIdSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        assertEquals(userMapper.toDto(new User()), userService.getUserDtoById(userId));
    }

    @Test
    void getUserDtoByIdFailed() {
        when(userRepository.findById(userId)).thenThrow(new EntityNotFoundException(String.format("User with id %s not found", userId)));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(userId));
        assertEquals("User with id %s not found".formatted(userId), exception.getMessage());
    }

    @Test
    void uploadCsvUsersSuccess() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.lenient().when(file.getInputStream()).thenReturn(
                UserServiceTest.class.getClassLoader().getResourceAsStream("files/students-test.csv"));
        Mockito.lenient().when(userRepository.saveAll(Mockito.anyList())).thenReturn(List.of(new User()));

        assertEquals(List.of(userMapper.toDto(new User())), userService.uploadCsvUsers(file));
    }

    @Test
    void uploadCsvUsers_BadFile() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.lenient().when(file.getInputStream()).thenReturn(
                UserServiceTest.class.getClassLoader().getResourceAsStream("files/students-bad-file.csv"));
        Mockito.lenient().when(userRepository.saveAll(Mockito.anyList())).thenReturn(List.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.uploadCsvUsers(file));
        assertEquals("Error while reading file", exception.getMessage());
    }

    @Test
    public void createUserShouldReturnUserDtoWhenUserIsCreated() {
        createUserDto.setEmail("88005553535");
        createUserDto.setPhone("89090909090");

        Country country = new Country();
        when(countryService.getCountryById(1L)).thenReturn(country);

        user.setId(1L);

        String avatarUrl = "http://example.com/avatar.png";
        when(avatarService.generateAvatar(any(User.class))).thenReturn(avatarUrl);

        when(userRepository.save(userCaptor.capture())).thenReturn(user);

        userService.createUser(createUserDto);

        User savedUser = userCaptor.getValue();

        assertEquals(savedUser.getEmail(), createUserDto.getEmail());
        assertEquals(savedUser.getPhone(), createUserDto.getPhone());

        verify(countryService).getCountryById(1L);
        verify(avatarService).generateAvatar(any(User.class));
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    public void getAvatarUrlShouldReturnUrlWhenValidUrlProvided() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String validUrl = "http://example.com/avatar.png";
        userProfilePic.setFileId(validUrl);
        userProfilePic.setSmallFileId(validUrl);

        user.setUserProfilePic(userProfilePic);

        String result = userService.getAvatarUrl(1L);

        assertEquals(validUrl, result);
    }

    @Test
    public void getAvatarUrl_ShouldReturnPresignedUrl_WhenInvalidKeyProvided() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String invalidKey = "avatarKey";
        userProfilePic.setFileId(invalidKey);
        userProfilePic.setSmallFileId(invalidKey);

        user.setUserProfilePic(userProfilePic);

        String presignedUrl = "http://example.com/presigned-url";
        when(s3Service.generatePresignedUrl(invalidKey)).thenReturn(presignedUrl);

        String result = userService.getAvatarUrl(1L);

        assertEquals(presignedUrl, result);
    }

    @Test
    void testInvalidUserIdForBanUser() {
        banUserEvent.setUserId(-23);
        assertThrows(IllegalArgumentException.class,
                () -> userService.banUser(banUserEvent));
    }

    @Test
    void testCorrectWorkForBanUser() {
        user.setMessages(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.banUser(banUserEvent);

        verify(userRepository).save(userCaptor.capture());
        User resultUser = userCaptor.getValue();

        assertEquals(resultUser.getMessages(), banUserEvent.getCommentCount());

    }

    @Test
    void testCorrectWorkForBanUserWithBanSetting() {
        user.setMessages(3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.banUser(banUserEvent);

        verify(userRepository).save(userCaptor.capture());
        User resultUser = userCaptor.getValue();

        assertEquals(resultUser.getMessages(), 6);
        assertTrue(resultUser.isBanned());

    }

    @Test
    public void testDeactivateUser() {
        Goal firstGoal = new Goal();
        Goal secondGoal = new Goal();

        List<Goal> goals = List.of(firstGoal, secondGoal);

        User firstMentor = new User();
        User secondMentor = new User();

        firstMentor.setId(5L);
        secondMentor.setId(7L);

        User firstMentee = new User();
        User secondMentee = new User();

        firstMentee.setId(8L);
        secondMentee.setId(9L);

        firstGoal.setMentor(firstMentor);
        firstGoal.setMentor(secondMentor);

        user.setGoals(goals);
        user.setOwnedEvents(List.of(new Event(), new Event()));
        user.setActive(true);

        user.setMentees(List.of(firstMentee, secondMentee));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        doNothing().when(mentorshipService).deleteMentor(firstMentee.getId(), userId);
        doNothing().when(mentorshipService).deleteMentor(secondMentee.getId(), userId);
        when(goalService.getGoalsByMentorId(userId)).thenReturn(goals.stream());

        userService.deactivateUser();

        verify(userRepository).save(user);
        assertNull(user.getGoals());
        assertNull(user.getOwnedEvents());
        assertFalse(user.isActive());
    }

    @Test
    void testBanUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setBanned(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.banUser(userId);

        assertTrue(user.isBanned());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testBanUser_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.banUser(userId));

        verify(userRepository, times(0)).save(any());
    }
}
