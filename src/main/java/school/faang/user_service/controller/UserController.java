
package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.CsvUploadResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.upload.UserServiceUpload;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final UserService userService;
    private final UserServiceUpload userServiceUpload;

    @Override
    public UserDto getUser(Long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @Override
    public CsvUploadResponseDto uploadStudentsCsv(MultipartFile file) {
        return userServiceUpload.processStudentsCsv(file);
    }
}