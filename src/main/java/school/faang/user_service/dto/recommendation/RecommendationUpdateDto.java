package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RecommendationUpdateDto {

    @NotNull
    private Long id;

    @NotNull
    private Long authorId;

    @NotNull
    private Long receiverId;

    @NotBlank(message = " Content most not be empty! ")
    @Size(max = 4096, message = "Name must be less than 4096 characters")
    private String content;

    private List<SkillOfferUpdateDto> skillOffers;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
