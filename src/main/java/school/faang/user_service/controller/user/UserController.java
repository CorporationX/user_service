package school.faang.user_service.controller.user;

import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
    private final ConverterCsvToPerson converterCsvToPerson;
    private final UserValidator userValidator;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        userFilterDtoValidator.checkIsNull(userFilterDto);
        log.info("Received userFilterDto: {}", userFilterDto);
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("/add/file")
    public void convertScvFile(@RequestParam("file") MultipartFile file) {
        userValidator.validateCsvFile(file);
        List<Person> persons = converterCsvToPerson.convertCsvToPerson(file);
        log.info("Received Persons: {}", persons);
        userService.convertCsvFile(persons);
    }
}