package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterDto {

    @Size(max = 255)
    private String namePattern;

    @Size(max = 255)
    private String aboutPattern;

    @Size(max = 255)
    private String emailPattern;

    @Size(max = 255)
    private String contactPattern;

    @Size(max = 255)
    private String countryPattern;

    @Size(max = 255)
    private String cityPattern;

    @Size(max = 255)
    private String phonePattern;

    @Size(max = 255)
    private String skillPattern;

    @NotNull
    private int experienceMin;

    @NotNull
    private int experienceMax;
    @Min(1)
    @Max(10)
    private int page;
    @Min(1)
    @Max(20)
    private int pageSize;
}
