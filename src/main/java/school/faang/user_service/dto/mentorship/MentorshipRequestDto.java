package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    private long id;
    private String description;
    @NotNull
    private long requesterId;
    @NotNull
    private long receiverId;
}
