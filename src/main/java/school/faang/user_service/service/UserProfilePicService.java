package school.faang.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.S3.S3Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;

@Service
public class UserProfilePicService {
    private final Logger logger = LoggerFactory.getLogger(UserProfilePicService.class);

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    // Метод загрузки изображения профиля пользователя
    public User uploadUserProfilePic(Long userId, MultipartFile file) throws IOException {
        logger.info("Загрузка изображения профиля для пользователя с ID: {}", userId);
        String folder = "user-profile-pics";
        // Сохранение файлов в Amazon S3 и получение их ключей
        String[] fileKeys = s3Service.saveResizedImages(file, folder);

        // Получение пользователя по ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Обновление изображений профиля пользователя
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(fileKeys[0]); // Установка ключа большого изображения
        userProfilePic.setSmallFileId(fileKeys[1]); // Установка ключа маленького изображения
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return user;
    }

    // Метод удаления изображения профиля пользователя
    public void deleteUserProfilePic(Long userId) throws IOException {
        // Получение пользователя по ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Получение fileId из изображения профиля пользователя
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            String fileId = userProfilePic.getFileId();

            logger.info("Удаление изображения профиля для пользователя с ID {}: fileId={}", userId, fileId);

            // Конструирование папки и имени файла с использованием fileId
            String folder = "user-profile-pics";

            // Удаление файла из Amazon S3
            s3Service.deleteFile(folder, fileId);

            // Обновление профиля пользователя для удаления fileId
            user.setUserProfilePic(null);
            userRepository.save(user);
        }
    }

    // Метод получения изображения профиля пользователя
    public InputStream getUserProfilePic(Long userId) throws IOException {
        // Получение пользователя по ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Получение fileId из изображения профиля пользователя
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            String fileId = userProfilePic.getFileId();

            // Загрузка файла из Amazon S3
            String folder = "user-profile-pics";
            return s3Service.downloadFile(folder, fileId);
        } else {
            throw new RuntimeException("Изображение профиля пользователя не найдено");
        }
    }
}
