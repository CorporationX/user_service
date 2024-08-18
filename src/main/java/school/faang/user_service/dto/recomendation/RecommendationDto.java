package school.faang.user_service.dto.recomendation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Information about recommendation")
public class RecommendationDto {
    @Schema(description = "Recommendation identifier")
    private long id;
    @Min(value = 1, message = "author id should not be less than 1")
    @Schema(description = "Author identifier")
    private long authorId;
    @Min(value = 1, message = "receiver id should not be less than 1")
    @Schema(description = "Receiver identifier")
    private long receiverId;
    @Size(min = 1, max = 64)
    @Schema(description = "Content")
    private String content;
    @Schema(description = "List skill offers")
    private List<SkillOfferDto> skillOffers;
    @Schema(description = "Date of recommendation creation")
    private LocalDateTime createdAt;
}
