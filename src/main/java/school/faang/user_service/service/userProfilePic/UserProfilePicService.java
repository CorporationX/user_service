package school.faang.user_service.service.userProfilePic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfile.UserProfileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.mapper.userProfilePic.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfilePicService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final UserProfilePicMapper userProfilePicMapper;

    public UserProfileDto addImageInProfile(Long userId, MultipartFile multipartFile) throws IOException {
        User user = checkTheUserInTheDatabase(userId);

        String folder = user.getId() + user.getUsername();
        UserProfilePic userProfilePic = s3Service.uploadProfile(multipartFile, folder);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return userProfilePicMapper.toDto(user);
    }

    public InputStream getImageFromProfile(Long userId) throws IOException {
        User user = checkTheUserInTheDatabase(userId);

        return s3Service.downloadingByteImage(user.getUserProfilePic().getFileId());
    }

    public UserProfileDto deleteImageFromProfile(Long userId) {
        User user = checkTheUserInTheDatabase(userId);

        s3Service.deleteImage(user.getUserProfilePic());
        user.setUserProfilePic(null);
        userRepository.save(user);

        return userProfilePicMapper.toDto(user);
    }

    private User checkTheUserInTheDatabase(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            String errorMessage = String.format(ExceptionMessages.USER_NOT_FOUND, userId);
            log.error(errorMessage);
            return new NullPointerException(errorMessage);
        });
    }
}