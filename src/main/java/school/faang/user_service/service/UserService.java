package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final S3Service s3Service;

    // вынеси в application
    private final static int MAX_IMAGE_PIC = 1080;
    private final static int MIN_IMAGE_PIC = 170;

    public UserDto findUserById(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public List<UserDto> findUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserProfilePicDto addUserPic(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(s3Service.uploadFile(convertFilePermissions(file, MAX_IMAGE_PIC)));
        userProfilePic.setSmallFileId(s3Service.uploadFile(convertFilePermissions(file, MIN_IMAGE_PIC)));

        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return UserProfilePicDto.fromUserProfilePic(userProfilePic);
    }

    public InputStream getUserPic(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        if (user.getUserProfilePic() == null) {
            throw new EntityNotFoundException("User " + userId + " not found");
        }
        return s3Service.downloadFile(user.getUserProfilePic().getFileId());
    }

    public void deleteUserPic(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        s3Service.delete(user.getUserProfilePic().getFileId());
        s3Service.delete(user.getUserProfilePic().getSmallFileId());
    }

    private MultipartFile convertFilePermissions(MultipartFile file, int permission) throws IOException {
        Thumbnails.of(file.getInputStream())
                .size(permission, permission)
                .toFile(file.getOriginalFilename());

        return file;
    }
}
