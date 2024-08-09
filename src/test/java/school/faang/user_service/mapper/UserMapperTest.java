package school.faang.user_service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.ContactInfo;
import school.faang.user_service.entity.person.Education;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.person.Address;

public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final long id = 1L;
    private final String username = "Username";
    private final String email = "email@gmail.com";
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    @Test
    public void toDto() {
        UserDto userDto = userMapper.toDto(user);
        Assertions.assertEquals(userDto.getId(), id);
        Assertions.assertEquals(userDto.getEmail(), email);
        Assertions.assertEquals(userDto.getUsername(), username);
        assertUserDto(userDto);
    }

    @Test
    public void toEntity() {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setEmail(email);
        User resultUser = userMapper.toEntity(userDto);
        Assertions.assertEquals(resultUser.getId(), id);
        Assertions.assertEquals(resultUser.getEmail(), email);
        Assertions.assertEquals(resultUser.getUsername(), username);
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
        Assertions.assertEquals(userDto.getUsername(), username);
    }

    private void assertUser(User user) {
        Assertions.assertEquals(user.getId(), id);
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getUsername(), username);
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
