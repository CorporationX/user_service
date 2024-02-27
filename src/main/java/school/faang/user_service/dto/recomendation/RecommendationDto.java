package school.faang.user_service.dto.recomendation;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    @NotEmpty(message = "Content не может быть пустым")
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}