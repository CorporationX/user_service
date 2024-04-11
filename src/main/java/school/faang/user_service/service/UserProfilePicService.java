package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfilePicService {

    private final S3Service s3Service;
    private final UserRepository userRepository;

    public User uploadUserProfilePic(Long userId, MultipartFile file) throws IOException {
        log.info("Загрузка изображения профиля для пользователя с ID: {}", userId);
        String folder = "user-profile-pics";
        String[] fileKeys = s3Service.saveResizedImages(file, folder);

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(fileKeys[0]);
        userProfilePic.setSmallFileId(fileKeys[1]);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return user;
    }

    public void deleteUserProfilePic(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            String fileId = userProfilePic.getFileId();

            log.info("Удаление изображения профиля для пользователя с ID {}: fileId={}", userId, fileId);

            String folder = "user-profile-pics";

            s3Service.deleteFile(folder, fileId);

            user.setUserProfilePic(null);
            userRepository.save(user);
        }
    }

    public InputStream getUserProfilePic(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            String fileId = userProfilePic.getFileId();

            String folder = "user-profile-pics";
            return s3Service.downloadFile(folder, fileId);
        } else {
            throw new RuntimeException("Изображение профиля пользователя не найдено");
        }
    }
}
