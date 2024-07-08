package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.*;
import school.faang.user_service.testData.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        TestData testData = new TestData();

        user = testData.getUser();

        userDto = testData.getUserDto();
    }

    @DisplayName("should map user to userDto")
    @Test
    void shouldMapUserEntityToUserDto() {
        UserDto actualDto = userMapper.toDto(user);

        assertEquals(userDto, actualDto);
    }

    @DisplayName("should map list of users to list of userDtos")
    @Test
    void shouldMapUserEntityListToUserDtoList() {
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        List<UserDto> actualDtos = userMapper.toDto(users);

        assertEquals(userDtos, actualDtos);
    }

    @DisplayName("should map User to Person")
    @Test
    void testToUser() {
        User actualUser = userMapper.toUser(returnPerson());
        assertEquals(returnUser(), actualUser);
    }

    private User returnUser() {
        User user = new User();
        user.setUsername("VadimBlack");
        user.setEmail("vladimirowitch.vadim@gmail.com");
        user.setPhone("79165895532");
        user.setCity("Moscow");
        user.setAboutMe("I'm from Central, study at Computer Science, i'm 2013rd year student, my major is Some major, my employer is MIB");
        return user;
    }

    private Person returnPerson() {
        Education education = new Education();
        education.setFaculty("Computer Science");
        education.setYearOfStudy(2013);
        education.setMajor("Some major");

        Address address = new Address();
        address.setCity("Moscow");
        address.setState("Central");

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("vladimirowitch.vadim@gmail.com");
        contactInfo.setPhone("79165895532");
        contactInfo.setAddress(address);

        Person person = new Person();
        person.setFirstName("Vadim");
        person.setLastName("Black");
        person.setContactInfo(contactInfo);
        person.setEmployer("MIB");
        person.setEducation(education);
        return person;
    }
}