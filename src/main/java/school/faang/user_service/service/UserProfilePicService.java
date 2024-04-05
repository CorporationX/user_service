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

    public User uploadUserProfilePic(Long userId, MultipartFile file) throws IOException {
        logger.info("Uploading profile picture for user with ID: {}", userId);
        String folder = "user-profile-pics";
        // Сохранить файл в S3 и получить fileId как строку
        String fileId = saveFileToS3(file, folder);

        // Retrieve user by ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Update user profile pic
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(fileId); // Установить fileId
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return user;
    }

    public void deleteUserProfilePic(Long userId) throws IOException {
        // Retrieve user by ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve fileId from user profile pic
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            String fileId = userProfilePic.getFileId();

            // Log the fileId before deleting the file
            logger.info("Deleting profile pic for user with ID {}: fileId={}", userId, fileId);

            // Construct folder and file name using fileId (or any other relevant information)
            // Assuming fileId represents the filename or key in Amazon S3
            String folder = "user-profile-pics";

            // Delete the file from S3
            s3Service.deleteFile(folder, fileId);

            // Update user profile to remove the fileId
            user.setUserProfilePic(null);
            userRepository.save(user);
        }
    }

    public InputStream getUserProfilePic(Long userId) throws IOException {
        // Retrieve user by ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve fileId from user profile pic
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            String fileId = userProfilePic.getFileId();

            // Download the file from S3
            String folder = "user-profile-pics";
            return s3Service.downloadFile(folder, fileId);
        } else {
            throw new RuntimeException("User profile pic not found");
        }
    }

    private String saveFileToS3(MultipartFile file, String folder) throws IOException {
        UserProfilePic userProfilePic = s3Service.uploadFile(file, folder).getUserProfilePic();
        return userProfilePic.getFileId();
    }
}
