package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.service.csv.CsvParserToPersonService;
import school.faang.user_service.service.user.ConvertToUserService;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CsvParserToPersonService csvParserToPersonService;
    private final ConvertToUserService convertToUserService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/students")
    public List<UserDto> saveUsers(@RequestParam("students") MultipartFile file) throws IOException {

        InputStream inputStream = file.getInputStream();

        List<Person> persons = csvParserToPersonService.convertCsv(inputStream);
        inputStream.close();
        return convertToUserService.prepareAndSaveUsers(persons);
    }
}
