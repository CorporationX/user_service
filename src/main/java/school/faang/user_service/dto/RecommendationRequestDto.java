package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationRequestDto {

    private Long id;
    private String message;
    private String status;
    private List<Long> skillsId;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
