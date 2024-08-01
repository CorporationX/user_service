package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public class UserProfilePicDto {
    @NotBlank
    private String fileId;
    @NotBlank
    private String smallFileId;
}
