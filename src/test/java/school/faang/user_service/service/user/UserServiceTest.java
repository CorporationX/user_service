package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar_api.AvatarApiService;
import school.faang.user_service.service.filters.UserCityPattern;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.filters.UserSkillPattern;
import school.faang.user_service.service.s3.S3CompatibleService;
import school.faang.user_service.service.s3.S3ServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private AvatarApiService avatarApiService;

    @Mock
    private S3CompatibleService s3CompatibleService;

    @Mock
    private S3ServiceImpl s3Service;

    @Mock
    private List<UserFilter> userFilter = new ArrayList<>();

    @Mock
    private UserMapper mapper;

    @Mock
    private MultipartFile file;

    @Captor
    ArgumentCaptor<User> captor;


    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto = new UserDto();
    private Long userId = 1L;
    private List<Long> idsList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<UserDto> userDtoList = new ArrayList<>();
    private UserProfilePic userProfilePic = new UserProfilePic();


    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(userId);
        byte[] content = "test image content".getBytes();
    }

    @Test
    @DisplayName("Find premium user test")
    public void testFindPremiumUsers() {

        Skill skillOne = Skill.builder()
                .id(10L)
                .title("Java")
                .build();

        Skill skillTwo = Skill.builder()
                .id(11L)
                .title("SQL")
                .build();

        Skill skillThree = Skill.builder()
                .id(12L)
                .title("Spring")
                .build();

        User userOne = User.builder()
                .id(1L)
                .username("Frank")
                .city("Moscow")
                .skills(List.of(skillOne))
                .build();

        User userTwo = User.builder()
                .id(2L)
                .username("John")
                .city("SPb")
                .skills(List.of(skillTwo, skillThree))
                .build();

        User userThree = User.builder()
                .id(3L)
                .username("Ben")
                .city("SPb")
                .skills(List.of(skillOne, skillThree))
                .build();

        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCityPattern("SPb");
        userFilterDto.setSkillPattern("Java");

        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(userOne, userTwo, userThree));
        when(userFilter.stream()).thenReturn(Stream.of(
                new UserCityPattern(),
                new UserSkillPattern()
        ));

        List<User> premiumUsers = userService.findPremiumUser(userFilterDto);
        assertThat(premiumUsers.get(0)).usingRecursiveComparison().isEqualTo(userThree);
    }

    @Test
    public void getUserTest() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertEquals(result, userDto);
        Mockito.verify(userRepository, atLeastOnce()).findById(userId);
        Mockito.verify(mapper, atLeastOnce()).toDto(user);
    }

    @Test
    public void getUsersByIdsTest(){
        Mockito.when(userRepository.findAllById(idsList)).thenReturn(userList);
        Mockito.when(mapper.toDto(userList)).thenReturn(userDtoList);

        List<UserDto> result = userService.getUsersByIds(idsList);

        assertEquals(result, userDtoList);
        Mockito.verify(userRepository, atLeastOnce()).findAllById(idsList);
        Mockito.verify(mapper, atLeastOnce()).toDto(userList);
    }

    @Test
    public void registerUserTest() {
        byte[] expectedApiResponse = {1, 2, 3, 4, 5};
        Country country = new Country();
        country.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email@gmail.com");
        user.setPhone("123");
        user.setCountry(country);
        user.setActive(true);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("username");
        expectedUser.setPassword("password");
        expectedUser.setEmail("email@gmail.com");
        expectedUser.setPhone("123");
        expectedUser.setCountry(country);
        expectedUser.setActive(true);
        UserProfilePic profilePic = new UserProfilePic();
        expectedUser.setUserProfilePic(profilePic);

        when(countryRepository.existsById(country.getId())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        when(avatarApiService.generateDefaultAvatar(user.getUsername())).thenReturn(expectedApiResponse);

        User actual = userService.registerNewUser(user);
        assertNotNull(actual);
        assertThat(expectedUser).usingRecursiveComparison()
                .ignoringFields("userProfilePic")
                .isEqualTo(actual);
    }

    @Test
    public void testAddProfileImage_FileExceedsMaxSize() {
        int maxSizeFile = 6 * 1024 * 1024; // 6 MB
        when(file.getSize()).thenReturn((long) maxSizeFile);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.addProfileImage(userId, file));
        assertEquals("File exceeds the maximum size of 5 MB", exception.getMessage());
        verify(userRepository, never()).findById(userId);
        verify(s3Service, never()).uploadFile(any(byte[].class), anyString(), anyLong(), anyString());
    }

    @Test
    public void testGetBigImageFromProfile() throws IOException {
        userProfilePic.setFileId("largeImage.jpg");
        user.setUserProfilePic(userProfilePic);
        byte[] imageBytes = "test image content".getBytes();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.downloadFile("largeImage.jpg")).thenReturn(imageBytes);
        byte[] result = userService.getBigImageFromProfile(userId).getContentAsByteArray();
        assertEquals(new String(imageBytes), new String(result));
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).downloadFile("largeImage.jpg");
    }

    @Test
    public void testGetSmallImageFromProfile() throws IOException {
        userProfilePic.setSmallFileId("smallImage.jpg");
        user.setUserProfilePic(userProfilePic);
        byte[] imageBytes = "test image content".getBytes();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.downloadFile("smallImage.jpg")).thenReturn(imageBytes);
        byte[] result = userService.getSmallImageFromProfile(userId).getContentAsByteArray();
        assertEquals(new String(imageBytes), new String(result));
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).downloadFile("smallImage.jpg");
    }

    @Test
    public void testDeleteProfileImage() {
        userProfilePic.setFileId("largeImage.jpg");
        userProfilePic.setSmallFileId("smallImage.jpg");
        user.setUserProfilePic(userProfilePic);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.deleteProfileImage(userId);
        verify(s3Service, times(1)).deleteFile("largeImage.jpg");
        verify(s3Service, times(1)).deleteFile("smallImage.jpg");
        verify(userRepository, times(1)).save(user);
        assertNull(user.getUserProfilePic());
    }

    @Test
    public void testDeleteProfileImage_UserProfileNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.deleteProfileImage(userId));

        verify(s3Service, never()).deleteFile(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}