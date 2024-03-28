package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.resource.ResourceDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.image.ImageResizer;
import school.faang.user_service.mapper.resource.ResourceMapper;
import school.faang.user_service.repository.ResourceRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.resource.ResourceService;
import school.faang.user_service.validation.user.UserAvatarValidator;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final UserAvatarValidator userAvatarValidator;
    private final ResourceRepository resourceRepository;
    private final ImageResizer imageResizer;

    @Value("${services.dicebear.avatar}")
    private String randomAvatar;

    @Value("${services.dicebear.small_avatar}")
    private String randomAvatarSmall;

    @Transactional
    public List<ResourceDto> upload(long userId, MultipartFile avatar) {
        userAvatarValidator.validateIfAvatarIsImage(avatar.getContentType());
        User user = getUserFromRepository(userId);
        String folderName = String.format("users_avatars/%d", userId);

        MultipartFile resizedAvatar = imageResizer.resize(avatar, 1080, 1080);
        MultipartFile resizedAvatarSmall = imageResizer.resize(avatar, 170, 170);
        Resource uploadedResource = resourceService.uploadFile(resizedAvatar, folderName);
        Resource uploadedResourceSmall = resourceService.uploadFile(resizedAvatarSmall, folderName);
        log.info("Resources resized and uploaded to file storage");

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(uploadedResource.getKey())
                .smallFileId(uploadedResourceSmall.getKey())
                .build());
        userRepository.save(user);
        List<Resource> savedAvatars = resourceRepository.saveAll(List.of(uploadedResource, uploadedResourceSmall));
        log.info("User's profilePic saved: UserID: {}, Profile pics IDs: [{}, {}]",
                user.getId(),
                uploadedResource.getId(),
                uploadedResourceSmall.getId());

        return resourceMapper.toDto(savedAvatars);
    }

    public InputStream get(long avatarId) {
        Resource avatar = getAvatarFromRepository(avatarId);
        return resourceService.getFile(avatar.getKey());
    }

    @Transactional
    public void delete(long userId) {
        User user = getUserFromRepository(userId);
        List<Resource> usersAvatars = resourceRepository.findAllByUserId(userId);

        usersAvatars.forEach(avatar -> resourceService.deleteFile(avatar.getKey()));
        resourceRepository.deleteAll(usersAvatars);
        log.info("User's (ID: {}) avatar pictures deleted", userId);
        setRandomAvatarForUser(user);
        userRepository.save(user);
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
    }

    private Resource getAvatarFromRepository(long avatarId) {
        return resourceRepository.findById(avatarId)
                .orElseThrow(() -> new EntityNotFoundException("Avatar doesn't exist by ID: " + avatarId));
    }

    private void setRandomAvatarForUser(User user) {
        UUID uuid = UUID.randomUUID();
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(randomAvatar + uuid)
                .smallFileId(randomAvatarSmall + uuid)
                .build());
    }
}
