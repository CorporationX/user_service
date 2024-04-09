package school.faang.user_service.controller.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.csv.CSVFileParserService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CSVFileParserService csvFileParserService;

    @GetMapping("/filtered")
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping()
    public UserCreateDto createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Method createUser accepted " + userCreateDto);
        return userService.createUser(userCreateDto);
    }

    @PostMapping(value = "/upload-csv-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void parseCSV(@RequestParam MultipartFile file) {
        csvFileParserService.parseFile(file);
    }

    @GetMapping("/exists/{userId}")
    public boolean existById(@PathVariable @Positive(message = "Id must be greater than zero") long userId) {
        return userService.existById(userId);
    }
}
