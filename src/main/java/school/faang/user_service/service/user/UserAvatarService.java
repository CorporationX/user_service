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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.resource.ResourceService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    public ResourceDto upload(long userId, MultipartFile avatar) {
        User user = getUserFromRepository(userId);
        String folderName = String.format("users_avatars/%d", userId);

        //TODO: валидацию на размер и что это картинка, и придумать как делать ресайз
        Resource uploadedResource = resourceService.uploadFile(avatar, folderName);
        return resourceMapper.toDto(uploadedResource);
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
    }
}
