package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.EmptyFileException;
import school.faang.user_service.exception.FileFormatException;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        log.info("Endpoint <getUsersByIds>, uri='/users' was called successfully");
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long userId) {
        log.info("Endpoint <getUser>, uri='/users/{}' was called successfully", userId);
        return userService.getUser(userId);
    }

    @PostMapping("/telegram")
    public String setTelegramContact() {
        return userService.setTelegramContact();
    }

    @PostMapping("{userId}/telegram/{chatId}")
    public void saveTelegramChatId(@PathVariable long userId, @PathVariable long chatId) {
        userService.saveTelegramChatId(userId, chatId);
    }

    @PostMapping("/upload")
    public void uploadUsers(@RequestParam("persons") MultipartFile multipartFile) {
        validateFile(multipartFile);
        userService.saveStudents(multipartFile);
    }

    private void validateFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new EmptyFileException("You send empty file");
        }
        if (!isCSVFile(multipartFile)) {
            throw new FileFormatException("This is not csv file, please send csv format file");
        }
    }

    private boolean isCSVFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("text/csv");
    }
}
