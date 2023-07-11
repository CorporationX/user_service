package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private int id;
    private String message;
    private String status;
    List<Skill> skills;
    private int requesterId;
    private int recieverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
