package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
@Testcontainers
public class UserControllerIntegrationTest {

    @Autowired
    UserController userController;
    @Autowired
    UserRepository userRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSqlContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_password");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSqlContainer::getPassword);
        registry.add("spring.datasource.username", postgreSqlContainer::getUsername);
    }

    @Test
    public void testUsersFromCsvUpload() {
        Path path = Paths.get("src/main/resources/files/students.csv");
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        MultipartFile multipartFile =
                new MockMultipartFile("Persons", "test.csv", "text/csv", content);

        userController.uploadUsers(multipartFile);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(userRepository.existsByUsername("Johnatan_Doe"));
        Assertions.assertTrue(userRepository.existsByUsername("Michaelis_Johnson"));
        Assertions.assertTrue(userRepository.existsByUsername("Jennifer_Lee"));
        Assertions.assertTrue(userRepository.existsByUsername("Christopher_Clark"));
    }
}
