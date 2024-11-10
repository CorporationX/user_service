package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UserFilterDto (
    @NotEmpty String namePattern,
    @NotEmpty String aboutPattern,
    @NotEmpty String emailPattern,
    @NotEmpty String contactPattern,
    @NotEmpty String countryPattern,
    @NotEmpty String cityPattern,
    @NotEmpty String phonePattern,
    @NotEmpty String skillPattern,
    @Positive Integer experienceMin,
    @Positive Integer experienceMax,
    @Positive Integer page,
    @Positive Integer pageSize
) {
}
