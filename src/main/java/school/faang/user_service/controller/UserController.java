package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> userIds) {
        return userService.findUsersByIds(userIds);
    }

    @PostMapping("/upload")
    public List<UserDto> uploadFile(@RequestParam("students.csv") MultipartFile file,
                                                       @RequestParam("person-schema.json") MultipartFile schemaJson) throws IOException {
        log.info("Received request to upload files: students.csv and person-schema.json");

        String schemaContent = new String(schemaJson.getBytes(), UTF_8);
        writeSchemaToFile(schemaContent);

        try (InputStream inputStream = file.getInputStream()) {
            log.info("Processing students.csv");

            List<UserDto> users = userService.saveStudents(inputStream).get();
            log.info("Processed {} users from students.csv", users.size());
            return users;
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error("Error processing students.csv", e);
            throw new RuntimeException(e);
        }
    }
    private void writeSchemaToFile(String schemaJson) throws IOException {
        Path schemaDir = Paths.get("src/main/resources/json");
        if (Files.notExists(schemaDir)) {
            Files.createDirectories(schemaDir);
            log.info("Created directory: {}", schemaDir.toAbsolutePath());
        }

        Path schemaFile = schemaDir.resolve("person-schema.json");
        try {
            Files.writeString(schemaFile, schemaJson, CREATE, TRUNCATE_EXISTING);
            log.info("Saved person-schema.json to {}", schemaFile.toAbsolutePath());
        } catch (IOException e) {
            log.error("Error saving person-schema.json", e);
            throw e;
        }
    }
}
