package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.exception.FileUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Tag(name = "Управление пользователями")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Деактивировать пользователя по идентификатору")
    @PostMapping("/users/deactivate/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @PostMapping("/users/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws FileUploadException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new FileUploadException("Invalid file name");
        }
        try {
            InputStream inputStream = file.getInputStream();
            userService.processCsv(inputStream);
        } catch (IOException e) {
            throw new FileUploadException("Error processing file: " + fileName);
        }
        return ResponseEntity.ok("File successfully processed: " + fileName);
    }
}
