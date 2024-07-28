package school.faang.user_service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.ContactInfo;
import school.faang.user_service.entity.person.Education;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.person.Address;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    private final long id = 1L;
    private final String userName = "Username";
    private final String email = "email@gmail.com";
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(id)
                .username(userName)
                .email(email)
                .build();
    }

    @Test
    public void toDto() {
        UserDto userDto = userMapper.toDto(user);
        assertUserDto(userDto);
    }

    @Test
    public void toEntity() {
        UserDto userDto = new UserDto(id, userName, email);
        User resultUser = userMapper.toEntity(userDto);
        assertUser(resultUser);
    }

    @Test
    public void personToUserTest() {
        Person person = createPerson();
        User resultUser = userMapper.personToUser(person);
        assertPersonToUser(person, resultUser);
    }

    private Person createPerson() {
        Address address = Address.builder()
                .city("New York")
                .state("NY")
                .build();

        ContactInfo contactInfo = ContactInfo.builder()
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .address(address)
                .build();

        Education education = Education.builder()
                .faculty("Engineering")
                .yearOfStudy(2023)
                .major("Computer Science")
                .build();

        return Person.builder()
                .firstName("John")
                .lastName("Doe")
                .contactInfo(contactInfo)
                .education(education)
                .employer("TechCorp")
                .build();
    }

    private void assertUserDto(UserDto userDto) {
        Assertions.assertEquals(userDto.getId(), id);
        Assertions.assertEquals(userDto.getEmail(), email);
        Assertions.assertEquals(userDto.getUsername(), userName);
    }

    private void assertUser(User user) {
        Assertions.assertEquals(user.getId(), id);
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getUsername(), userName);
    }

    private void assertPersonToUser(Person person, User user) {
        Assertions.assertEquals(user.getUsername(), person.getFirstName() + " " + person.getLastName());
        Assertions.assertEquals(user.getEmail(), person.getContactInfo().getEmail());
        Assertions.assertEquals(user.getPhone(), person.getContactInfo().getPhone());
        Assertions.assertEquals(user.getCity(), person.getContactInfo().getAddress().getCity());
        String aboutMe = person.getContactInfo().getAddress().getState() + ", " +
                person.getEducation().getFaculty() + ", " +
                person.getEducation().getYearOfStudy() + ", " +
                person.getEducation().getMajor() + ", " +
                person.getEmployer();
        Assertions.assertEquals(user.getAboutMe(), aboutMe);
    }
}
