package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.contact.ContactPreference;
import school.faang.user_service.model.enums.contact.PreferredContact;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.impl.event.EventServiceImpl;
import school.faang.user_service.service.impl.goal.GoalServiceImpl;
import school.faang.user_service.service.impl.mentorship.MentorshipServiceImpl;
import school.faang.user_service.service.impl.user.UserServiceImpl;
import school.faang.user_service.util.CsvParser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserFilter userFilter;

    @Mock
    private GoalServiceImpl goalService;

    @Mock
    private EventServiceImpl eventServiceImpl;

    @Mock
    private MentorshipServiceImpl mentorshipService;

    @Mock
    private CsvParser csvParser;

    private long id;
    private UserDto userDto;
    private UserFilterDto userFilterDto;
    private User user;
    private List<UserFilter> filters;

    @BeforeEach
    void setUp() {
        id = 1;
        userFilterDto = new UserFilterDto();
        userDto = new UserDto(
                1L,
                "JaneSmith",
                "janesmith@example.com",
                "0987654321",
                PreferredContact.TELEGRAM);

        ContactPreference contactPreference = ContactPreference.builder()
                .id(1L)
                .user(user)
                .preference(PreferredContact.TELEGRAM)
                .build();

        user = User.builder()
                .id(1L)
                .goals(List.of())
                .ownedEvents(List.of())
                .username("JaneSmith")
                .email("janesmith@example.com")
                .phone("0987654321")
                .aboutMe("About Jane Smith")
                .experience(5)
                .banned(false)
                .contactPreference(contactPreference)
                .build();

        filters = List.of(userFilter);

        userService = new UserServiceImpl(userRepository, countryRepository, filters, userMapper, goalService, eventServiceImpl, mentorshipService, csvParser);
    }

    @Test
    void findUserById_whenUserExists_thenReturnUser() {
        // given
        user = buildUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        // when
        var result = userService.findUserById(id);
        // then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void findUserById_UserDoesNotExist_ThrowsException() {
        // given
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        // when/then
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(id),
                "User with ID: %d does not exist.".formatted(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void shouldReturnPremiumUsersByFilters() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user));
        when(filters.get(0).isApplicable(any())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(user));

        var result = userService.getPremiumUsers(userFilterDto);

        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(user);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).contains(userDto);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void testDeactivateUserProfileWrongIdThrow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivateUserProfile(id));
    }


    @Test
    void testDeactivateUserProfileOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deactivateUserProfile(id);

        verify(userRepository).findById(id);
        verify(goalService).removeGoals(List.of());
        verify(eventServiceImpl).deleteEvents(List.of());
        verify(mentorshipService).deleteMentorFromMentees(anyLong(), any());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Get User - Success")
    void testGetUserShouldReturnUser() {
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());

        var result = userService.getUser(1L);

        verify(userRepository).findById(anyLong());
        verify(userMapper).toDto(user);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userDto);
    }

    @Test
    @DisplayName("Get User - Not Found")
    void testGetUserShouldThrowEntityNotFoundException() {
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                userService.getUser(1L)).withMessage("User with ID: %d does not exist.".formatted(id));

        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Get Users - Success")
    void testGetUsersShouldReturnUsers() {
        var user2 = User.builder()
                .id(2L)
                .goals(List.of())
                .ownedEvents(List.of())
                .username("Someone")
                .email("someone@mail.com")
                .phone("1234567890")
                .aboutMe("About Someone")
                .experience(10)
                .build();

        var userDto2 = UserDto.builder()
                .id(2L)
                .username("Someone")
                .email("someone@mail.com")
                .phone("1234567890")
                .build();

        doReturn(List.of(user, user2)).when(userRepository).findAllById(anyList());

        var result = userService.getUsersByIds(List.of(1L, 2L));

        verify(userRepository).findAllById(List.of(1L, 2L));
        verify(userMapper).toDto(user);
        assertThat(result).isNotNull().hasSize(2).isEqualTo(List.of(userDto, userDto2));
    }

    @Test
    void banUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        // Act
        userService.banUserById(1L);

        // Assert
        assertTrue(user.getBanned());
    }

    private User buildUser() {
        return User.builder()
                .id(id)
                .build();
    }
}

