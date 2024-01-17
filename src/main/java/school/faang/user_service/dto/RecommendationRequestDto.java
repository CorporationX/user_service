package school.faang.user_service.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {

    private Long id;
    private String message;
    private String status;
    private List<SkillRequestDto> skills;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
