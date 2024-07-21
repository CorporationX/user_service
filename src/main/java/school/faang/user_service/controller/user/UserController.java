package school.faang.user_service.controller.user;

import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.converter.starter.ConverterCsvToPerson;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserFilterDtoValidator userFilterDtoValidator;
    private final UserValidator userValidator;
    private final ConverterCsvToPerson converterCsvToPerson;

    @GetMapping("/{userId}/followers")
    public List<Long> getIdsFollowersUser(@PathVariable("userId") long userId) {
        return userService.getIdsFollowersUser(userId);
    }

    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        userFilterDtoValidator.checkIsNull(userFilterDto);

        log.info("Received userFilterDto: {}", userFilterDto);
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("{userid}/picture")
    public UserDto uploadProfilePicture(@PathVariable Long userid, @RequestParam MultipartFile picture) {
        userValidator.checkUserInDB(userid);
        userValidator.checkMaxSizePic(picture);

        return userService.uploadProfilePicture(userid, picture);
    }

    @GetMapping("{userId}/picture")
    public ResponseEntity<byte[]> downloadProfilePicture(@PathVariable Long userId) {
        userValidator.checkUserInDB(userId);

        return userService.downloadProfilePicture(userId);
    }

    @DeleteMapping("{userId}/picture")
    public UserDto deleteProfilePicture(@PathVariable Long userId) {
        userValidator.checkUserInDB(userId);

        return userService.deleteProfilePicture(userId);
    }

    @PostMapping("/add/file")
    public void convertScvFile(@RequestParam("file") MultipartFile file) {
        userValidator.validateCsvFile(file);
        List<Person> persons = converterCsvToPerson.convertCsvToPerson(file);
        log.info("Received Persons: {}", persons);
        userService.convertCsvFile(persons);
    }
}