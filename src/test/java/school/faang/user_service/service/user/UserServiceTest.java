package school.faang.user_service.service.user;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.student.Address;
import school.faang.user_service.entity.student.ContactInfo;
import school.faang.user_service.entity.student.Education;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserFilter userFilter;

    @Mock
    private GoalService goalService;

    @Mock
    private EventService eventService;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserServiceImpl userService;

    private long id;
    Stream<User> userStream;
    UserDto userDto;
    UserFilterDto userFilterDto;
    User user;
    List<UserFilter> filters;

    @BeforeEach
    void setUp() {
        id = 1;
        userFilterDto = new UserFilterDto();
        userDto = new UserDto(
                2L,
                "JaneSmith",
                "janesmith@example.com");

        user = User.builder()
                .id(2L)
                .goals(List.of())
                .ownedEvents(List.of())
                .username("JaneSmith")
                .email("janesmith@example.com")
                .phone("0987654321")
                .aboutMe("About Jane Smith")
                .experience(5)
                .build();

        userStream = Stream.of(user);
        filters = List.of(userFilter);
        userService = new UserServiceImpl(userRepository, countryRepository, filters, userMapper, goalService,
                eventService, mentorshipService);
    }

    @Test
    @DisplayName("test CSV parsing logic")
    void testCsvParsing() {
        ByteArrayInputStream inputStream = getTestStream();
        List<Person> persons = getTestPersonList();

        List<Person> userList = userService.parseFileToPersons.apply(inputStream);

        assertThat(userList).isEqualTo(persons);
    }

    @Test
    @DisplayName("test that both users are saved to db")
    void testAddUsersFromFile() throws IOException {
        ByteArrayInputStream inputStream = getTestStream();
        List<Person> persons = getTestPersonList();;

        userService.addUsersFromFile(inputStream);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository,times(2)).save(userCaptor.capture());
        List<User> capturedUsers = userCaptor.getAllValues();

        assertThat(capturedUsers.get(0).getUsername()).startsWith(persons.get(0).getFirstName());
        assertThat(capturedUsers.get(1).getUsername()).startsWith(persons.get(1).getFirstName());
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

        assertThrows(DataValidationException.class, () -> userService.deactivateUserProfile(id));
    }


    @Test
    void testDeactivateUserProfileOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deactivateUserProfile(id);

        verify(userRepository).findById(id);
        verify(goalService).removeGoals(List.of());
        verify(eventService).deleteEvents(List.of());
        verify(mentorshipService).deleteMentorFromMentees(anyLong(), any());
        verify(userRepository).save(any());
    }

    private static @NotNull List<Person> getTestPersonList() {
        List<Person> persons = new ArrayList<>();

        Person johnDoe = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .yearOfBirth(1998)
                .group("A")
                .studentID("123456")
                .contactInfo(ContactInfo.builder()
                        .email("johndoe@example.com")
                        .phone("+1-123-456-7890")
                        .address(Address.builder()
                                .street("123 Main Street")
                                .city("New York")
                                .state("NY")
                                .country("USA")
                                .postalCode("10001")
                                .build())
                        .build())
                .education(Education.builder()
                        .faculty("Computer Science")
                        .yearOfStudy(3)
                        .major("Software Engineering")
                        .build())
                .status("Active")
                .admissionDate("2016-09-01")
                .graduationDate("2020-05-30")
                .previousEducation(new ArrayList<>())
                .scholarship(true)
                .employer("XYZ Technologies")
                .build();

        Person michaelJohnson = Person.builder()
                .firstName("Michael")
                .lastName("Johnson")
                .yearOfBirth(1988)
                .group("Group A")
                .studentID("246813")
                .contactInfo(ContactInfo.builder()
                        .email("michaeljohnson@example.com")
                        .phone("1112223333")
                        .address(Address.builder()
                                .street("Second Street")
                                .city("Miami")
                                .state("FL")
                                .country("USA")
                                .postalCode("67890")
                                .build())
                        .build())
                .education(Education.builder()
                        .faculty("Law")
                        .yearOfStudy(2021)
                        .major("Corporate Law")
                        .build())
                .status("Graduated")
                .admissionDate("2019-01-01")
                .graduationDate("2021-12-31")
                .previousEducation(new ArrayList<>())
                .scholarship(true)
                .employer("ABC Law Firm")
                .build();

        persons.add(johnDoe);
        persons.add(michaelJohnson);
        return persons;
    }

    private static @NotNull ByteArrayInputStream getTestStream() {
        String csvData = "firstName,lastName,yearOfBirth,group,studentID,email,phone,street,city,state,country,postalCode,faculty,yearOfStudy,major,GPA,status,admissionDate,graduationDate,degree,institution,completionYear,scholarship,employer\n" +
                "John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,2016,true,XYZ Technologies\n" +
                "Michael,Johnson,1988,Group A,246813,michaeljohnson@example.com,1112223333,Second Street,Miami,FL,USA,67890,Law,2021,Corporate Law,3.7,Graduated,2019-01-01,2021-12-31,Bachelor,DEF University,2019,true,ABC Law Firm\n" ;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        return inputStream;
    }
}