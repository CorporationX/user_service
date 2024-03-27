package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.resource.ResourceDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.mapper.resource.ResourceMapper;
import school.faang.user_service.repository.ResourceRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.resource.ResourceService;
import school.faang.user_service.validation.user.UserAvatarValidator;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final UserAvatarValidator userAvatarValidator;
    private final ResourceRepository resourceRepository;

    public ResourceDto upload(long userId, MultipartFile avatar) {
        //TODO: валидация на размер и что это картинка, и придумать как делать ресайз
        userAvatarValidator.validateAvatarSize(avatar.getSize());
        User user = getUserFromRepository(userId);
        String folderName = String.format("users_avatars/%d", userId);

        Resource uploadedResource = resourceService.uploadFile(avatar, folderName);
        return resourceMapper.toDto(resourceRepository.save(uploadedResource));
    }

    public InputStream get(long avatarId) {
        Resource avatar = getAvatarFromRepository(avatarId);
        return resourceService.getFile(avatar.getKey());
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
    }

    private Resource getAvatarFromRepository(long avatarId) {
        return resourceRepository.findById(avatarId)
                .orElseThrow(() -> new EntityNotFoundException("Avatar doesn't exist by ID: " + avatarId));
    }
}
