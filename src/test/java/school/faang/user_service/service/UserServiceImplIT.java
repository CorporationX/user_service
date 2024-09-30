package school.faang.user_service.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.UserServiceApplication;
import school.faang.user_service.config.TestContainersInitializer;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestContainersInitializer.class, classes = UserServiceApplication.class)
class UserServiceImplIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private CountryRepository countryRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    public void testUploadFile() throws IOException {
        File file = new File("src/test/resources/files/students.csv");
        FileInputStream inputStream = new FileInputStream(file);

        List<UserDto> userDtos = userService.uploadFile(inputStream);
        assertNotNull(userDtos);
        assertEquals(4, userDtos.size());

        Assertions.assertThat(userDtos).extracting(UserDto::username)
                .contains("Doe_John", "Lee_Jennifer", "Johnson_Michael", "Clark_Christopher");

        Assertions.assertThat(userDtos).extracting(UserDto::email)
                .contains("johndoe@example.com", "michaeljohnson@example.com",
                        "jenniferlee@example.com", "christopherclark@example.com");
    }
}