package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProfilePic generatedUserProfilePic;
    @Mock
    private CountryService countryService;
    @Spy
    private UserMapperImpl userMapper;
    @Captor
    private ArgumentCaptor<User> captor;
    @InjectMocks
    private UserService userService;
    private static final long EXISTENT_USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;
    private User existentUser;
    private UserRegistrationDto registerUserDto;
    private final String SMALL_PIC_GENERATED_URL = "small-pic-generated-url";
    private final String PIC_GENERATED_URL = "pic-generated-url";
    private final String SMALL_PIC_URL = "pic-url";
    private final String PIC_URL = "pic-url";
    private final String COUNTRY_NAME = "US";
    private final Country COUNTRY = Country.builder()
            .title(COUNTRY_NAME)
            .build();


    @BeforeEach
    public void init() {
        existentUser = new User();
        existentUser.setId(EXISTENT_USER_ID);

        registerUserDto = UserRegistrationDto.builder()
                .username("username")
                .password("password")
                .email("email@gmail.com")
                .country(COUNTRY_NAME)
                .profilePicSmallFileId(SMALL_PIC_URL)
                .profilePicFileId(PIC_URL)
                .build();
    }

    @Test
    public void testCreateUser_userHasProfilePicture_doesNotGeneratePicture() {
        when(countryService.getCountryByTitle(registerUserDto.getCountry())).thenReturn(COUNTRY);

        userService.createUser(registerUserDto);

        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();

        UserProfilePic userProfilePic = savedUser.getUserProfilePic();
        assertEquals(PIC_URL, userProfilePic.getFileId());
        assertEquals(SMALL_PIC_URL, userProfilePic.getSmallFileId());
        assertEquals(registerUserDto.getUsername(), savedUser.getUsername());
        assertEquals(registerUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    public void testCreateUser_userHasNotProfilePicture_generatePicture() {
        when(generatedUserProfilePic.getSmallFileId()).thenReturn(SMALL_PIC_GENERATED_URL);
        when(generatedUserProfilePic.getFileId()).thenReturn(PIC_GENERATED_URL);
        when(countryService.getCountryByTitle(registerUserDto.getCountry())).thenReturn(COUNTRY);
        registerUserDto.setProfilePicFileId(null);
        registerUserDto.setProfilePicSmallFileId(null);

        userService.createUser(registerUserDto);

        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();

        UserProfilePic userProfilePic = savedUser.getUserProfilePic();
        assertEquals(PIC_GENERATED_URL, userProfilePic.getFileId());
        assertEquals(SMALL_PIC_GENERATED_URL, userProfilePic.getSmallFileId());
        assertEquals(registerUserDto.getUsername(), savedUser.getUsername());
        assertEquals(registerUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    public void testGetUserById_userExists_returnsUser() {
        mockUserFindBiId(EXISTENT_USER_ID);
        User existingUserById = userService.getUserById(EXISTENT_USER_ID);
        assertEquals(existingUserById, existentUser);
    }

    @Test
    public void testGetUserById_userNotExist_throwsEntityNotFoundException() {
        mockUserFindBiId(NON_EXISTENT_USER_ID);
        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById(NON_EXISTENT_USER_ID)
        );
    }

    @Test
    public void testGetUserDtoById_userExists_returnsUserDto() {
        mockUserFindBiId(EXISTENT_USER_ID);
        UserDto existingUserById = userService.getUserDtoById(EXISTENT_USER_ID);
        assertEquals(existingUserById, userMapper.toDto(existentUser));
    }

    @Test
    public void testGetUserById_userNotExists_throwsEntityNotFoundException() {
        mockUserFindBiId(NON_EXISTENT_USER_ID);
        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById(NON_EXISTENT_USER_ID)
        );
    }

    private void mockUserFindBiId(long id) {
        Optional<User> userOpt = id == EXISTENT_USER_ID ? Optional.of(existentUser) : Optional.empty();
        when(userRepository.findById(id)).thenReturn(userOpt);
    }
}
