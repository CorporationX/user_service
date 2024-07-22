package school.faang.user_service.controller.user;

import com.json.student.Person;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.avatar.ProfilePicService;
import school.faang.user_service.service.csv.CSVFileService;
import school.faang.user_service.service.csv.CsvFileConverter;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "Users")
@Slf4j
@Validated
public class UserController {
    private final ProfilePicService profilePicService;
    private final UserService userService;
    private final CSVFileService csvFileService;
    private final CsvFileConverter converter;

    @Operation(summary = "Get premium users")
    @PostMapping("premium")
    public List<UserDto> getPremiumUsers(@Valid @ParameterObject @RequestBody(required = false) UserFilterDto filter) {
        return userService.findPremiumUsers(filter);
    }

    @GetMapping("{userId}")
    @Operation(summary = "Get user by ID")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Deactivate user")
    @PostMapping("deactivation/{id}")
    public void deactivateUser(@Positive @Parameter @PathVariable Long id) {
        userService.deactivateUserById(id);
    }

    @Operation(summary = "Get users by ids")
    @PostMapping
    public List<UserDto> getUsersByIds(@NotNull @RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers();
    }

    @Operation(summary = "Upload CSV file")
    @PostMapping("/add/file")
    public String convertCsvFile(@RequestParam("file") MultipartFile file) {
        List<Person> persons = converter.convertCsvToPerson(file);
        log.info("Received Persons: {}", persons);
        csvFileService.convertCsvFile(persons);
        return file.getOriginalFilename();
    }

    @Operation(summary = "Create user")
    @PostMapping("creature")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto createUser(@ParameterObject @RequestBody @Valid UserDto userDto){
        profilePicService.generateAndSetPic(userDto);
        return userService.createUser(userDto);
    }
}
