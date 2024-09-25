package school.faang.user_service.service.user;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.util.AvatarApiService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final AvatarApiService avatarApiService;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<User> findPremiumUser(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(premiumUsers, (stream, filter) -> filter.apply(stream, filterDto), (stream, filter) -> stream)
                .toList();
    }

    @Transactional
    public User setAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow();
        ObjectMetadata metadata = null;
        byte[] fileBytes = null;
        String fileName = null;

        if (Objects.isNull(file) || file.isEmpty()) {
            fileBytes = avatarApiService.getDefaultAvatar(user.getUsername());
            fileName = "profile_default.svg";
            metadata = prepareFileMetadata("image/svg+xml", fileBytes.length);
        } else {
            try {
                fileBytes = file.getBytes();
            } catch(IOException e) {
                e.printStackTrace();
            }
            fileName = file.getOriginalFilename();
            metadata = prepareFileMetadata(file.getContentType(), fileBytes.length);
        }

        return userPictureUpdate(user, fileName, fileBytes, metadata);
    }

    private User userPictureUpdate(User toUpdate, String fileName, byte[] fileBytes, ObjectMetadata metadata) {
        String fileId = s3Service.uploadFile(fileName, new ByteArrayInputStream(fileBytes), metadata, toUpdate.getId());
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(fileId);
        toUpdate.setUserProfilePic(profilePic);
        return userRepository.save(toUpdate);
    }

    private ObjectMetadata prepareFileMetadata(String contentType, long sizeInBytes) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(sizeInBytes);
        return metadata;
    }
}
