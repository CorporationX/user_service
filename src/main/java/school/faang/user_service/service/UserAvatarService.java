package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.event.ProfilePicEvent;
import school.faang.user_service.publisher.ProfilePicEventPublisher;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAvatarService {
    private final ProfilePicEventPublisher profilePicEventSender;

    @Value(value = "${user-profile-pic.folder-name}")
    private String folderName;

    public void uploadAvatar(Long userId, MultipartFile file) {
        String profilePicKey = String.format("%s/%s-%s", folderName, UUID.randomUUID(), file.getOriginalFilename());

        profilePicEventSender.publish(ProfilePicEvent.builder()
                .userId(userId)
                .profilePicKey(profilePicKey)
                .build());
    }
}
