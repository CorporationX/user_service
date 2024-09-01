package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.Skill;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private long id;
    private String message;
    private int status;
    private List<Long> skillsId;
    private long requesterId;
    private long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
