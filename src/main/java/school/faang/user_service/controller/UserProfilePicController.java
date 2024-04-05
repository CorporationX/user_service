package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.UserProfilePicService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/user/profile-pic")
public class UserProfilePicController {

    @Autowired
    private UserProfilePicService userProfilePicService;

    // Контроллер для загрузки изображения профиля пользователя
    @PostMapping("/{userId}")
    public ResponseEntity<?> uploadUserProfilePic(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            userProfilePicService.uploadUserProfilePic(userId, file);
            return ResponseEntity.ok("Изображение профиля пользователя успешно загружено");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось загрузить изображение профиля пользователя");
        }
    }

    // Контроллер для удаления изображения профиля пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserProfilePic(@PathVariable Long userId) {
        try {
            userProfilePicService.deleteUserProfilePic(userId);
            return ResponseEntity.ok("Изображение профиля пользователя успешно удалено");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось удалить изображение профиля пользователя");
        }
    }

    // Контроллер для получения изображения профиля пользователя
    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getUserProfilePic(@PathVariable Long userId) {
        try {
            InputStream userProfilePicStream = userProfilePicService.getUserProfilePic(userId);

            // Чтение изображения из потока в массив байтов
            byte[] imageBytes = userProfilePicStream.readAllBytes();

            // Подготовка заголовков ответа для изображения
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);

            // Возвращаем ответ с данными изображения и заголовками
            return ResponseEntity.ok().headers(headers).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
