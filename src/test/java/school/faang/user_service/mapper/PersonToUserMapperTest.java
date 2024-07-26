package school.faang.user_service.mapper;

import com.json.student.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

public class PersonToUserMapperTest {
    private UserMapper userMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Проверка приобразования Person to User")
    public void testPersonToUser() throws Exception {
        String personJson = "{"
                + "\"firstName\": \"John\","
                + "\"lastName\": \"Doe\","
                + "\"yearOfBirth\": 1995,"
                + "\"group\": \"A\","
                + "\"studentID\": \"12345\","
                + "\"contactInfo\": {"
                + "    \"email\": \"john.doe@example.com\","
                + "    \"phone\": \"123-456-7890\","
                + "    \"address\": {"
                + "        \"street\": \"123 Main St\","
                + "        \"city\": \"Springfield\","
                + "        \"state\": \"IL\","
                + "        \"country\": \"USA\","
                + "        \"postalCode\": \"62701\""
                + "    }"
                + "},"
                + "\"education\": {"
                + "    \"faculty\": \"Engineering\","
                + "    \"yearOfStudy\": 3,"
                + "    \"major\": \"Computer Science\","
                + "    \"GPA\": 3.8"
                + "},"
                + "\"status\": \"Active\","
                + "\"admissionDate\": \"2020-09-01\","
                + "\"graduationDate\": \"2024-06-01\","
                + "\"previousEducation\": [{"
                + "    \"degree\": \"BSc\","
                + "    \"institution\": \"Some University\","
                + "    \"completionYear\": 2018"
                + "}],"
                + "\"scholarship\": true,"
                + "\"employer\": \"Tech Company\""
                + "}";

        Person person = objectMapper.readValue(personJson, Person.class);

        User user = userMapper.personToUser(person);

        assertNotNull(user);
        assertEquals("John Doe", user.getUsername());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("123-456-7890", user.getPhone());
        assertEquals("Springfield", user.getCity());
        assertTrue(user.getAboutMe().contains("IL"));
        assertTrue(user.getAboutMe().contains("Engineering"));
        assertTrue(user.getAboutMe().contains("3"));
        assertTrue(user.getAboutMe().contains("Computer Science"));
        assertTrue(user.getAboutMe().contains("Tech Company"));
    }
}
