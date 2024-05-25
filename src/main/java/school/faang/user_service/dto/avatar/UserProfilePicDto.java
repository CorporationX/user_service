package school.faang.user_service.dto.avatar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfilePicDto {
    @NotNull
    @NotBlank
    private String fileId;
    @NotNull
    @NotBlank
    private String smallFileId;
}
