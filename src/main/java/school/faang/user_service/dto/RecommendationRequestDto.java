package school.faang.user_service.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecommendationRequestDto {

    private Long id;
    private String message;
    private String status;
    private List<Skill> skills;
    private Long requesterId;
    private Long recieverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Autowired
    public RecommendationRequestDto(Long id, String message, String status, List<Skill> skills, Long requesterId, Long recieverId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.skills = skills;
        this.requesterId = requesterId;
        this.recieverId = recieverId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public Long getRecieverId() {
        return recieverId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
