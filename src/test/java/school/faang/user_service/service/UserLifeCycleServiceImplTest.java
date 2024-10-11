package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.NonUniqueFieldsException;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserLifeCycleServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        when(userRepository.existsByUsernameAndEmailAndPhone(username, email, phone)).thenReturn(false);
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(profilePictureService.saveProfilePictures(registrationDto)).thenReturn(profilePic);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.registrationUser(registrationDto);

        assertEquals(correctDto, result);
        verify(userRepository).existsByUsernameAndEmailAndPhone(username, email, phone);
        verify(countryRepository).findById(countryId);
        verify(profilePictureService).saveProfilePictures(registrationDto);
        verify(userRepository).save(user);
    }

    @Test
    void registrationUser_CountryNotFound() {
        String correctMessage = "Country not found";

        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> userService.registrationUser(registrationDto));

        assertEquals(correctMessage, exception.getMessage());
        verify(countryRepository).findById(countryId);
        verify(userRepository).existsByUsernameAndEmailAndPhone(username, email, phone);
        verify(profilePictureService, never()).saveProfilePictures(registrationDto);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registrationUser_InvalidData() {
        String invalidDataMessage = "Username/email/phone already in use";
        when(userRepository.existsByUsernameAndEmailAndPhone(username, email, phone)).thenReturn(true);

        Throwable exception = assertThrows(NonUniqueFieldsException.class,
                () -> userService.registrationUser(registrationDto));

        assertEquals(invalidDataMessage, exception.getMessage());
    }

    @Test
    void banUsersById() {
        List<Long> ids = List.of(1L, 2L, 3L);

        userService.banUsersById(ids);

        verify(userRepository).banUsersById(ids);
    }
}