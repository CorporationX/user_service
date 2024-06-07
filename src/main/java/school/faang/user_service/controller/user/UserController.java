package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static school.faang.user_service.exception.ExceptionMessage.NO_FILE_IN_REQUEST;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/uploadData")
    List<UserDto> uploadData(@RequestParam("file") MultipartFile multipartFile)
            throws IOException, ExecutionException, InterruptedException {
        if (multipartFile.isEmpty()) {
            throw new DataValidationException(NO_FILE_IN_REQUEST.getMessage());
        }
        return userService.saveUsers(multipartFile.getInputStream());
    }
}
