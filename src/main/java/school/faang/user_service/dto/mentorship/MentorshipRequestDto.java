package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    private long id;
    @NotBlank
    @Size(max = 150)
    private String description;
    @NotNull
    private long requesterId;
    @NotNull
    private long receiverId;
}
