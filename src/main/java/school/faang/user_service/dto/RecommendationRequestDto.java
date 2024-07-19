package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RecommendationRequestDto{
    private Long id;
    private Long requesterId;
    private Long receiverId;

    @NotBlank(message = "Message should not be empty.")
    private String message;
    private String status;
    private List<Long> skillIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
