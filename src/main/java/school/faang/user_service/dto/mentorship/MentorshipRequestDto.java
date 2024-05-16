package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    private Long id;

    @NotBlank
    @Size(min = 20, max = 4096, message = "description should be more then 19 and less or equal to 4096 symbols")
    private String description;

    @Positive
    @NotNull
    private Long requesterId;

    @Positive
    @NotNull
    private Long receiverId;

    @NotNull
    private RequestStatus status;

    @Size(min = 20, max = 4096, message = "description should be more then 19 and less or equal to 4096 symbols")
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
