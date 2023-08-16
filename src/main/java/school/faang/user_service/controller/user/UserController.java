package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PostMapping("/CSV")
    public List<UserDto> createCSV(@RequestParam MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            return userService.createUserCSV(inputStream);
        } catch (IOException e) {
            throw new FileException("Can't read file: " + e.getMessage());
        }
    }
}
