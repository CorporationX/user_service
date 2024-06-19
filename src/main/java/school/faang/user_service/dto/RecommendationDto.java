package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    private Long id;
    @NotNull
    @Positive
    private Long authorId;
    @NotNull
    @Positive
    private Long receiverId;
    @NotBlank
    private String content;
    private List<SkillOfferDto> skillOffers;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime createAt;
}
