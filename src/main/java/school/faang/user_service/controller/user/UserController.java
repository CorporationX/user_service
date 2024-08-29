package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.csv.CsvUserService;
import school.faang.user_service.service.user.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USERS)
public class UserController {
    private final CsvUserService csvUserService;
    private final UserService userService;

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable long id){
        return userService.getUser(id);
    }

    @PostMapping(value = "/parseUser")
    public void getStudentsParsing(@RequestBody MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.error("multipartFile isEmpty");
            throw new IllegalArgumentException("multipartFile isEmpty");
        } else {
            csvUserService.getStudentsParsing(multipartFile);
        }
    }
}