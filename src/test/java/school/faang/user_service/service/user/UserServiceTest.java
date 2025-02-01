package school.faang.user_service.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.dto.user.UpdateUsersRankDto;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.mapper.csv.CsvParser;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UpdateUsersRankDto usersRankDto;
    private User user;
    private List<User> users;

    @InjectMocks
    private UserService userService;

    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private UserContext userContext;
    @Mock
    private AvatarService avatarService;

    @Mock
    private CsvParser csvParser;

    @Mock
    private CountryService countryService;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        var firstUser = User.builder().id(1L).build();
        var secondUser = User.builder().id(2L).build();
        var thirdUser = User.builder()
                .id(3L)
                .mentors(List.of(secondUser))
                .mentees(List.of(firstUser))
                .active(true)
                .contactPreference(ContactPreference.builder().preference(PreferredContact.EMAIL).build())
                .build();
        user = thirdUser;
        users = List.of(thirdUser);

        usersRankDto = UpdateUsersRankDto.builder()
                .halfUserRank(50.0)
                .maximumUserRating(100.0)
                .minimumUserRating(0.0)
                .maximumGrowthRating(9.9)
                .ratingGrowthIntensive(0.05)
                .build();
    }

    @Test
    void findById_WithCorrectId_ReturnNotEmptyOptionalUser() {
        Optional<User> userOptional = Optional.of(User.builder().build());
        when(userRepository.findById(1L))
                .thenReturn(userOptional);

        assertNotNull(userService.findById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUsersRank_WhenUsersHaveValidRanks() {
        Map<Long, Double> usersNewRanks = Map.of(
                1L, 10.555,
                2L, 20.123,
                3L, 0.0);
        usersRankDto.setUsersRankByIds(usersNewRanks);

        userService.updateUsersRankByUserIds(usersRankDto);

        verify(userRepository).updateUserRankByUserId(1L, 10.56);
        verify(userRepository).updateUserRankByUserId(2L, 20.12);
        verify(userRepository, times(0)).updateUserRankByUserId(3L, 0.0);
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }

    @Test
    void testUpdatePassiveUsers_SuccessUpdating() {
        Map<Long, Double> usersNewRanks = Map.of(1L, 10.0, 2L, 15.0);
        usersRankDto.setUsersRankByIds(usersNewRanks);
        Set<Long> activeUserIds = usersNewRanks.keySet();
        BigDecimal maxPossibleRating = BigDecimal.valueOf(usersRankDto.getMaximumGrowthRating() * usersRankDto.getRatingGrowthIntensive());
        double roundedMaxPossibleRating = maxPossibleRating.setScale(2, RoundingMode.HALF_UP).doubleValue();

        userService.updateUsersRankByUserIds(usersRankDto);

        verify(userRepository).updatePassiveUsersRatingWhichRatingLessThanRating(eq(roundedMaxPossibleRating), eq(activeUserIds));
        verify(userRepository).updatePassiveUsersRatingWhichRatingMoreThanRating(eq(roundedMaxPossibleRating), eq(activeUserIds));
    }

    @Test
    void testFlushAndClearCalled_WhenBatchLimitExceeded() {
        Map<Long, Double> usersNewRanks = new HashMap<>();
        for (long i = 1; i <= 60; i++) {
            usersNewRanks.put(i, (double) i);
        }
        usersRankDto.setUsersRankByIds(usersNewRanks);

        userService.updateUsersRankByUserIds(usersRankDto);

        verify(entityManager, times(2)).flush();
        verify(entityManager, times(2)).clear();
    }

    @Test
    void testGenerateRandomAvatarSuccess() {
        Long userId = 1L;
        String avatarUrl = "http://localhost/avatar/1.svg";
        User user = new User();
        user.setId(userId);
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarService.generateRandomAvatar(anyString(), eq(userId + ".svg"))).thenReturn(avatarUrl);
        String result = userService.generateRandomAvatar();
        assertEquals(avatarUrl, result);
        assertNotNull(user.getUserProfilePic());
        assertEquals(avatarUrl, user.getUserProfilePic().getFileId());
        verify(userRepository).save(user);
    }

    @Test
    void testGenerateRandomAvatarUserNotFound() {
        Long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.generateRandomAvatar();
        });
        assertEquals("User not found", exception.getMessage());
        verify(avatarService, never()).generateRandomAvatar(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testToGetUserDtoById_ShouldThrowException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.getUserDtoById(user.getId()));
    }

    @Test
    void testToGetUserDtosByIds_ShouldReturnCorrectDtos() {
        var ids = List.of(3L);
        when(userRepository.findAllByIds(ids))
                .thenReturn(Optional.of(users));

        var userDtos = userService.getUserDtosByIds(ids);

        assertNotNull(userDtos);
        assertEquals(users.size(), userDtos.size());
    }

    @Test
    void testToGetUserDtosByIds_ShouldThrowException() {
        var ids = List.of(1L, 2L, 3L);
        when(userRepository.findAllByIds(ids))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.getUserDtosByIds(ids));
    }

    @Test
    void testToUploadUsersByInputStream_ShouldSuccessSave() throws IOException {
        ClassPathResource resource = new ClassPathResource("files/students.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                "text/csv",
                resource.getInputStream()
        );
        ArgumentCaptor<List<User>> listUsersArgumentCaptor = ArgumentCaptor.forClass((Class<List<User>>) (Class) ArrayList.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(csvParser.parseCsv(file, Person.class))
                .thenReturn(List.of(Person.builder()
                        .firstName("Test")
                        .lastName("Test")
                        .email("test@mail.ru")
                        .phone("877777777")
                        .city("City")
                        .country("Country")
                        .state("State")
                        .faculty("Faculty")
                        .yearOfStudy("Year")
                        .major("major")
                        .employer("employer")
                        .build()));
        when(countryService.getCountryOrCreateByName(userArgumentCaptor.capture()))
                .thenReturn(Country.builder().title("USA").build());
        when(userRepository.saveAll(listUsersArgumentCaptor.capture()))
                .thenReturn(List.of(User.builder().build()));

        userService.uploadUsers(file);

        verify(csvParser, times(1)).parseCsv(file, Person.class);
        assertNotNull(listUsersArgumentCaptor.getValue());
        verify(userRepository, times(1)).saveAll(listUsersArgumentCaptor.getValue());
        assertEquals(1, listUsersArgumentCaptor.getValue().size());
    }

    @Test
    void testToGenerateRandomPassword_ShouldSuccessGenerate() {
        User user = User.builder()
                .email("test@mail.ru")
                .build();

        String password = userService.generateRandomPassword(user);

        assertNotNull(password);
        assertEquals("test@mail.ru", password);
    }

    @Test
    void testToGetUserDtoById_ShouldReturnCorrectDto() {
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(user));

        var userDto = userService.getUserDtoById(3L);

        verify(userMapper, times(1)).toDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertNotNull(userDto);
    }

    @Test
    void testDeactivateUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deactivateUser(1L);
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeactivateUserByIncorrectId() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.deactivateUser(1L));
    }

    @Test
    void testDeactivateUserByDeactivatedUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        user.setActive(false);

        assertThrows(DataValidationException.class, () -> userService.deactivateUser(1L));
    }
}