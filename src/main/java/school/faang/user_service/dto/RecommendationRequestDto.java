package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;

    @NotBlank
    private String message;

    @NotNull
    @NotEmpty
    private List<Long> skillIds;

    @NotNull
    private Long requesterId;

    @NotNull
    private Long receiverId;

    private RequestStatusDto status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
