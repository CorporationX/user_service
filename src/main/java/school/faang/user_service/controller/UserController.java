package school.faang.user_service.controller;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.DeactivatedUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final CsvMapper csvMapper;
    private final String typeFile = "text/scv";

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            return userService.getPremiumUsers();
        }
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<DeactivatedUserDto> deactivateUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") @NotNull MultipartFile file) {
        if (!typeFile.equals(file.getContentType()) || file.isEmpty()) {
            return ResponseEntity.badRequest().body("The file must be in CSV format!");
        }
        try (InputStream inputStream = file.getInputStream()) {
            CsvSchema schema = csvMapper.schemaFor(Person.class).withHeader().withColumnSeparator(',');
            MappingIterator<Person> iterator = csvMapper.readerFor(Person.class)
                    .with(schema)
                    .readValues(inputStream);
            List<Person> people = iterator.readAll();

            userService.saveUsers(people);
            log.info("File uploaded");

            return ResponseEntity.ok("File uploaded and processed successfully.");
        } catch (Exception e) {
            log.error("Error while loading file");
            return ResponseEntity.internalServerError().body("Failed to process the file.");
        }
    }
}
