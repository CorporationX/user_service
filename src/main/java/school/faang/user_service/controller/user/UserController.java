package school.faang.user_service.controller.user;

import com.json.student.Person;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import school.faang.user_service.service.user.UserService;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;


    @PostMapping("/upload/csv")
    public void addUsersFromFile(@NotNull @RequestParam("file") MultipartFile file) {
        userService.saveUsersFromFile(file);
    }
}
