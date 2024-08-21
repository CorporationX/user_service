package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.event.ProfileViewEventDto;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    @Value("${services.frofilePic.fileLimit}")
    private int FILE_LIMIT;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> userIds) {
        return userService.findUsersByIds(userIds);
    }

    @PatchMapping("/{userId}/deactivate")
    public UserDto deactivateUserById(@PathVariable Long userId) {
        return userService.deactivateUserById(userId);
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

    @PostMapping("/profilePic/{userId}")
    public UserProfilePicDto addUsersPic(@PathVariable long userId, @RequestBody MultipartFile file) throws IOException {

        if (file.getSize() > FILE_LIMIT) {
            throw new FileSizeLimitExceededException("File Size Limit ", file.getSize(), FILE_LIMIT);
        }
        return userService.addUserPic(userId, file);
    }

    @GetMapping("/profilePic/{userId}")
    public ResponseEntity<byte[]> getUserPic(@PathVariable long userId) throws IOException {
        byte[] imageBytes = userService.getUserPic(userId).readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping("/profilePic/{userId}")
    public void deleteUserPic(@PathVariable long userId) {
        userService.deleteUserPic(userId);
    }

    @PostMapping("/profileView")
    public UserDto getUserProfile(@RequestBody ProfileViewEventDto profileViewEventDto) {
        return userService.getUserProfile(profileViewEventDto);
    }

}