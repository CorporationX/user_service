package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.user.UserProfilePicMapper;
import school.faang.user_service.parser.PersonParser;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.UserProfilePicS3Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {
    @Mock
    private PersonMapper personMapper;
    @Mock
    private PersonParser personParser;
    @Mock
    private Executor taskExecutor;
    @Mock
    private UserProfilePicS3Service userProfilePicS3Service;
    @Mock
    private UserProfilePicMapper profilePicMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private MultipartFile file;
    private User user;
    private UserProfilePic profilePic;

    @BeforeEach
    void setUp() {
        file = mock(MultipartFile.class);

        Person person = mock(Person.class);
        List<Person> students = List.of(person, person, person);

        user = mock(User.class);
        profilePic = mock(UserProfilePic.class);
        UserProfilePicDto profilePicDto = mock(UserProfilePicDto.class);

        when(user.getUserProfilePic()).thenReturn(profilePic);
        when(profilePic.getFileId()).thenReturn("fileId");
        when(profilePicMapper.toDto(profilePic)).thenReturn(profilePicDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(personParser.parse(file)).thenReturn(students);
        when(personMapper.toUser(person)).thenReturn(user);
        when(file.getSize()).thenReturn(99L);
        when(userProfilePicS3Service.upload(any(MultipartFile.class), anyString())).thenReturn(profilePic);

        ReflectionTestUtils.setField(userService, "partitionSize", 1);
        ReflectionTestUtils.setField(userService, "maxFileSize", 100);
        ReflectionTestUtils.setField(userService, "folder", "folder");
        ReflectionTestUtils.setField(userService, "diceBearBaseUrl", "https://api.dicebear.com/7.x/adventurer/svg?seed=");
    }

    @Test
    void saveStudents_shouldInvokeParseMethod() {
        userService.saveStudents(file);
        verify(personParser).parse(file);
    }

    @Test
    void saveStudents_shouldInvokeExecuteMethodThreeTimes() {
        userService.saveStudents(file);
        verify(taskExecutor, times(3)).execute(any(Runnable.class));
    }

    @Test
    void saveStudents_shouldInvokeExecuteMethodOneTime() {
        ReflectionTestUtils.setField(userService, "partitionSize", 3);
        userService.saveStudents(file);
        verify(taskExecutor, times(1)).execute(any(Runnable.class));
    }

    @Test
    void saveProfilePic_shouldThrowException() {
        when(file.getSize()).thenReturn(101L);
        assertThrows(DataValidationException.class, () -> userService.saveProfilePic(file, 1L));
    }

    @Test
    void saveProfilePic_shouldInvokeRepositoryFindById() {
        userService.saveProfilePic(file, 1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void saveProfilePic_shouldInvokeS3ServiceUploadMethod() {
        userService.saveProfilePic(file, 1L);
        verify(userProfilePicS3Service, times(1)).upload(any(MultipartFile.class), anyString());
    }

    @Test
    void saveProfilePic_shouldInvokeUserSetProfilePicMethod() {
        userService.saveProfilePic(file, 1L);
        verify(user, times(1)).setUserProfilePic(profilePic);
    }

    @Test
    void saveProfilePic_shouldInvokeProfilePicMapperToDtoMethod() {
        userService.saveProfilePic(file, 1L);
        verify(profilePicMapper, times(1)).toDto(profilePic);
    }

    @Test
    void getProfilePic_shouldInvokeRepositoryFindById() {
        userService.getProfilePic(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getProfilePic_shouldInvokeS3ServiceDownloadMethod() {
        userService.getProfilePic(1L);
        verify(userProfilePicS3Service, times(1)).download("fileId");
    }

    @Test
    void deleteProfilePic_shouldInvokeRepositoryFindById() {
        userService.deleteProfilePic(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void deleteProfilePic_shouldInvokeUserSetProfilePicMethod() {
        userService.deleteProfilePic(1L);
        verify(user, times(1)).setUserProfilePic(null);
    }

    @Test
    void deleteProfilePic_shouldInvokeS3ServiceDeleteMethod() {
        userService.deleteProfilePic(1L);
        verify(userProfilePicS3Service, times(1)).delete("fileId");
    }

    @Test
    void testSetBanForUser() {
        userService.setBanForUser(1L);
        verify(userRepository, times(1)).setBanUser(1L);
    }

    @Test
    public void testCreateAvatar() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.createAvatar(userId);

        assertNotNull(result.getAvatarUrl());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testCreateAvatar_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.createAvatar(userId));
    }

    @Test
    public void testGenerateRandomAvatarUrl() {
        String avatarUrl = userService.generateRandomAvatarUrl();

        assertTrue(avatarUrl.startsWith("https://api.dicebear.com/7.x/adventurer/svg?seed="));
    }
}