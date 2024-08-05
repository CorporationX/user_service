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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
                                                       @RequestParam("person-schema.json") String schemaJson) throws IOException {
        log.info("Received request to upload files: students.csv and person-schema.json");
        writeSchemaToFileAsJson(schemaJson);

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
    private void writeSchemaToFileAsJson(String schemaJson) throws IOException {
        File schemaDir = new File("src/main/resources/json");
        if (!schemaDir.exists()) {
            schemaDir.mkdirs();
            log.info("Created directory: {}", schemaDir.getPath());
        }
        File schemaFile = new File(schemaDir, "person-schema.json");
        try(FileWriter fileWriter = new FileWriter(schemaFile)) {
            fileWriter.write(schemaJson);
            log.info("Saved person-schema.json to {}", schemaFile.getPath());
        }catch (IOException e) {
            log.error("Error saving person-schema.json", e);
            throw e;
        }
    }
}
