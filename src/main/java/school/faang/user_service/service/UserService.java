package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ResourceDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final S3Service s3Service;

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

    public ResourceDto addUserPic(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));
        file.getResource()
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(s3Service.uploadFile(file, "userId"));

        user.setUserProfilePic(userProfilePic);

        userRepository.save(user);
        return null;
    }
}
