package school.faang.user_service.dto.recomendation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecommendationDto {
    private long id;
    @Min(value = 1, message = "author id should not be less than 1")
    private long authorId;
    @Min(value = 1, message = "receiver id should not be less than 1")
    private long receiverId;
    @Size(min = 1, max = 64)
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
