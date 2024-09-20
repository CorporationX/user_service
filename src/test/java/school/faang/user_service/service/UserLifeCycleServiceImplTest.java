package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserLifeCycleServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLifeCycleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private ProfilePictureService profilePictureService;

    @Mock
    private MentorshipService mentorshipService;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserLifeCycleServiceImpl userService;

    private final long countryId = 1L;
    private final String username = "Robert";
    private final String email = "robert@java.com";
    private final String phone = "+123456";

    private final UserRegistrationDto registrationDto = UserRegistrationDto.builder()
            .username(username)
            .email(email)
            .phone(phone)
            .countryId(countryId)
            .build();
    ;

    @Test
    void deactivateUser_WithValidId() {
        long id = 1L;
        boolean active = false;
        doNothing().when(goalRepository).deleteUnusedGoalsByMentorId(id);
        doNothing().when(eventRepository).deleteAllByOwnerId(id);
        doNothing().when(mentorshipService).stopMentorship(id);
        doNothing().when(userRepository).updateUserActive(id, active);

        userService.deactivateUser(id);

        verify(goalRepository).deleteUnusedGoalsByMentorId(id);
        verify(eventRepository).deleteAllByOwnerId(id);
        verify(mentorshipService).stopMentorship(id);
        verify(userRepository).updateUserActive(id, active);
    }

    @Test
    void deactivateUser_WithNull() {
        long id = 1L;
        boolean active = false;

        assertThrows(NullPointerException.class,
                () -> userService.deactivateUser(null));

        verify(goalRepository, never()).deleteUnusedGoalsByMentorId(id);
        verify(eventRepository, never()).deleteAllByOwnerId(id);
        verify(mentorshipService, never()).stopMentorship(id);
        verify(userRepository, never()).updateUserActive(id, active);
    }

    @Test
    void registrationUser_Success() {
        Country country = new Country();
        UserProfilePic profilePic = new UserProfilePic();
        User user = User.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .country(country)
                .userProfilePic(profilePic)
                .build();
        UserDto correctDto = UserDto.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByPhone(phone)).thenReturn(false);
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(profilePictureService.saveProfilePictures(registrationDto)).thenReturn(profilePic);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.registrationUser(registrationDto);

        assertEquals(correctDto, result);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByPhone(phone);
        verify(countryRepository).findById(countryId);
        verify(profilePictureService).saveProfilePictures(registrationDto);
        verify(userRepository).save(user);
    }

    @Test
    void registrationUser_CountryNotFound() {
        String correctMessage = "Country not found";

        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(DataValidationException.class,
                () -> userService.registrationUser(registrationDto));

        assertEquals(correctMessage, exception.getMessage());
        verify(countryRepository).findById(countryId);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByPhone(phone);
        verify(profilePictureService, never()).saveProfilePictures(registrationDto);
        verify(userRepository, never()).save(any(User.class));
    }

    @ParameterizedTest
    @CsvSource({
            "true, false, false",
            "false, true, false",
            "false, false, true",
    })
    void registrationUser_InvalidData(boolean existsUsername, boolean existsEmail, boolean existsPhone) {
        String invalidDataMessage = "Username/email/phone already in use";

        if (existsUsername) {
            when(userRepository.existsByUsername(registrationDto.username())).thenReturn(true);
        }
        if (existsEmail) {
            when(userRepository.existsByEmail(registrationDto.email())).thenReturn(true);
        }
        if (existsPhone) {
            when(userRepository.existsByPhone(registrationDto.phone())).thenReturn(true);
        }

        Throwable exception = assertThrows(DataValidationException.class,
                () -> userService.registrationUser(registrationDto));

        assertEquals(invalidDataMessage, exception.getMessage());
    }
}