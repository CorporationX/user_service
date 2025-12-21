package school.faang.user_service.service.user;

import jakarta.validation.constraints.NotBlank;

public interface AvatarGenerationService {
    String generateAvatarUrl();

    String setSizeToGeneratedAvatar(@NotBlank String generatedAvatarUrl, int requiredSize);
}
